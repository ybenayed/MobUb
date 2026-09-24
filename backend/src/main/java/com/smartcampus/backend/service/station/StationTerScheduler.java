package com.smartcampus.backend.service.station;

import com.smartcampus.backend.service.station.PassageTerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class StationTerScheduler {

    private final PassageTerService passageTerService;

    @Value("#{'${navitia.watched-stations:}'.split(',')}")
    private List<String> watchedStations;

    @Scheduled(fixedDelayString = "${navitia.refresh-interval-ms:1800000}") 
    public void refreshWatchedStations() {
        for (String stopId : watchedStations) {
            String trimmed = stopId.trim();
            if (trimmed.isEmpty()) continue;

            try {
                passageTerService.refreshCache(trimmed);
            } catch (Exception e) {
                log.warn("Echec rafraichissement scheduler pour {} : {}", trimmed, e.getMessage());
            }
        }
    }
}