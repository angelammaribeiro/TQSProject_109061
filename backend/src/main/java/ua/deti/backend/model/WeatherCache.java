package ua.deti.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "weather_cache")
public class WeatherCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column
    private Double maxTemperature;
    @Column
    private Double minTemperature;
    @Column
    private Double humidity;
    @Column
    private Double chanceOfRain;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Cache statistics
    @Transient
    private static long totalRequests = 0;
    @Transient
    private static long hits = 0;
    @Transient
    private static long misses = 0;

    public static class CacheStats {
        private final long totalRequests;
        private final long hits;
        private final long misses;
        private final double hitRate;

        public CacheStats(long totalRequests, long hits, long misses) {
            this.totalRequests = totalRequests;
            this.hits = hits;
            this.misses = misses;
            this.hitRate = totalRequests > 0 ? (double) hits / totalRequests * 100 : 0;
        }

        public long getTotalRequests() { return totalRequests; }
        public long getHits() { return hits; }
        public long getMisses() { return misses; }
        public double getHitRate() { return hitRate; }
    }

    public static void incrementTotalRequests() { totalRequests++; }
    public static void incrementHits() { hits++; }
    public static void incrementMisses() { misses++; }

    public static CacheStats getCacheStats() {
        return new CacheStats(totalRequests, hits, misses);
    }

    // Constructors
    public WeatherCache() {}

    public WeatherCache(String location, LocalDateTime date, String forecast) {
        this.location = location;
        this.date = date;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getChanceOfRain() {
        return chanceOfRain;
    }

    public void setChanceOfRain(Double chanceOfRain) {
        this.chanceOfRain = chanceOfRain;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
} 