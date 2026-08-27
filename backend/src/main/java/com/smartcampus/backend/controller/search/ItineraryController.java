package com.smartcampus.backend.controller.search;

import com.smartcampus.backend.dto.search.ItineraryOptionDTO;
import com.smartcampus.backend.dto.search.ItineraryRequestDTO;
import com.smartcampus.backend.service.search.OtpItineraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItineraryController {

    private static final Logger log = LoggerFactory.getLogger(ItineraryController.class);

    private final OtpItineraryService otpItineraryService;

    public ItineraryController(OtpItineraryService otpItineraryService) {
        this.otpItineraryService = otpItineraryService;
    }

    @PostMapping("/itinerary")
    public ResponseEntity<List<ItineraryOptionDTO>> computeItinerary(@RequestBody ItineraryRequestDTO request) {
        log.info("=== Calcul d'itineraire ===");
        log.info("Origine      : {} | lat={} lon={}",
                request.getOrigin().getName(),
                request.getOrigin().getLatitude(),
                request.getOrigin().getLongitude());
        log.info("Destination  : {} | lat={} lon={}",
                request.getDestination().getName(),
                request.getDestination().getLatitude(),
                request.getDestination().getLongitude());

        List<ItineraryOptionDTO> itineraries = otpItineraryService.computeItinerary(request);

        if (itineraries.isEmpty()) {
            log.warn("Aucun itineraire trouve entre {} et {}",
                    request.getOrigin().getName(), request.getDestination().getName());
            return ResponseEntity.noContent().build();
        }

        log.info("{} itineraire(s) trouve(s)", itineraries.size());
        return ResponseEntity.ok(itineraries);
    }
}