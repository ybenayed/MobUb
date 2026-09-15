package com.smartcampus.backend.service.parking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.parking.*;
import com.smartcampus.backend.entity.parking.Parking;
import com.smartcampus.backend.exception.ResourceNotFoundException;
import com.smartcampus.backend.mapper.parking.ParkingMapper;
import com.smartcampus.backend.repository.parking.ParkingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingService {

    private final ParkingRepository parkingRepository;
    private final ParkingMapper parkingMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private final ParkingRealtimeCache realtimeCache;
    private final ParkingRealtimeService realtimeService;

    private static final String BASE_URL =
        "https://opendata.bordeaux-metropole.fr/api/explore/v2.1/catalog/datasets/st_park_p/records";

    private static final long FRAICHEUR_SEUIL_SECONDES = 300; // 5 min

    public List<ParkingDTO> getAllParking() {
        return parkingRepository.findAll().stream().map(parkingMapper::toDTO).toList();
    }

    public ParkingDTO getParkingById(Long id) {
        return parkingRepository.findById(id)
                .map(parkingMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Parking introuvable : " + id));
    }

    public int importParkingFromApi() {
        int imported = 0;
        int offset = 0;
        int totalCount;

        do {
            String url = BASE_URL + "?limit=100&offset=" + offset;
            try {
                String rawJson = restTemplate.getForObject(url, String.class);
                JsonNode root = objectMapper.readTree(rawJson);
                totalCount = root.path("total_count").asInt(0);

                for (JsonNode record : root.path("results")) {
                    String ident = record.path("ident").asText(null);
                    if (ident == null || parkingRepository.existsByIdent(ident)) {
                        continue; 
                    }

                    Parking parking = buildParkingFromRecord(record);
                    parkingRepository.save(parking);
                    imported++;
                }
            } catch (Exception e) {
                throw new RuntimeException("Erreur import parking offset=" + offset, e);
            }
            offset += 100;
        } while (offset < totalCount);

        log.info("Import parking terminé : {} nouveaux parkings", imported);
        return imported;
    }

    public List<ParkingPositionDTO> getAllPositions() {
        return parkingRepository.findAllPositions();
    }

    public List<ParkingPositionDTO> getPositionsByType(String taType) {
        return parkingRepository.findPositionsByTaType(taType);
    }

    public List<ParkingCountDTO> getCountByType() {
        return parkingRepository.countGroupedByTaType().stream()
                .map(row -> new ParkingCountDTO((String) row[0], (Long) row[1]))
                .toList();
    }

    public List<ParkingStatusDTO> getAllWithStatus() {
        return parkingRepository.findAll().stream()
                .map(this::mergeWithDynamic)
                .toList();
    }

    public ParkingStatusDTO getStatusById(Long id) {
        Parking parking = parkingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parking introuvable : " + id));
        return mergeWithDynamic(parking);
    }

    private ParkingStatusDTO mergeWithDynamic(Parking parking) {
        ParkingDynamicDTO dyn = realtimeCache.get(parking.getIdent());

        boolean tropVieux = dyn == null || dyn.getFetchedAt() == null
                || dyn.getFetchedAt().isBefore(OffsetDateTime.now().minusSeconds(FRAICHEUR_SEUIL_SECONDES));

        if (tropVieux) {
            ParkingDynamicDTO frais = realtimeService.fetchLive(parking.getIdent());
            if (frais != null) {
                dyn = frais;
            }
        }

        boolean fraiche = dyn != null && dyn.getFetchedAt() != null
                && dyn.getFetchedAt().isAfter(OffsetDateTime.now().minusSeconds(FRAICHEUR_SEUIL_SECONDES));

        ParkingStatusDTO dto = parkingMapper.toStatusDTO(parking);
        if (dyn != null) {
            dto.setEtat(dyn.getEtat());
            dto.setLibre(dyn.getLibre());
            dto.setTotalTempsReel(dyn.getTotalTempsReel());
            dto.setConnecte(dyn.getConnecte());
            dto.setMdate(dyn.getMdate());
        }
        dto.setDataFraiche(fraiche);
        return dto;
    }

    private Parking buildParkingFromRecord(JsonNode record) {
        JsonNode geo = record.path("geo_point_2d");
        Double lat = geo.hasNonNull("lat") ? geo.get("lat").asDouble() : null;
        Double lon = geo.hasNonNull("lon") ? geo.get("lon").asDouble() : null;

        Point location = null;
        if (lat != null && lon != null) {
            location = geometryFactory.createPoint(new Coordinate(lon, lat));
        }

        return Parking.builder()
                .ident(record.path("ident").asText(null))
                .nom(record.path("nom").asText(null))
                .adresse(record.path("adresse").asText(null))
                .exploit(record.path("exploit").asText(null))
                .infor(record.path("infor").asText(null))
                .taType(record.path("ta_type").asText(null))
                .type(record.path("type").asText(null))
                .latitude(lat)
                .longitude(lon)
                .location(location)
                .npTotal(getInt(record, "np_total"))
                .npGlobal(getInt(record, "np_global"))
                .npPmr(getInt(record, "np_pmr"))
                .npVle(getInt(record, "np_vle"))
                .npVeltot(getInt(record, "np_veltot"))
                .npVelec(getInt(record, "np_velec"))
                .np2rmot(getInt(record, "np_2rmot"))
                .npCovoit(getInt(record, "np_covoit"))
                .thQuar(getDouble(record, "th_quar"))
                .thDemi(getDouble(record, "th_demi"))
                .thHeur(getDouble(record, "th_heur"))
                .th2(getDouble(record, "th_2"))
                .th3(getDouble(record, "th_3"))
                .th4(getDouble(record, "th_4"))
                .th10(getDouble(record, "th_10"))
                .th24(getDouble(record, "th_24"))
                .thNuit(getDouble(record, "th_nuit"))
                .taTitul(getDouble(record, "ta_titul"))
                .taNtitul(getDouble(record, "ta_ntitul"))
                .taResmoi(getDouble(record, "ta_resmoi"))
                .taNres7j(getDouble(record, "ta_nres7j"))
                .taMoimot(getDouble(record, "ta_moimot"))
                .taMoivel(getDouble(record, "ta_moivel"))
                .taHandi(record.path("ta_handi").asText(null))
                .anServ(record.path("an_serv").asText(null))
                .secteur(record.path("secteur").asText(null))
                .propr(record.path("propr").asText(null))
                .typgest(record.path("typgest").asText(null))
                .nbNiv(getInt(record, "nb_niv"))
                .gabariStd(getDouble(record, "gabari_std"))
                .gabariMax(getDouble(record, "gabari_max"))
                .url(record.path("url").asText(null))
                .build();
    }

    private Integer getInt(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asInt() : null;
    }

    private Double getDouble(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asDouble() : null;
    }

    public ParkingDTO createParking(ParkingRequestDTO request) {
    if (parkingRepository.existsByIdent(request.getIdent())) {
        throw new IllegalArgumentException("Un parking avec cet identifiant existe deja : " + request.getIdent());
    }
    Parking parking = applyRequest(new Parking(), request);
    return parkingMapper.toDTO(parkingRepository.save(parking));
}

public ParkingDTO updateParking(Long id, ParkingRequestDTO request) {
    Parking parking = parkingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Parking introuvable : " + id));
    applyRequest(parking, request);
    return parkingMapper.toDTO(parkingRepository.save(parking));
}

public void deleteParking(Long id) {
    if (!parkingRepository.existsById(id)) {
        throw new ResourceNotFoundException("Parking introuvable : " + id);
    }
    parkingRepository.deleteById(id);
}

private Parking applyRequest(Parking p, ParkingRequestDTO r) {
    Point location = (r.getLatitude() != null && r.getLongitude() != null)
            ? geometryFactory.createPoint(new Coordinate(r.getLongitude(), r.getLatitude()))
            : null;

    p.setIdent(r.getIdent());
    p.setNom(r.getNom());
    p.setAdresse(r.getAdresse());
    p.setExploit(r.getExploit());
    p.setInfor(r.getInfor());
    p.setTaType(r.getTaType());
    p.setType(r.getType());
    p.setLatitude(r.getLatitude());
    p.setLongitude(r.getLongitude());
    p.setLocation(location);
    p.setNpTotal(r.getNpTotal());
    p.setNpGlobal(r.getNpGlobal());
    p.setNpPmr(r.getNpPmr());
    p.setNpVle(r.getNpVle());
    p.setNpVeltot(r.getNpVeltot());
    p.setNpVelec(r.getNpVelec());
    p.setNp2rmot(r.getNp2rmot());
    p.setNpCovoit(r.getNpCovoit());
    p.setThQuar(r.getThQuar());
    p.setThDemi(r.getThDemi());
    p.setThHeur(r.getThHeur());
    p.setTh2(r.getTh2());
    p.setTh3(r.getTh3());
    p.setTh4(r.getTh4());
    p.setTh10(r.getTh10());
    p.setTh24(r.getTh24());
    p.setThNuit(r.getThNuit());
    p.setTaTitul(r.getTaTitul());
    p.setTaNtitul(r.getTaNtitul());
    p.setTaResmoi(r.getTaResmoi());
    p.setTaNres7j(r.getTaNres7j());
    p.setTaMoimot(r.getTaMoimot());
    p.setTaMoivel(r.getTaMoivel());
    p.setTaHandi(r.getTaHandi());
    p.setAnServ(r.getAnServ());
    p.setSecteur(r.getSecteur());
    p.setPropr(r.getPropr());
    p.setTypgest(r.getTypgest());
    p.setNbNiv(r.getNbNiv());
    p.setGabariStd(r.getGabariStd());
    p.setGabariMax(r.getGabariMax());
    p.setUrl(r.getUrl());
    return p;
}
}