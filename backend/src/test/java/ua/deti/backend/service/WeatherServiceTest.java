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
import ua.deti.backend.dto.WeatherForecastResponse;
import ua.deti.backend.model.WeatherCache;
import ua.deti.backend.repository.WeatherCacheRepository;
import ua.deti.backend.service.impl.WeatherServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    private final double testMaxTemp = 25.0;
    private final double testMinTemp = 15.0;
    private final double testHumidity = 60.0;
    private final double testChanceOfRain = 10.0;
    private final LocalDateTime testDate = LocalDateTime.now();
    private final LocalDateTime testCacheDate = testDate.truncatedTo(ChronoUnit.DAYS);

    @BeforeEach
    void setUp() {
        when(weatherApiConfig.getApiKey()).thenReturn("test-api-key");
        when(weatherApiConfig.getApiUrl()).thenReturn("http://test-weather-api.com/v1");
    }

    private WeatherCache createTestWeatherCache(LocalDateTime timestamp) {
        WeatherCache cache = new WeatherCache();
        cache.setLocation(testLocation);
        cache.setDate(testCacheDate);
        cache.setMaxTemperature(testMaxTemp);
        cache.setMinTemperature(testMinTemp);
        cache.setHumidity(testHumidity);
        cache.setChanceOfRain(testChanceOfRain);
        cache.setTimestamp(timestamp);
        return cache;
    }

    private WeatherForecastResponse createMockForecastResponse() {
        WeatherForecastResponse mockResponse = new WeatherForecastResponse();
        WeatherForecastResponse.Forecast forecast = new WeatherForecastResponse.Forecast();
        WeatherForecastResponse.ForecastDay forecastDay = new WeatherForecastResponse.ForecastDay();
        WeatherForecastResponse.Day day = new WeatherForecastResponse.Day();
        day.setMaxtemp_c(testMaxTemp);
        day.setMintemp_c(testMinTemp);
        day.setAvghumidity(testHumidity);
        day.setDaily_chance_of_rain(testChanceOfRain);
        forecastDay.setDay(day);
        List<WeatherForecastResponse.ForecastDay> forecastDayList = new ArrayList<>();
        forecastDayList.add(forecastDay);
        forecast.setForecastday(forecastDayList);
        mockResponse.setForecast(forecast);
        return mockResponse;
    }

    @Test
    void testGetWeatherForecast_WhenCacheExists_ReturnsCachedData() {
        WeatherCache cachedWeather = createTestWeatherCache(LocalDateTime.now().minusMinutes(5));
        when(weatherCacheRepository.findByLocationAndDate(testLocation, testCacheDate))
            .thenReturn(Optional.of(cachedWeather));

        WeatherCacheDTO result = weatherService.getWeatherForecast(testLocation, testDate);

        assertNotNull(result);
        assertEquals(testLocation, result.getLocation());
        assertEquals(testMaxTemp, result.getMaxTemperature());
        assertEquals(testMinTemp, result.getMinTemperature());
        assertEquals(testHumidity, result.getHumidity());
        assertEquals(testChanceOfRain, result.getChanceOfRain());
        verify(weatherCacheRepository, times(1)).findByLocationAndDate(testLocation, testCacheDate);
        verify(restTemplate, never()).getForObject(anyString(), any());
    }

    @Test
    void testGetWeatherForecast_WhenCacheExpired_CallsAPIAndUpdatesCache() {
        WeatherCache expiredCache = createTestWeatherCache(LocalDateTime.now().minusHours(25));
        when(weatherCacheRepository.findByLocationAndDate(testLocation, testCacheDate))
            .thenReturn(Optional.of(expiredCache));

        WeatherForecastResponse mockResponse = createMockForecastResponse();
        when(restTemplate.getForObject(anyString(), eq(WeatherForecastResponse.class)))
            .thenReturn(mockResponse);

        when(weatherCacheRepository.save(any(WeatherCache.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        WeatherCacheDTO result = weatherService.getWeatherForecast(testLocation, testDate);

        assertNotNull(result);
        assertEquals(testLocation, result.getLocation());
        assertEquals(testMaxTemp, result.getMaxTemperature());
        verify(weatherCacheRepository, times(1)).findByLocationAndDate(testLocation, testCacheDate);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherForecastResponse.class));
        verify(weatherCacheRepository, times(1)).save(any(WeatherCache.class));
    }

    @Test
    void testGetWeatherForecast_WhenNoCache_CallsAPIAndSavesNewCache() {
        when(weatherCacheRepository.findByLocationAndDate(testLocation, testCacheDate))
            .thenReturn(Optional.empty());

        WeatherForecastResponse mockResponse = createMockForecastResponse();
        when(restTemplate.getForObject(anyString(), eq(WeatherForecastResponse.class)))
            .thenReturn(mockResponse);
        
        when(weatherCacheRepository.save(any(WeatherCache.class)))
            .thenAnswer(invocation -> {
                WeatherCache savedCache = invocation.getArgument(0);
                savedCache.setId(1L);
                return savedCache;
            });

        WeatherCacheDTO result = weatherService.getWeatherForecast(testLocation, testDate);

        assertNotNull(result);
        assertEquals(testLocation, result.getLocation());
        assertEquals(testMaxTemp, result.getMaxTemperature());
        verify(weatherCacheRepository, times(1)).findByLocationAndDate(testLocation, testCacheDate);
        verify(restTemplate, times(1)).getForObject(anyString(), eq(WeatherForecastResponse.class));
        verify(weatherCacheRepository, times(1)).save(any(WeatherCache.class));
    }
} 