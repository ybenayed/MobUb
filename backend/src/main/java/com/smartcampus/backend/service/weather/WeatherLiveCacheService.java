package com.smartcampus.backend.service.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcampus.backend.dto.weather.CurrentWeatherDTO;
import com.smartcampus.backend.dto.weather.DailyWeatherDTO;
import com.smartcampus.backend.dto.weather.HourlyPointDTO;
import com.smartcampus.backend.dto.weather.WeatherSummaryDTO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class WeatherLiveCacheService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${weather.campus.latitude:44.808}")
    private double latitude;

    @Value("${weather.campus.longitude:-0.595}")
    private double longitude;

    private static final String WEATHER_URL = "https://api.open-meteo.com/v1/forecast";

    private static final int PAST_DAYS = 7;
    private static final int FORECAST_DAYS = 8; // aujourd'hui + 7 prochains jours

    private final AtomicReference<WeatherSummaryDTO> cache = new AtomicReference<>();

    public WeatherLiveCacheService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 minutes
    public void refresh() {
        try {
            String url = WEATHER_URL +
                "?latitude=" + latitude + "&longitude=" + longitude +
                "&current_weather=true" +
                "&hourly=temperature_2m,weathercode,precipitation_probability,is_day" +
                "&daily=temperature_2m_max,temperature_2m_min,weathercode,precipitation_sum,windspeed_10m_max" +
                "&timezone=auto&forecast_days=" + FORECAST_DAYS + "&past_days=" + PAST_DAYS;

            String rawJson = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(rawJson);

            cache.set(buildSummary(root));
            log.info("Cache meteo rafraichi");
        } catch (Exception e) {
            log.error("Erreur refresh cache meteo, ancien cache conserve", e);
        }
    }

    public WeatherSummaryDTO getSummary() { return cache.get(); }

    public CurrentWeatherDTO getCurrent() {
        WeatherSummaryDTO s = cache.get();
        return s != null ? s.getCurrent() : null;
    }

    public List<HourlyPointDTO> getHourlyToday() {
        WeatherSummaryDTO s = cache.get();
        return s != null ? s.getHourlyToday() : List.of();
    }

    public List<DailyWeatherDTO> getPastDaily() {
        WeatherSummaryDTO s = cache.get();
        return s != null ? s.getPastDaily() : List.of();
    }

    public List<DailyWeatherDTO> getForecastDaily() {
        WeatherSummaryDTO s = cache.get();
        return s != null ? s.getForecastDaily() : List.of();
    }

    private WeatherSummaryDTO buildSummary(JsonNode root) {
        JsonNode currentNode = root.path("current_weather");
        boolean currentIsDay = currentNode.path("is_day").asInt(1) == 1;
        Integer currentCode = currentNode.path("weathercode").asInt();
        WeatherCodeResolver.WeatherCodeInfo currentInfo = WeatherCodeResolver.resolve(currentCode, currentIsDay);

        CurrentWeatherDTO current = CurrentWeatherDTO.builder()
                .temperature(currentNode.path("temperature").asDouble())
                .windspeed(currentNode.path("windspeed").asDouble())
                .winddirection(currentNode.path("winddirection").asInt())
                .weathercode(currentCode)
                .description(currentInfo.description())
                .icon(currentInfo.icon())
                .time(currentNode.path("time").asText(null))
                .isDay(currentNode.path("is_day").asInt(1))
                .build();

        //  Daily : toujours variante "jour" (resume global de la journee)
        List<DailyWeatherDTO> allDaily = new ArrayList<>();
        JsonNode daily = root.path("daily");
        JsonNode dailyTime = daily.path("time");
        for (int i = 0; i < dailyTime.size(); i++) {
            Integer code = daily.path("weathercode").get(i).asInt();
            WeatherCodeResolver.WeatherCodeInfo info = WeatherCodeResolver.resolve(code, true);

            allDaily.add(DailyWeatherDTO.builder()
                    .date(LocalDate.parse(dailyTime.get(i).asText()))
                    .temperatureMax(daily.path("temperature_2m_max").get(i).asDouble())
                    .temperatureMin(daily.path("temperature_2m_min").get(i).asDouble())
                    .weathercode(code)
                    .description(info.description())
                    .icon(info.icon())
                    .precipitationSum(daily.path("precipitation_sum").get(i).asDouble())
                    .windspeedMax(daily.path("windspeed_10m_max").get(i).asDouble())
                    .build());
        }

        List<DailyWeatherDTO> pastDaily = new ArrayList<>(
                allDaily.subList(0, Math.min(PAST_DAYS, allDaily.size())));
        List<DailyWeatherDTO> forecastDaily = allDaily.size() > PAST_DAYS
                ? new ArrayList<>(allDaily.subList(PAST_DAYS, allDaily.size()))
                : new ArrayList<>();

        //  Hourly : 24h d'aujourd'hui, avec is_day reel heure par heure
        List<HourlyPointDTO> hourlyToday = new ArrayList<>();
        JsonNode hourly = root.path("hourly");
        JsonNode hourlyTime = hourly.path("time");
        int todayStartIndex = PAST_DAYS * 24;

        for (int i = todayStartIndex; i < todayStartIndex + 24 && i < hourlyTime.size(); i++) {
            Integer code = hourly.path("weathercode").get(i).asInt();
            boolean isDay = hourly.path("is_day").has(i) && hourly.path("is_day").get(i).asInt(1) == 1;
            WeatherCodeResolver.WeatherCodeInfo info = WeatherCodeResolver.resolve(code, isDay);

            hourlyToday.add(HourlyPointDTO.builder()
                    .time(hourlyTime.get(i).asText())
                    .temperature(hourly.path("temperature_2m").get(i).asDouble())
                    .weathercode(code)
                    .description(info.description())
                    .icon(info.icon())
                    .precipitationProbability(
                            hourly.path("precipitation_probability").has(i)
                                    ? hourly.path("precipitation_probability").get(i).asInt()
                                    : null)
                    .build());
        }

        return WeatherSummaryDTO.builder()
                .latitude(latitude)
                .longitude(longitude)
                .current(current)
                .hourlyToday(hourlyToday)
                .pastDaily(pastDaily)
                .forecastDaily(forecastDaily)
                .build();
    }
}