package ua.deti.backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.deti.backend.config.WeatherApiConfig;
import ua.deti.backend.dto.WeatherResponse;
import ua.deti.backend.model.WeatherCache;
import ua.deti.backend.repository.WeatherCacheRepository;
import ua.deti.backend.service.WeatherService;
import ua.deti.backend.dto.WeatherCacheDTO;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class WeatherServiceImpl implements WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);
    private static final long CACHE_TTL_HOURS = 24;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WeatherCacheRepository weatherCacheRepository;

    @Autowired
    private WeatherApiConfig weatherApiConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public WeatherCacheDTO getWeatherForecast(String location, LocalDateTime date) {
        WeatherCache.incrementTotalRequests();
        
        // Try to get from cache first
        Optional<WeatherCache> cachedForecast = weatherCacheRepository.findByLocationAndDate(location, date);
        
        // Case 1: Cache exists and is valid
        if (cachedForecast.isPresent() && isCacheValid(cachedForecast.get())) {
            WeatherCache.incrementHits();
            logger.info("Cache hit for location: {} and date: {}", location, date);
            return mapToDTO(cachedForecast.get());
        }
        
        // Case 2: Cache miss or expired
        WeatherCache.incrementMisses();
        logger.info("Cache miss for location: {} and date: {}", location, date);
        
        // Get fresh forecast from API
        try {
            WeatherResponse response = restTemplate.getForObject(
                String.format("%s?key=%s&q=%s&aqi=no", 
                    weatherApiConfig.getApiUrl(), 
                    weatherApiConfig.getApiKey(),
                    location),
                WeatherResponse.class
            );
            
            if (response == null || response.getCurrent() == null || response.getCurrent().getCondition() == null) {
                throw new RuntimeException("Invalid response from weather API");
            }

            String newForecast = response.getCurrent().getCondition().getText();
            
            // Create or update cache
            WeatherCache weatherCache = cachedForecast.orElse(new WeatherCache());
            weatherCache.setLocation(location);
            weatherCache.setDate(date);
            weatherCache.setForecast(newForecast);
            weatherCache.setTimestamp(LocalDateTime.now());
            
            // Save and return
            weatherCache = weatherCacheRepository.save(weatherCache);
            return mapToDTO(weatherCache);
        } catch (Exception e) {
            logger.error("Error fetching weather forecast: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch weather data", e);
        }
    }
    
    // Helper method to map entity to DTO
    private WeatherCacheDTO mapToDTO(WeatherCache cache) {
        WeatherCacheDTO dto = new WeatherCacheDTO();
        dto.setLocation(cache.getLocation());
        dto.setDate(cache.getDate());
        dto.setForecast(cache.getForecast());
        dto.setTimestamp(cache.getTimestamp());
        return dto;
    }

    private boolean isCacheValid(WeatherCache cache) {
        return ChronoUnit.HOURS.between(cache.getTimestamp(), LocalDateTime.now()) < CACHE_TTL_HOURS;
    }

    private String fetchWeatherFromAPI(String location, LocalDateTime date) {
        String url = String.format("%s?key=%s&q=%s&aqi=no", 
            weatherApiConfig.getApiUrl(), 
            weatherApiConfig.getApiKey(),
            location);

        try {
            WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);
            
            if (response != null && response.getCurrent() != null && response.getCurrent().getCondition() != null) {
                return response.getCurrent().getCondition().getText();
            }
            throw new RuntimeException("Empty response from weather API");
        } catch (Exception e) {
            logger.error("Error calling weather API: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch weather data", e);
        }
    }

    private String formatWeatherResponse(WeatherResponse response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            logger.error("Error formatting weather response: {}", e.getMessage());
            throw new RuntimeException("Failed to format weather data", e);
        }
    }

    @Override
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredCache() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(CACHE_TTL_HOURS);
        weatherCacheRepository.deleteByTimestampBefore(threshold);
        logger.info("Cleaned up expired weather cache entries");
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
        weatherApiConfig.setApiUrl(apiUrl);
    }

    @Override
    public WeatherCacheDTO getWeather(String location, LocalDateTime date) {
        return getWeatherForecast(location, date);
    }
} 