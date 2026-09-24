package com.smartcampus.backend.service.search;

import com.smartcampus.backend.dto.search.GeoPointDTO;
import com.smartcampus.backend.dto.search.ItineraryOptionDTO;
import com.smartcampus.backend.dto.search.ItineraryRequestDTO;
import com.smartcampus.backend.dto.search.LegDTO;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * IMPORTANT : cette instance OTP tourne sur la nouvelle API GraphQL "GTFS GraphQL v2"
 * (racine "planConnection", pagination Relay edges/node, inputs imbriques), et NON
 * sur l'ancienne API "index/graphql -> plan { itineraries }". Toute la structure de
 * requete a ete construite par introspection reelle du schema (__schema/__type),
 * pas par supposition. Voir la conversation de mise au point pour le detail des
 * champs verifies : PlanLabeledLocationInput, PlanModesInput, PlanPreferencesInput,
 * PlanDateTimeInput, Itinerary, Leg, LegTime.
 */
@Service
public class OtpItineraryService {

    private static final Logger log = LoggerFactory.getLogger(OtpItineraryService.class);

    private static final ZoneId PARIS_ZONE = ZoneId.of("Europe/Paris");

    // Le frontend ne choisit plus le nombre d'itineraires : on demande toujours
    // un plafond haut a OTP (argument de pagination "first") et on renvoie tout.
    private static final int MAX_ITINERARIES_REQUESTED = 15;

    private static final List<String> DEFAULT_MODES = List.of("WALK", "TRANSIT");

    // Noms de modes "conviviaux" acceptes cote API mobile pour le velo en libre-service.
    // Traduits vers PlanAccessMode/PlanEgressMode "BICYCLE_RENTAL".
    private static final List<String> BIKE_RENTAL_ALIASES = List.of("BICYCLE_RENTAL", "BICYCLE_RENT", "VCUB");

    // Modes de vehicules de transport en commun proposes par defaut quand TRANSIT est demande.
    // Valeurs verifiees dans l'enum TransitMode du schema OTP (liste plus large disponible
    // si besoin plus tard : AIRPLANE, CABLE_CAR, CARPOOL, COACH, FUNICULAR, GONDOLA,
    // MONORAIL, SNOW_AND_ICE, TAXI, TROLLEYBUS).
    private static final List<String> DEFAULT_TRANSIT_VEHICLE_MODES =
            List.of("BUS", "TRAM", "RAIL", "SUBWAY", "FERRY");

    // Profils de calcul d'itineraire velo, valeurs reelles de l'enum CyclingOptimizationType
    // (verifie par introspection : seulement 3 valeurs, pas de "GREENWAYS" dans ce schema).
    private static final Map<String, String> BICYCLE_PROFILES = Map.of(
            "SHORTEST_DURATION", "Vélo — le plus rapide",
            "SAFE_STREETS", "Vélo — itinéraire sûr",
            "FLAT_STREETS", "Vélo — le plus plat"
    );

    // Facteurs de secours (g CO2 / km / passager, valeurs ADEME indicatives, a ajuster
    // a la metropole si besoin). N'est utilise que si emissionsPerPerson.co2 est absent
    // ou nul cote OTP (config GTFS incomplete) : voir fallbackCo2Grams(). Mode inconnu
    // ou non liste ici => considere comme non emetteur par defaut (0.0), a completer
    // si un mode motorise supplementaire apparait (FERRY, CABLE_CAR, etc.).
    private static final Map<String, Double> CO2_FACTORS_G_PER_KM = Map.of(
            "WALK", 0.0,
            "BICYCLE", 0.0, // couvre aussi le velo en libre-service (rentedBike=true, mode reste "BICYCLE")
            "BUS", 103.0,
            "TRAM", 4.0,
            "SUBWAY", 4.0,
            "RAIL", 30.0,
            "TRAIN", 30.0,
            "CAR", 193.0
    );

    private static final String PLAN_QUERY = """
            query Plan(
              $origin: PlanLabeledLocationInput!,
              $destination: PlanLabeledLocationInput!,
              $numItineraries: Int!,
              $modes: PlanModesInput,
              $dateTime: PlanDateTimeInput,
              $preferences: PlanPreferencesInput
            ) {
              planConnection(
                origin: $origin
                destination: $destination
                first: $numItineraries
                modes: $modes
                dateTime: $dateTime
                preferences: $preferences
              ) {
                edges {
                  node {
                    duration
                    numberOfTransfers
                    walkDistance
                    accessibilityScore
                    emissionsPerPerson { co2 }
                    legs {
                      mode
                      distance
                      rentedBike
                      from { name lat lon }
                      to { name lat lon }
                      route { shortName longName }
                      legGeometry { points }
                      start { scheduledTime }
                      end { scheduledTime }
                    }
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

    // ------------------------------------------------------------------
    // API publique du service, utilisee par le controller
    // ------------------------------------------------------------------

    public List<ItineraryOptionDTO> computeItinerary(ItineraryRequestDTO request) {
        List<ItineraryOptionDTO> options = fetchAndEnrich(request, null);
        options.sort(ItinerarySortCriterion.FASTEST.comparator());
        return options;
    }

    public List<ItineraryOptionDTO> computeItinerarySorted(ItineraryRequestDTO request, ItinerarySortCriterion criterion) {
        List<ItineraryOptionDTO> options = fetchAndEnrich(request, null);
        options.sort(criterion.comparator());
        return options;
    }

    public int countItineraries(ItineraryRequestDTO request) {
        return fetchAndEnrich(request, null).size();
    }

    /**
     * Variante PMR : force wheelchair.enabled=true, puis trie par accessibilityScore
     * (score natif OTP, 0=non accessible a 1=accessible), depart age a duree egale.
     */
    public List<ItineraryOptionDTO> computeAccessibleItinerary(ItineraryRequestDTO request) {
        request.setWheelchair(true);
        List<ItineraryOptionDTO> options = fetchAndEnrich(request, null);
        options.sort(ItinerarySortCriterion.ACCESSIBILITY.comparator());
        return options;
    }

    /**
     * Plusieurs itineraires velo perso distincts, un par profil OTP
     * (rapide / sur / plat), etiquetes via profileLabel. Necessaire car OTP
     * ne varie pas le trajet BICYCLE tout seul avec la pagination "first".
     */
    public List<ItineraryOptionDTO> computeBicycleItineraries(ItineraryRequestDTO request) {
        List<ItineraryOptionDTO> merged = new ArrayList<>();
        Set<String> seenSignatures = new LinkedHashSet<>();

        for (Map.Entry<String, String> profile : BICYCLE_PROFILES.entrySet()) {
            List<ItineraryOptionDTO> options = fetchAndEnrich(request, profile.getKey());
            for (ItineraryOptionDTO option : options) {
                String signature = signatureOf(option);
                if (seenSignatures.add(signature)) {
                    option.setProfileLabel(profile.getValue());
                    merged.add(option);
                }
            }
        }
        merged.sort(ItinerarySortCriterion.FASTEST.comparator());
        return merged;
    }

    // Coeur : un seul point d'appel OTP, reutilise par toutes les methodes ci-dessus

    private List<ItineraryOptionDTO> fetchAndEnrich(ItineraryRequestDTO request, String cyclingOptimizationType) {
        List<String> modes = (request.getModes() != null && !request.getModes().isEmpty())
                ? request.getModes()
                : DEFAULT_MODES;

        Map<String, Object> variables = new HashMap<>();
        variables.put("origin", locationVariable(request.getOrigin().getLatitude(), request.getOrigin().getLongitude()));
        variables.put("destination", locationVariable(request.getDestination().getLatitude(), request.getDestination().getLongitude()));
        variables.put("numItineraries", MAX_ITINERARIES_REQUESTED);
        variables.put("modes", buildModesVariable(modes));

        Map<String, Object> dateTimeVar = buildDateTimeVariable(request);
        if (dateTimeVar != null) {
            variables.put("dateTime", dateTimeVar);
        }

        Map<String, Object> preferencesVar = buildPreferencesVariable(request, cyclingOptimizationType);
        if (preferencesVar != null) {
            variables.put("preferences", preferencesVar);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("query", PLAN_QUERY);
        body.put("variables", variables);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        String url = otpBaseUrl + "/index/graphql";

        log.info("Requete OTP -> modes={} cyclingOptimizationType={} variables={}",
                modes, cyclingOptimizationType, variables);

        OtpGraphQlResponseDTO response;
        try {
            response = otpRestTemplate.postForObject(url, entity, OtpGraphQlResponseDTO.class);
        } catch (Exception e) {
            log.error("Erreur lors de l'appel a OTP ({}) : {}", url, e.getMessage());
            return new ArrayList<>();
        }

        if (response == null || response.getData() == null
                || response.getData().getPlanConnection() == null
                || response.getData().getPlanConnection().getEdges() == null) {
            log.warn("Reponse OTP vide ou incomplete pour {} -> {}", url, variables);
            return new ArrayList<>();
        }

        List<ItineraryOptionDTO> results = new ArrayList<>();
        for (OtpPlanEdgeRawDTO edge : response.getData().getPlanConnection().getEdges()) {
            if (edge.getNode() != null) {
                results.add(toItineraryOptionDTO(edge.getNode()));
            }
        }
        return results;
    }

    // Construction des variables GraphQL (structure imbriquee du nouveau schema)

    private Map<String, Object> locationVariable(double lat, double lon) {
        return Map.of("location", Map.of("coordinate", Map.of(
                "latitude", lat,
                "longitude", lon
        )));
    }

    /**
     * Traduit les modes "conviviaux" de l'app mobile vers la structure PlanModesInput.
     * - Si TRANSIT est demande : on utilise modes.transit.{access,egress,transfer,transit}
     *   (WALK garanti en access/egress/transfer sauf si vélo précisé), transitOnly=true.
     * - Sinon : on utilise modes.direct (trajet complet sans transport en commun),
     *   directOnly=true, avec WALK toujours inclus en secours.
     */
    private Map<String, Object> buildModesVariable(List<String> userModes) {
        List<String> upper = userModes.stream().map(String::toUpperCase).toList();
        boolean wantsTransit = upper.contains("TRANSIT");
        boolean wantsBikeRental = upper.stream().anyMatch(BIKE_RENTAL_ALIASES::contains);
        boolean wantsBicycle = upper.contains("BICYCLE");
        boolean wantsCar = upper.contains("CAR");

        Map<String, Object> modes = new HashMap<>();

        if (wantsTransit) {
            String accessMode = wantsBikeRental ? "BICYCLE_RENTAL" : (wantsBicycle ? "BICYCLE" : "WALK");
            String egressMode = wantsBikeRental ? "BICYCLE_RENTAL" : "WALK";

            List<Map<String, String>> transitVehicleModes = new ArrayList<>();
            for (String vehicleMode : DEFAULT_TRANSIT_VEHICLE_MODES) {
                transitVehicleModes.add(Map.of("mode", vehicleMode));
            }

            Map<String, Object> transit = new HashMap<>();
            transit.put("access", List.of(accessMode));
            transit.put("egress", List.of(egressMode));
            transit.put("transfer", List.of("WALK"));
            transit.put("transit", transitVehicleModes);

            modes.put("transitOnly", true);
            modes.put("transit", transit);
        } else {
            List<String> direct = new ArrayList<>();
            direct.add("WALK"); // toujours inclus en secours
            if (wantsBicycle) direct.add("BICYCLE");
            if (wantsBikeRental) direct.add("BICYCLE_RENTAL");
            if (wantsCar) direct.add("CAR");

            modes.put("directOnly", true);
            modes.put("direct", direct.stream().distinct().toList());
        }

        return modes;
    }

    /** null si date/heure absentes ("maintenant" -> OTP utilise l'heure courante par defaut). */
    private Map<String, Object> buildDateTimeVariable(ItineraryRequestDTO request) {
        if (request.getDate() == null && request.getTime() == null) {
            return null;
        }
        LocalDate date = request.getDate() != null ? LocalDate.parse(request.getDate()) : LocalDate.now(PARIS_ZONE);
        LocalTime time = request.getTime() != null ? LocalTime.parse(request.getTime()) : LocalTime.now(PARIS_ZONE);
        String iso = ZonedDateTime.of(date, time, PARIS_ZONE).toOffsetDateTime().toString();

        boolean arriveBy = Boolean.TRUE.equals(request.getArriveBy());
        return arriveBy ? Map.of("latestArrival", iso) : Map.of("earliestDeparture", iso);
    }

    /** null si aucune preference n'est definie (evite d'envoyer un objet vide inutilement). */
    private Map<String, Object> buildPreferencesVariable(ItineraryRequestDTO request, String cyclingOptimizationType) {
        Map<String, Object> preferences = new HashMap<>();

        if (Boolean.TRUE.equals(request.getWheelchair())) {
            preferences.put("accessibility", Map.of("wheelchair", Map.of("enabled", true)));
        }

        Map<String, Object> street = new HashMap<>();
        if (request.getWalkSpeed() != null) {
            street.put("walk", Map.of("speed", request.getWalkSpeed()));
        }

        Map<String, Object> bicycle = new HashMap<>();
        if (request.getBikeSpeed() != null) {
            bicycle.put("speed", request.getBikeSpeed());
        }
        if (cyclingOptimizationType != null) {
            bicycle.put("optimization", Map.of("type", cyclingOptimizationType));
        }
        if (!bicycle.isEmpty()) {
            street.put("bicycle", bicycle);
        }

        if (!street.isEmpty()) {
            preferences.put("street", street);
        }

        return preferences.isEmpty() ? null : preferences;
    }

    
    private ItineraryOptionDTO toItineraryOptionDTO(OtpItineraryRawDTO raw) {
        List<LegDTO> legs = new ArrayList<>();
        if (raw.getLegs() != null) {
            for (OtpLegRawDTO leg : raw.getLegs()) {
                String routeName = leg.getRoute() != null ? leg.getRoute().getShortName() : null;

                List<GeoPointDTO> geometry = decodePolyline(
                        leg.getLegGeometry() != null ? leg.getLegGeometry().getPoints() : null
                );
                if (geometry.isEmpty() && leg.getFrom() != null && leg.getTo() != null) {
                    geometry = List.of(
                            new GeoPointDTO(leg.getFrom().getLat(), leg.getFrom().getLon()),
                            new GeoPointDTO(leg.getTo().getLat(), leg.getTo().getLon())
                    );
                }

                legs.add(new LegDTO(
                    leg.getMode(),
                    leg.getFrom() != null ? leg.getFrom().getName() : null,
                    leg.getFrom() != null ? leg.getFrom().getLat() : 0.0,
                    leg.getFrom() != null ? leg.getFrom().getLon() : 0.0,
                    leg.getTo() != null ? leg.getTo().getName() : null,
                    leg.getTo() != null ? leg.getTo().getLat() : 0.0,
                    leg.getTo() != null ? leg.getTo().getLon() : 0.0,
                    parseEpochMillis(leg.getStart()),
                    parseEpochMillis(leg.getEnd()),
                    leg.getDistance() != null ? leg.getDistance() : 0.0,
                    Boolean.TRUE.equals(leg.getRentedBike()),
                    routeName,
                    geometry
            ));
            }
        }

        double co2 = (raw.getEmissionsPerPerson() != null && raw.getEmissionsPerPerson().getCo2() != null)
                ? raw.getEmissionsPerPerson().getCo2() : 0.0;
        if (co2 == 0.0) {
           
            co2 = fallbackCo2Grams(legs);
        }
        double accessibility = raw.getAccessibilityScore() != null ? raw.getAccessibilityScore() : 0.0;
        double walkDistance = raw.getWalkDistance() != null ? raw.getWalkDistance() : 0.0;

        return new ItineraryOptionDTO(
                raw.getDuration(),
                raw.getNumberOfTransfers(),
                walkDistance,
                co2,
                accessibility,
                null, 
                legs
        );
    }

    private long parseEpochMillis(OtpLegTimeRawDTO legTime) {
        if (legTime == null || legTime.getScheduledTime() == null) {
            return 0L;
        }
        try {
            return OffsetDateTime.parse(legTime.getScheduledTime()).toInstant().toEpochMilli();
        } catch (Exception e) {
            log.warn("Impossible de parser l'heure de leg OTP : {}", legTime.getScheduledTime());
            return 0L;
        }
    }

 
    private double fallbackCo2Grams(List<LegDTO> legs) {
        if (legs == null || legs.isEmpty()) {
            return 0.0;
        }
        double totalGrams = 0.0;
        for (LegDTO leg : legs) {
            double distanceKm = leg.getDistance() / 1000.0;
            String mode = leg.getMode() != null ? leg.getMode().toUpperCase() : "";
            double factor = CO2_FACTORS_G_PER_KM.getOrDefault(mode, 0.0);
            totalGrams += distanceKm * factor;
        }
        return totalGrams;
    }

    private String signatureOf(ItineraryOptionDTO option) {
        StringBuilder sb = new StringBuilder();
        sb.append(option.getDuration()).append('|');
        if (option.getLegs() != null) {
            for (LegDTO leg : option.getLegs()) {
                sb.append(leg.getMode()).append(':').append(leg.getStartTime()).append(':').append(leg.getEndTime()).append(';');
            }
        }
        return sb.toString();
    }

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