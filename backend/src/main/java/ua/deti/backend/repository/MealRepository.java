package ua.deti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.deti.backend.model.Meal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByRestaurantIdAndAvailableFromLessThanEqualAndAvailableToGreaterThanEqual(
            Long restaurantId, LocalDate endDate, LocalDate startDate);
    
    List<Meal> findByRestaurantId(Long restaurantId);
} 