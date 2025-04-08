package ua.deti.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import ua.deti.backend.config.WeatherApiConfig;
import ua.deti.backend.dto.WeatherCacheDTO;
import ua.deti.backend.dto.WeatherResponse;
import ua.deti.backend.model.WeatherCache;
import ua.deti.backend.repository.WeatherCacheRepository;
import ua.deti.backend.service.impl.WeatherServiceImpl;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherCacheRepository weatherCacheRepository;

    @Mock
    private WeatherApiConfig weatherApiConfig;

    @InjectMocks
    private WeatherServiceImpl weatherService;

    private final String testLocation = "Aveiro";
    private final String testForecast = "Sunny, 25°C";
    private final LocalDateTime testDate = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        // No setup needed as we'll configure mocks in each test
    }

    @Test
    void testGetWeather_WhenCacheExists_ReturnsCachedData() {
        // Arrange
        WeatherCache cachedWeather = new WeatherCache();
        cachedWeather.setLocation(testLocation);
        cachedWeather.setDate(testDate);
        cachedWeather.setForecast(testForecast);
        cachedWeather.setTimestamp(LocalDateTime.now().minusMinutes(5));

        when(weatherCacheRepository.findByLocationAndDate(testLocation, testDate))
            .thenReturn(Optional.of(cachedWeather));

        // Act
        WeatherCacheDTO result = weatherService.getWeather(testLocation, testDate);

        // Assert
        assertNotNull(result);
        assertEquals(testLocation, result.getLocation());
        assertEquals(testForecast, result.getForecast());
        verify(weatherCacheRepository, times(1)).findByLocationAndDate(testLocation, testDate);
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    void testGetWeather_WhenCacheExpired_CallsAPIAndUpdatesCache() {
        // Arrange
        WeatherCache expiredCache = new WeatherCache();
        expiredCache.setLocation(testLocation);
        expiredCache.setDate(testDate);
        expiredCache.setForecast("Old forecast");
        expiredCache.setTimestamp(LocalDateTime.now().minusHours(25)); // Make sure it's expired

        when(weatherCacheRepository.findByLocationAndDate(testLocation, testDate))
            .thenReturn(Optional.of(expiredCache));

        when(weatherApiConfig.getApiKey()).thenReturn("test-api-key");
        when(weatherApiConfig.getApiUrl()).thenReturn("http://test-weather-api.com");

        WeatherResponse mockResponse = new WeatherResponse();
        WeatherResponse.Current current = new WeatherResponse.Current();
        WeatherResponse.Condition condition = new WeatherResponse.Condition();
        condition.setText(testForecast);
        current.setCondition(condition);
        mockResponse.setCurrent(current);

        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class)))
            .thenReturn(mockResponse);

        when(weatherCacheRepository.save(any(WeatherCache.class)))
            .thenAnswer(invocation -> {
                WeatherCache savedCache = invocation.getArgument(0);
                savedCache.setForecast(testForecast);
                return savedCache;
            });

        // Act
        WeatherCacheDTO result = weatherService.getWeather(testLocation, testDate);

        // Assert
        assertNotNull(result);
        assertEquals(testLocation, result.getLocation());
        assertEquals(testForecast, result.getForecast());
        verify(weatherCacheRepository, times(1)).findByLocationAndDate(testLocation, testDate);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherResponse.class));
        verify(weatherCacheRepository, times(1)).save(any(WeatherCache.class));
    }

    @Test
    void testGetWeather_WhenNoCache_CallsAPIAndSavesNewCache() {
        // Arrange
        when(weatherCacheRepository.findByLocationAndDate(testLocation, testDate))
            .thenReturn(Optional.empty());

        when(weatherApiConfig.getApiKey()).thenReturn("test-api-key");
        when(weatherApiConfig.getApiUrl()).thenReturn("http://test-weather-api.com");

        WeatherResponse mockResponse = new WeatherResponse();
        WeatherResponse.Current current = new WeatherResponse.Current();
        WeatherResponse.Condition condition = new WeatherResponse.Condition();
        condition.setText(testForecast);
        current.setCondition(condition);
        mockResponse.setCurrent(current);

        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class)))
            .thenReturn(mockResponse);

        WeatherCache newCache = new WeatherCache();
        newCache.setLocation(testLocation);
        newCache.setDate(testDate);
        newCache.setForecast(testForecast);
        newCache.setTimestamp(LocalDateTime.now());

        when(weatherCacheRepository.save(any(WeatherCache.class)))
            .thenReturn(newCache);

        // Act
        WeatherCacheDTO result = weatherService.getWeather(testLocation, testDate);

        // Assert
        assertNotNull(result);
        assertEquals(testLocation, result.getLocation());
        assertEquals(testForecast, result.getForecast());
        verify(weatherCacheRepository, times(1)).findByLocationAndDate(testLocation, testDate);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherResponse.class));
        verify(weatherCacheRepository, times(1)).save(any(WeatherCache.class));
    }
} 