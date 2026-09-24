package com.smartcampus.backend.service.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.weather.AirQualityAtDTO;
import com.smartcampus.backend.dto.weather.WeatherRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AirQualityQueryService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String AIR_QUALITY_URL = "https://air-quality-api.open-meteo.com/v1/air-quality";

    private static final int PAST_DAYS = 7;
    private static final int FORECAST_DAYS = 7;

    public AirQualityQueryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AirQualityAtDTO getAirQualityAt(WeatherRequestDTO request) {
        if (request.getLatitude() == null || request.getLongitude() == null || request.getDate() == null) {
            throw new IllegalArgumentException("latitude, longitude et date sont obligatoires");
        }

        LocalDate today = LocalDate.now();
        LocalDate requestedDate = request.getDate();
        long diffDays = ChronoUnit.DAYS.between(today, requestedDate);

        if (diffDays < -PAST_DAYS || diffDays > FORECAST_DAYS - 1) {
            throw new IllegalArgumentException(
                    "La date doit etre comprise entre " + today.minusDays(PAST_DAYS)
                            + " et " + today.plusDays(FORECAST_DAYS - 1));
        }

        try {
            String url = AIR_QUALITY_URL +
                    "?latitude=" + request.getLatitude() + "&longitude=" + request.getLongitude() +
                    "&hourly=pm2_5,pm10,ozone,nitrogen_dioxide,european_aqi" +
                    "&timezone=auto&forecast_days=" + FORECAST_DAYS + "&past_days=" + PAST_DAYS;

            String rawJson = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(rawJson);

            return buildAirQualityAt(root, request);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la recuperation qualite de l'air pour lat={}, lon={}, date={}",
                    request.getLatitude(), request.getLongitude(), request.getDate(), e);
            return null;
        }
    }

    private AirQualityAtDTO buildAirQualityAt(JsonNode root, WeatherRequestDTO request) {
        LocalDate requestedDate = request.getDate();
        LocalTime requestedTime = request.getTime();

        JsonNode hourly = root.path("hourly");
        JsonNode hourlyTime = hourly.path("time");

        List<Integer> dayIndexes = new ArrayList<>();
        for (int i = 0; i < hourlyTime.size(); i++) {
            String iso = hourlyTime.get(i).asText();
            if (LocalDate.parse(iso.substring(0, 10)).equals(requestedDate)) {
                dayIndexes.add(i);
            }
        }
        if (dayIndexes.isEmpty()) {
            throw new IllegalArgumentException("Aucune donnee de qualite de l'air disponible pour la date " + requestedDate);
        }

        double pm25Avg = dayIndexes.stream().mapToDouble(i -> hourly.path("pm2_5").get(i).asDouble()).average().orElse(0);
        double pm10Avg = dayIndexes.stream().mapToDouble(i -> hourly.path("pm10").get(i).asDouble()).average().orElse(0);
        double ozoneAvg = dayIndexes.stream().mapToDouble(i -> hourly.path("ozone").get(i).asDouble()).average().orElse(0);
        double no2Avg = dayIndexes.stream().mapToDouble(i -> hourly.path("nitrogen_dioxide").get(i).asDouble()).average().orElse(0);
        int aqiMax = dayIndexes.stream()
                .filter(i -> hourly.path("european_aqi").has(i))
                .mapToInt(i -> hourly.path("european_aqi").get(i).asInt())
                .max().orElse(0);

        AirQualityCodeResolver.AirQualityInfo dayInfo = AirQualityCodeResolver.resolve(aqiMax);

        AirQualityAtDTO.AirQualityAtDTOBuilder builder = AirQualityAtDTO.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .date(requestedDate)
                .time(requestedTime)
                .pm2_5Avg(round(pm25Avg))
                .pm10Avg(round(pm10Avg))
                .ozoneAvg(round(ozoneAvg))
                .nitrogenDioxideAvg(round(no2Avg))
                .europeanAqi(aqiMax)
                .category(dayInfo.category())
                .description(dayInfo.description())
                .icon(dayInfo.icon());

        if (requestedTime != null) {
            int hourIndex = -1;
            for (int i : dayIndexes) {
                String iso = hourlyTime.get(i).asText();
                int hour = Integer.parseInt(iso.substring(11, 13));
                if (hour == requestedTime.getHour()) {
                    hourIndex = i;
                    break;
                }
            }

            if (hourIndex != -1) {
                Integer hourAqi = hourly.path("european_aqi").has(hourIndex)
                        ? hourly.path("european_aqi").get(hourIndex).asInt() : null;
                AirQualityCodeResolver.AirQualityInfo hourInfo = AirQualityCodeResolver.resolve(hourAqi);

                builder.pm2_5(hourly.path("pm2_5").get(hourIndex).asDouble())
                        .pm10(hourly.path("pm10").get(hourIndex).asDouble())
                        .ozone(hourly.path("ozone").get(hourIndex).asDouble())
                        .nitrogenDioxide(hourly.path("nitrogen_dioxide").get(hourIndex).asDouble())
                        .europeanAqi(hourAqi)
                        .category(hourInfo.category())
                        .description(hourInfo.description())
                        .icon(hourInfo.icon());
            } else {
                log.warn("Heure {} non trouvee pour la date {}, retour de la moyenne journaliere uniquement",
                        requestedTime, requestedDate);
            }
        }

        return builder.build();
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}