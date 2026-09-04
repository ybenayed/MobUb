package com.smartcampus.backend.controller.search;

import com.smartcampus.backend.dto.search.ItineraryOptionDTO;
import com.smartcampus.backend.dto.search.ItineraryRequestDTO;
import com.smartcampus.backend.service.search.ItinerarySortCriterion;
import com.smartcampus.backend.service.search.OtpItineraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ItineraryController {

    private static final Logger log = LoggerFactory.getLogger(ItineraryController.class);

    private final OtpItineraryService otpItineraryService;

    public ItineraryController(OtpItineraryService otpItineraryService) {
        this.otpItineraryService = otpItineraryService;
    }

    /** Comportement par defaut : toutes les options, triees par duree. */
    @PostMapping("/itinerary")
    public ResponseEntity<?> computeItinerary(@RequestBody ItineraryRequestDTO request) {
        return respond(request, () -> otpItineraryService.computeItinerary(request));
    }

    @PostMapping("/itinerary/fastest")
    public ResponseEntity<?> fastest(@RequestBody ItineraryRequestDTO request) {
        return respond(request, () -> otpItineraryService.computeItinerarySorted(request, ItinerarySortCriterion.FASTEST));
    }

    @PostMapping("/itinerary/least-walking")
    public ResponseEntity<?> leastWalking(@RequestBody ItineraryRequestDTO request) {
        return respond(request, () -> otpItineraryService.computeItinerarySorted(request, ItinerarySortCriterion.LEAST_WALKING));
    }

    @PostMapping("/itinerary/fewest-transfers")
    public ResponseEntity<?> fewestTransfers(@RequestBody ItineraryRequestDTO request) {
        return respond(request, () -> otpItineraryService.computeItinerarySorted(request, ItinerarySortCriterion.FEWEST_TRANSFERS));
    }

    @PostMapping("/itinerary/eco")
    public ResponseEntity<?> eco(@RequestBody ItineraryRequestDTO request) {
        return respond(request, () -> otpItineraryService.computeItinerarySorted(request, ItinerarySortCriterion.ECO_FRIENDLY));
    }

    @PostMapping("/itinerary/accessible")
    public ResponseEntity<?> accessible(@RequestBody ItineraryRequestDTO request) {
        return respond(request, () -> otpItineraryService.computeAccessibleItinerary(request));
    }

    @PostMapping("/itinerary/bicycle")
    public ResponseEntity<?> bicycle(@RequestBody ItineraryRequestDTO request) {
        return respond(request, () -> otpItineraryService.computeBicycleItineraries(request));
    }

    /** Utilitaire de test rapide : juste le nombre d'itineraires proposes par OTP. */
    @PostMapping("/itinerary/count")
    public ResponseEntity<?> count(@RequestBody ItineraryRequestDTO request) {
        if (request.getOrigin() == null || request.getDestination() == null) {
            return ResponseEntity.badRequest().body("origin et destination sont obligatoires");
        }
        int count = otpItineraryService.countItineraries(request);
        return ResponseEntity.ok(Map.of("count", count));
    }

    // ------------------------------------------------------------------

    private interface ItinerarySupplier {
        List<ItineraryOptionDTO> get();
    }

    private ResponseEntity<?> respond(ItineraryRequestDTO request, ItinerarySupplier supplier) {
        if (request.getOrigin() == null || request.getDestination() == null) {
            return ResponseEntity.badRequest().body("origin et destination sont obligatoires");
        }

        log.info("=== Calcul d'itineraire ===");
        log.info("Origine      : {} | lat={} lon={}",
                request.getOrigin().getName(),
                request.getOrigin().getLatitude(),
                request.getOrigin().getLongitude());
        log.info("Destination  : {} | lat={} lon={}",
                request.getDestination().getName(),
                request.getDestination().getLatitude(),
                request.getDestination().getLongitude());

        List<ItineraryOptionDTO> itineraries = supplier.get();

        if (itineraries.isEmpty()) {
            log.warn("Aucun itineraire trouve entre {} et {}",
                    request.getOrigin().getName(), request.getDestination().getName());
            return ResponseEntity.noContent().build();
        }

        log.info("{} itineraire(s) trouve(s)", itineraries.size());
        return ResponseEntity.ok(itineraries);
    }
}