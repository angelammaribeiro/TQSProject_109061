package ua.deti.backend.service;

import ua.deti.backend.dto.WeatherCacheDTO;
import ua.deti.backend.model.WeatherCache;
import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherService {
    WeatherCacheDTO getWeatherForecast(String location, LocalDateTime date);
    void cleanupExpiredCache();
    WeatherCache.CacheStats getCacheStats();
    void setApiKey(String apiKey);
    void setApiUrl(String apiUrl);
    WeatherCacheDTO getWeather(String location, LocalDateTime date);
} 