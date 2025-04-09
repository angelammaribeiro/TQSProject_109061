package ua.deti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.deti.backend.model.Restaurant;
import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByNameContainingIgnoreCase(String name);
    List<Restaurant> findByLocationContainingIgnoreCase(String location);
    Optional<Restaurant> findByNameIgnoreCase(String name);
} 