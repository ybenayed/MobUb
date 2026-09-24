package com.smartcampus.backend.service.station;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@Service
@RequiredArgsConstructor
public class LineTBService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String LINES_DISCOVERY_URL =
        "https://bdx.mecatran.com/utw/ws/siri/2.0/bordeaux/lines-discovery.json" +
        "?AccountKey=opendata-bordeaux-metropole-flux-gtfs-rt";

    // 6h de cache :  le referentiel des lignes est quasi statique
    private static final long CACHE_TTL_SECONDS = 6 * 60 * 60;

    private volatile Map<String, String> lineCodeByRef = Map.of();
    private volatile Instant expiresAt = Instant.EPOCH;
    private final ReentrantLock lock = new ReentrantLock();

    
    public String resolveLineCode(String lineRef) {
        if (lineRef == null || lineRef.isBlank()) return null;
        ensureFresh();
        return lineCodeByRef.get(lineRef);
    }

    private void ensureFresh() {
        if (Instant.now().isBefore(expiresAt)) return;

        lock.lock();
        try {
            if (Instant.now().isBefore(expiresAt)) return; 

            Map<String, String> fresh = fetchLines();
            if (!fresh.isEmpty()) {
                lineCodeByRef = fresh;
                expiresAt = Instant.now().plusSeconds(CACHE_TTL_SECONDS);
                log.info("Referentiel des lignes TB rafraichi : {} lignes", fresh.size());
            }
        } catch (Exception e) {
            log.error("Erreur rafraichissement referentiel lignes TB, conservation de l'ancien cache", e);
            expiresAt = Instant.now().plusSeconds(60);
        } finally {
            lock.unlock();
        }
    }

    private Map<String, String> fetchLines() {
        String rawJson = restTemplate.getForObject(LINES_DISCOVERY_URL, String.class);
        Map<String, String> result = new ConcurrentHashMap<>();

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            JsonNode lines = root.path("Siri").path("LinesDelivery").path("AnnotatedLineRef");

            for (JsonNode line : lines) {
                String ref = line.path("LineRef").path("value").asText(null);
                String code = line.path("LineCode").path("value").asText(null);
                if (ref != null && code != null) {
                    result.put(ref, code);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur parsing referentiel lignes TB", e);
        }

        return result;
    }
}