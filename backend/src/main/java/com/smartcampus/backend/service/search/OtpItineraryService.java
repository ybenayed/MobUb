package com.smartcampus.backend.service.search;

import com.smartcampus.backend.dto.search.ItineraryOptionDTO;
import com.smartcampus.backend.dto.search.ItineraryRequestDTO;
import com.smartcampus.backend.dto.search.LegDTO;
import com.smartcampus.backend.dto.search.otp.OtpGraphQlResponseDTO;
import com.smartcampus.backend.dto.search.otp.OtpItineraryRawDTO;
import com.smartcampus.backend.dto.search.otp.OtpLegRawDTO; 
import com.smartcampus.backend.dto.search.GeoPointDTO;
import com.smartcampus.backend.dto.search.otp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OtpItineraryService {

    private static final Logger log = LoggerFactory.getLogger(OtpItineraryService.class);

    // Valeurs de repli SEULEMENT si le mobile n'envoie rien pour ce champ
    private static final int DEFAULT_NUM_ITINERARIES = 3;
    private static final List<String> DEFAULT_MODES = List.of("WALK", "TRANSIT");

    // Noms de modes "conviviaux" acceptes cote API mobile, a traduire vers {mode, qualifier}
    // OTP n'a pas de mode BICYCLE_RENTAL a part entiere : c'est BICYCLE + qualifier RENT
    private static final List<String> BIKE_RENTAL_ALIASES = List.of("BICYCLE_RENTAL", "BICYCLE_RENT", "VCUB");

    private static final String PLAN_QUERY = """
            query Plan(
              $fromLat: Float!, $fromLon: Float!,
              $toLat: Float!, $toLon: Float!,
              $numItineraries: Int!,
              $transportModes: [TransportMode],
              $date: String, $time: String, $arriveBy: Boolean,
              $walkSpeed: Float, $bikeSpeed: Float, $wheelchair: Boolean
            ) {
              plan(
                from: { lat: $fromLat, lon: $fromLon }
                to: { lat: $toLat, lon: $toLon }
                numItineraries: $numItineraries
                transportModes: $transportModes
                date: $date
                time: $time
                arriveBy: $arriveBy
                walkSpeed: $walkSpeed
                bikeSpeed: $bikeSpeed
                wheelchair: $wheelchair
              ) {
                    itineraries {
                    duration
                    legs {
                        mode
                        startTime
                        endTime
                        from { name lat lon }
                        to { name lat lon }
                        route { shortName longName }
                        legGeometry { points }
                    }
                    }
              }
            }
            """;

    private final RestTemplate otpRestTemplate;
    private final String otpBaseUrl;

    public OtpItineraryService(
            @Qualifier("otpRestTemplate") RestTemplate otpRestTemplate,
            @Value("${otp.base-url}") String otpBaseUrl) {
        this.otpRestTemplate = otpRestTemplate;
        this.otpBaseUrl = otpBaseUrl;
    }

    public List<ItineraryOptionDTO> computeItinerary(ItineraryRequestDTO request) {

        List<String> modes = (request.getModes() != null && !request.getModes().isEmpty())
                ? request.getModes()
                : DEFAULT_MODES;

        int numItineraries = (request.getNumItineraries() != null && request.getNumItineraries() > 0)
                ? request.getNumItineraries()
                : DEFAULT_NUM_ITINERARIES;

        List<Map<String, String>> transportModes = buildTransportModes(modes);

        Map<String, Object> variables = new HashMap<>();
        variables.put("fromLat", request.getOrigin().getLatitude());
        variables.put("fromLon", request.getOrigin().getLongitude());
        variables.put("toLat", request.getDestination().getLatitude());
        variables.put("toLon", request.getDestination().getLongitude());
        variables.put("numItineraries", numItineraries);
        variables.put("transportModes", transportModes);
        variables.put("date", request.getDate());             // null accepte -> OTP utilise "maintenant"
        variables.put("time", request.getTime());             // null accepte
        variables.put("arriveBy", request.getArriveBy());     // null accepte -> false par defaut cote OTP
        variables.put("walkSpeed", request.getWalkSpeed());   // null accepte -> valeur de router-config.json
        variables.put("bikeSpeed", request.getBikeSpeed());   // null accepte
        variables.put("wheelchair", request.getWheelchair()); // null accepte -> false

        Map<String, Object> body = new HashMap<>();
        body.put("query", PLAN_QUERY);
        body.put("variables", variables);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        String url = otpBaseUrl + "/index/graphql";

        log.info("Requete OTP -> modes={} transportModes={} numItineraries={} date={} time={} arriveBy={}",
                modes, transportModes, numItineraries, request.getDate(), request.getTime(), request.getArriveBy());

        OtpGraphQlResponseDTO response;
        try {
            response = otpRestTemplate.postForObject(url, entity, OtpGraphQlResponseDTO.class);
        } catch (Exception e) {
            log.error("Erreur lors de l'appel a OTP ({}) : {}", url, e.getMessage());
            return List.of();
        }

        if (response == null || response.getData() == null
                || response.getData().getPlan() == null
                || response.getData().getPlan().getItineraries() == null) {
            log.warn("Reponse OTP vide ou incomplete pour {} -> {}", url, variables);
            return List.of();
        }

        List<ItineraryOptionDTO> results = new ArrayList<>();
        for (OtpItineraryRawDTO raw : response.getData().getPlan().getItineraries()) {
            results.add(toItineraryOptionDTO(raw));
        }
        return results;
    }

    /**
     * Traduit une liste de modes "conviviaux" (cote API mobile) vers la structure
     * TransportMode attendue par OTP : { mode: Mode, qualifier: Qualifier }.
     * OTP n'a pas de mode "BICYCLE_RENTAL" separe : le velo en libre-service
     * est le mode BICYCLE avec le qualifier RENT.
     */
    private List<Map<String, String>> buildTransportModes(List<String> modes) {
        List<Map<String, String>> transportModes = new ArrayList<>();
        for (String mode : modes) {
            Map<String, String> entry = new HashMap<>();
            if (BIKE_RENTAL_ALIASES.contains(mode)) {
                entry.put("mode", "BICYCLE");
                entry.put("qualifier", "RENT");
            } else {
                entry.put("mode", mode);
            }
            transportModes.add(entry);
        }
        return transportModes;
    }

    private ItineraryOptionDTO toItineraryOptionDTO(OtpItineraryRawDTO raw) {
        List<LegDTO> legs = new ArrayList<>();
        if (raw.getLegs() != null) {
            for (OtpLegRawDTO leg : raw.getLegs()) {
                String routeName = leg.getRoute() != null ? leg.getRoute().getShortName() : null;

                List<GeoPointDTO> geometry = decodePolyline(
                        leg.getLegGeometry() != null ? leg.getLegGeometry().getPoints() : null
                );

                // Filet de sécurité : si OTP ne renvoie pas de géométrie, on retombe
                // sur une ligne droite from -> to (mieux que rien).
                if (geometry.isEmpty() && leg.getFrom() != null && leg.getTo() != null) {
                    geometry = List.of(
                            new GeoPointDTO(leg.getFrom().getLat(), leg.getFrom().getLon()),
                            new GeoPointDTO(leg.getTo().getLat(), leg.getTo().getLon())
                    );
                }

                legs.add(new LegDTO(
                        leg.getMode(),
                        leg.getFrom() != null ? leg.getFrom().getName() : null,
                        leg.getTo() != null ? leg.getTo().getName() : null,
                        leg.getStartTime(),
                        leg.getEndTime(),
                        routeName,
                        geometry
                ));
            }
        }
        return new ItineraryOptionDTO(raw.getDuration(), legs);
    }

    /**
     * Decode l'encoded polyline renvoye par OTP (Google Encoded Polyline Algorithm, precision 5).
     * Voir : https://developers.google.com/maps/documentation/utilities/polylinealgorithm
     */
    private List<GeoPointDTO> decodePolyline(String encoded) {
        List<GeoPointDTO> points = new ArrayList<>();
        if (encoded == null || encoded.isEmpty()) {
            return points;
        }

        int index = 0, len = encoded.length();
        int lat = 0, lon = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int deltaLat = ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
            lat += deltaLat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int deltaLon = ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
            lon += deltaLon;

            points.add(new GeoPointDTO(lat / 1e5, lon / 1e5));
        }
        return points;
    }
}