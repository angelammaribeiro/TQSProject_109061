package ua.deti.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.deti.backend.dto.WeatherCacheDTO;
import ua.deti.backend.model.WeatherCache;
import ua.deti.backend.service.WeatherService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public String Hello(){
        return "Hello World";
    }

    @GetMapping("/forecast")
    public ResponseEntity<WeatherCacheDTO> getWeatherForecast(
            @RequestParam String location,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        logger.info("GET /weather/forecast - Fetching weather forecast for {} on {}", location, date);
        
        if (location == null || location.trim().isEmpty()) {
            logger.warn("Invalid location parameter: {}", location);
            return ResponseEntity.badRequest().build();
        }

        try {
            WeatherCacheDTO forecast = weatherService.getWeatherForecast(location, date);
            return forecast != null ? ResponseEntity.ok(forecast) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching weather forecast", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/cache/stats")
    public ResponseEntity<WeatherCache.CacheStats> getCacheStats() {
        logger.info("GET /weather/cache/stats - Fetching cache statistics");
        
        try {
            WeatherCache.CacheStats stats = weatherService.getCacheStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching cache statistics", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 