package ua.deti.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ua.deti.backend.config.WeatherApiConfig;
import ua.deti.backend.dto.WeatherForecastResponse;
import ua.deti.backend.model.WeatherCache;
import ua.deti.backend.repository.WeatherCacheRepository;
import ua.deti.backend.service.WeatherService;
import ua.deti.backend.dto.WeatherCacheDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);
    private static final long CACHE_TTL_HOURS = 24;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private WeatherCacheRepository weatherCacheRepository;

    @Autowired
    private WeatherApiConfig weatherApiConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public WeatherCacheDTO getWeatherForecast(String location, LocalDateTime date) {
        WeatherCache.incrementTotalRequests();

        // Round date to the nearest day for caching purposes
        LocalDateTime cacheDate = date.truncatedTo(ChronoUnit.DAYS);

        // Try to get from cache first
        Optional<WeatherCache> cachedForecast = weatherCacheRepository.findByLocationAndDate(location, cacheDate);

        // Case 1: Cache exists and is valid
        if (cachedForecast.isPresent() && isCacheValid(cachedForecast.get())) {
            WeatherCache.incrementHits();
            logger.info("Cache hit for location: {} and date: {}", location, cacheDate);
            return mapToDTO(cachedForecast.get());
        }

        // Case 2: Cache miss or expired
        WeatherCache.incrementMisses();
        logger.info("Cache miss or expired for location: {} and date: {}", location, cacheDate);

        // Get fresh forecast from API
        try {
            String formattedDate = date.format(DATE_FORMATTER);
            String apiUrl = String.format("%s/forecast.json?key=%s&q=%s&dt=%s&aqi=no&alerts=no",
                    weatherApiConfig.getApiUrl().replace("/current.json", ""), // Ensure base URL is used
                    weatherApiConfig.getApiKey(),
                    location,
                    formattedDate);

            logger.debug("Calling Weather API: {}", apiUrl);
            WeatherForecastResponse response = restTemplate.getForObject(apiUrl, WeatherForecastResponse.class);

            if (response == null || response.getForecast() == null || response.getForecast().getForecastday() == null || response.getForecast().getForecastday().isEmpty()) {
                logger.error("Invalid or empty response from weather API for location: {} and date: {}", location, formattedDate);
                throw new RuntimeException("Invalid response from weather API");
            }

            WeatherForecastResponse.Day dayForecast = response.getForecast().getForecastday().get(0).getDay();

            // Create or update cache
            WeatherCache weatherCache = cachedForecast.orElse(new WeatherCache());
            weatherCache.setLocation(location);
            weatherCache.setDate(cacheDate); // Store truncated date
            weatherCache.setMaxTemperature(dayForecast.getMaxtemp_c());
            weatherCache.setMinTemperature(dayForecast.getMintemp_c());
            weatherCache.setHumidity(dayForecast.getAvghumidity());
            weatherCache.setChanceOfRain(dayForecast.getDaily_chance_of_rain());
            weatherCache.setTimestamp(LocalDateTime.now());

            // Save and return
            weatherCache = weatherCacheRepository.save(weatherCache);
            logger.info("Saved new forecast to cache for location: {} and date: {}", location, cacheDate);
            return mapToDTO(weatherCache);
        } catch (RestClientException e) {
            logger.error("Error calling Weather API for location: {} and date: {}: {}", location, date.format(DATE_FORMATTER), e.getMessage());
            throw new RuntimeException("Failed to fetch weather data from API", e);
        } catch (Exception e) {
            logger.error("Error processing weather forecast for location: {} and date: {}: {}", location, date.format(DATE_FORMATTER), e.getMessage(), e);
            throw new RuntimeException("Failed to process weather data", e);
        }
    }

    // Helper method to map entity to DTO
    private WeatherCacheDTO mapToDTO(WeatherCache cache) {
        WeatherCacheDTO dto = new WeatherCacheDTO();
        dto.setLocation(cache.getLocation());
        dto.setDate(cache.getDate());
        dto.setMaxTemperature(cache.getMaxTemperature());
        dto.setMinTemperature(cache.getMinTemperature());
        dto.setHumidity(cache.getHumidity());
        dto.setChanceOfRain(cache.getChanceOfRain());
        dto.setTimestamp(cache.getTimestamp());
        return dto;
    }

    private boolean isCacheValid(WeatherCache cache) {
        return ChronoUnit.HOURS.between(cache.getTimestamp(), LocalDateTime.now()) < CACHE_TTL_HOURS;
    }

    @Override
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredCache() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(CACHE_TTL_HOURS);
        long deletedCount = weatherCacheRepository.deleteByTimestampBefore(threshold);
        logger.info("Cleaned up {} expired weather cache entries older than {}", deletedCount, threshold);
    }

    @Override
    public WeatherCache.CacheStats getCacheStats() {
        return WeatherCache.getCacheStats();
    }

    @Override
    public void setApiKey(String apiKey) {
        weatherApiConfig.setApiKey(apiKey);
    }

    @Override
    public void setApiUrl(String apiUrl) {
        // Ensure the URL stored doesn't include specific endpoints like /current.json
        if (apiUrl != null && (apiUrl.endsWith("/current.json") || apiUrl.endsWith("/forecast.json"))) {
             apiUrl = apiUrl.substring(0, apiUrl.lastIndexOf('/'));
        }
        weatherApiConfig.setApiUrl(apiUrl);
    }

    @Override
    public WeatherCacheDTO getWeather(String location, LocalDateTime date) {
        // This method seems redundant if getWeatherForecast is the primary one.
        // Consider removing or clarifying its purpose.
        logger.warn("Calling deprecated getWeather method, use getWeatherForecast instead.");
        return getWeatherForecast(location, date);
    }
} 