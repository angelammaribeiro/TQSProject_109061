package ua.deti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ua.deti.backend.model.WeatherCache;
import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherCacheRepository extends JpaRepository<WeatherCache, Long> {
    Optional<WeatherCache> findByLocationAndDate(String location, LocalDateTime date);

    @Modifying
    @Transactional
    @Query("DELETE FROM WeatherCache wc WHERE wc.timestamp < :timestamp")
    long deleteByTimestampBefore(@Param("timestamp") LocalDateTime timestamp);
} 