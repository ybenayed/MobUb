package com.smartcampus.backend.service.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.weather.CurrentAirQualityDTO;
import com.smartcampus.backend.dto.weather.DailyAirQualityDTO;
import com.smartcampus.backend.dto.weather.HourlyAirQualityPointDTO;
import com.smartcampus.backend.dto.weather.AirQualitySummaryDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class AirQualityLiveCacheService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${weather.campus.latitude:44.808}")
    private double latitude;

    @Value("${weather.campus.longitude:-0.595}")
    private double longitude;

    private static final String AIR_QUALITY_URL = "https://air-quality-api.open-meteo.com/v1/air-quality";

    private static final int PAST_DAYS = 7;
    private static final int FORECAST_DAYS = 7;

    private final AtomicReference<AirQualitySummaryDTO> cache = new AtomicReference<>();

    public AirQualityLiveCacheService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 minutes
    public void refresh() {
        try {
            String url = AIR_QUALITY_URL +
                    "?latitude=" + latitude + "&longitude=" + longitude +
                    "&hourly=pm2_5,pm10,ozone,nitrogen_dioxide,european_aqi" +
                    "&timezone=auto&forecast_days=" + FORECAST_DAYS + "&past_days=" + PAST_DAYS;

            String rawJson = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(rawJson);

            cache.set(buildSummary(root));
            log.info("Cache qualite de l'air rafraichi");
        } catch (Exception e) {
            log.error("Erreur refresh cache qualite de l'air, ancien cache conserve", e);
        }
    }

    public AirQualitySummaryDTO getSummary() { return cache.get(); }

    public CurrentAirQualityDTO getCurrent() {
        AirQualitySummaryDTO s = cache.get();
        return s != null ? s.getCurrent() : null;
    }

    public List<HourlyAirQualityPointDTO> getHourlyToday() {
        AirQualitySummaryDTO s = cache.get();
        return s != null ? s.getHourlyToday() : List.of();
    }

    public List<DailyAirQualityDTO> getPastDaily() {
        AirQualitySummaryDTO s = cache.get();
        return s != null ? s.getPastDaily() : List.of();
    }

    public List<DailyAirQualityDTO> getForecastDaily() {
        AirQualitySummaryDTO s = cache.get();
        return s != null ? s.getForecastDaily() : List.of();
    }

    private AirQualitySummaryDTO buildSummary(JsonNode root) {
        JsonNode hourly = root.path("hourly");
        JsonNode hourlyTime = hourly.path("time");

        List<HourlyAirQualityPointDTO> allHourly = new ArrayList<>();
        for (int i = 0; i < hourlyTime.size(); i++) {
            allHourly.add(toHourlyPoint(hourly, hourlyTime, i));
        }

        // ─── Hourly : 24h d'aujourd'hui
        int todayStartIndex = PAST_DAYS * 24;
        List<HourlyAirQualityPointDTO> hourlyToday = new ArrayList<>();
        for (int i = todayStartIndex; i < todayStartIndex + 24 && i < allHourly.size(); i++) {
            hourlyToday.add(allHourly.get(i));
        }

        // ─── Current : heure la plus proche de maintenant
        int nowIndex = Math.min(todayStartIndex + LocalDateTime.now().getHour(), allHourly.size() - 1);
        CurrentAirQualityDTO current = null;
        if (!allHourly.isEmpty()) {
            HourlyAirQualityPointDTO nowPoint = allHourly.get(nowIndex);
            current = CurrentAirQualityDTO.builder()
                    .time(nowPoint.getTime())
                    .pm2_5(nowPoint.getPm2_5())
                    .pm10(nowPoint.getPm10())
                    .ozone(nowPoint.getOzone())
                    .nitrogenDioxide(nowPoint.getNitrogenDioxide())
                    .europeanAqi(nowPoint.getEuropeanAqi())
                    .category(nowPoint.getCategory())
                    .description(nowPoint.getDescription())
                    .icon(nowPoint.getIcon())
                    .build();
        }

        // ─── Agregation par jour (moyennes + pire indice de la journee)
        List<DailyAirQualityDTO> allDaily = aggregateByDay(allHourly);

        List<DailyAirQualityDTO> pastDaily = new ArrayList<>(
                allDaily.subList(0, Math.min(PAST_DAYS, allDaily.size())));
        List<DailyAirQualityDTO> forecastDaily = allDaily.size() > PAST_DAYS
                ? new ArrayList<>(allDaily.subList(PAST_DAYS, allDaily.size()))
                : new ArrayList<>();

        return AirQualitySummaryDTO.builder()
                .latitude(latitude)
                .longitude(longitude)
                .current(current)
                .hourlyToday(hourlyToday)
                .pastDaily(pastDaily)
                .forecastDaily(forecastDaily)
                .build();
    }

    private HourlyAirQualityPointDTO toHourlyPoint(JsonNode hourly, JsonNode hourlyTime, int i) {
        Integer europeanAqi = hourly.path("european_aqi").has(i) ? hourly.path("european_aqi").get(i).asInt() : null;
        AirQualityCodeResolver.AirQualityInfo info = AirQualityCodeResolver.resolve(europeanAqi);

        return HourlyAirQualityPointDTO.builder()
                .time(hourlyTime.get(i).asText())
                .pm2_5(hourly.path("pm2_5").get(i).asDouble())
                .pm10(hourly.path("pm10").get(i).asDouble())
                .ozone(hourly.path("ozone").get(i).asDouble())
                .nitrogenDioxide(hourly.path("nitrogen_dioxide").get(i).asDouble())
                .europeanAqi(europeanAqi)
                .category(info.category())
                .description(info.description())
                .icon(info.icon())
                .build();
    }

    private List<DailyAirQualityDTO> aggregateByDay(List<HourlyAirQualityPointDTO> allHourly) {
        Map<LocalDate, List<HourlyAirQualityPointDTO>> byDay = new TreeMap<>();
        for (HourlyAirQualityPointDTO point : allHourly) {
            LocalDate date = LocalDate.parse(point.getTime().substring(0, 10));
            byDay.computeIfAbsent(date, d -> new ArrayList<>()).add(point);
        }

        List<DailyAirQualityDTO> result = new ArrayList<>();
        for (Map.Entry<LocalDate, List<HourlyAirQualityPointDTO>> entry : byDay.entrySet()) {
            List<HourlyAirQualityPointDTO> points = entry.getValue();

            double pm25Avg = points.stream().mapToDouble(HourlyAirQualityPointDTO::getPm2_5).average().orElse(0);
            double pm10Avg = points.stream().mapToDouble(HourlyAirQualityPointDTO::getPm10).average().orElse(0);
            double ozoneAvg = points.stream().mapToDouble(HourlyAirQualityPointDTO::getOzone).average().orElse(0);
            double no2Avg = points.stream().mapToDouble(HourlyAirQualityPointDTO::getNitrogenDioxide).average().orElse(0);
            int aqiMax = points.stream()
                    .filter(p -> p.getEuropeanAqi() != null)
                    .mapToInt(HourlyAirQualityPointDTO::getEuropeanAqi)
                    .max().orElse(0);

            AirQualityCodeResolver.AirQualityInfo info = AirQualityCodeResolver.resolve(aqiMax);

            result.add(DailyAirQualityDTO.builder()
                    .date(entry.getKey())
                    .pm2_5Avg(round(pm25Avg))
                    .pm10Avg(round(pm10Avg))
                    .ozoneAvg(round(ozoneAvg))
                    .nitrogenDioxideAvg(round(no2Avg))
                    .europeanAqiMax(aqiMax)
                    .category(info.category())
                    .description(info.description())
                    .icon(info.icon())
                    .build());
        }
        return result;
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}