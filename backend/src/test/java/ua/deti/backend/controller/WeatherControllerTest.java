package ua.deti.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import ua.deti.backend.dto.WeatherCacheDTO;
import ua.deti.backend.model.WeatherCache;
import ua.deti.backend.service.WeatherService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    private final String testLocation = "Aveiro";
    private final String testForecast = "Sunny, 25°C";
    private final LocalDateTime testDate = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetWeatherForecast_Success() {
        // Arrange
        WeatherCacheDTO mockForecast = new WeatherCacheDTO();
        mockForecast.setLocation(testLocation);
        mockForecast.setDate(testDate);
        mockForecast.setForecast(testForecast);

        when(weatherService.getWeatherForecast(anyString(), any(LocalDateTime.class)))
            .thenReturn(mockForecast);

        // Act
        ResponseEntity<WeatherCacheDTO> response = weatherController.getWeatherForecast(testLocation, testDate);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(testLocation, response.getBody().getLocation());
        assertEquals(testForecast, response.getBody().getForecast());
    }

    @Test
    void testGetWeatherForecast_NotFound() {
        // Arrange
        when(weatherService.getWeatherForecast(anyString(), any(LocalDateTime.class)))
            .thenReturn(null);

        // Act
        ResponseEntity<WeatherCacheDTO> response = weatherController.getWeatherForecast(testLocation, testDate);

        // Assert
        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void testGetWeatherForecast_EmptyLocation() {
        // Act
        ResponseEntity<WeatherCacheDTO> response = weatherController.getWeatherForecast("", testDate);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetWeatherForecast_NullLocation() {
        // Act
        ResponseEntity<WeatherCacheDTO> response = weatherController.getWeatherForecast(null, testDate);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testGetWeatherForecast_ServiceError() {
        // Arrange
        when(weatherService.getWeatherForecast(anyString(), any(LocalDateTime.class)))
            .thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<WeatherCacheDTO> response = weatherController.getWeatherForecast(testLocation, testDate);

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
    }

    @Test
    void testGetCacheStats_Success() {
        // Arrange
        WeatherCache.CacheStats mockStats = new WeatherCache.CacheStats(10, 5, 5);
        when(weatherService.getCacheStats()).thenReturn(mockStats);

        // Act
        ResponseEntity<WeatherCache.CacheStats> response = weatherController.getCacheStats();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().getTotalRequests());
        assertEquals(5, response.getBody().getHits());
        assertEquals(5, response.getBody().getMisses());
    }

    @Test
    void testGetCacheStats_ServiceError() {
        // Arrange
        when(weatherService.getCacheStats())
            .thenThrow(new RuntimeException("Service error"));

        // Act
        ResponseEntity<WeatherCache.CacheStats> response = weatherController.getCacheStats();

        // Assert
        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
    }
} 