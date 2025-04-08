package ua.deti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.deti.backend.model.WeatherCache;
import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherCacheRepository extends JpaRepository<WeatherCache, Long> {
    Optional<WeatherCache> findByLocationAndDate(String location, LocalDateTime date);
    void deleteByTimestampBefore(LocalDateTime timestamp);
} 