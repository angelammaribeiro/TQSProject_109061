package ua.deti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.deti.backend.model.Meal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findByRestaurantIdAndAvailableFromLessThanEqualAndAvailableToGreaterThanEqual(
            Long restaurantId, LocalDate endDate, LocalDate startDate);
    
    List<Meal> findByRestaurantId(Long restaurantId);

    @Query("SELECT m FROM Meal m WHERE m.restaurant.id = :restaurantId " +
           "AND :date BETWEEN m.availableFrom AND m.availableTo " +
           "ORDER BY m.price ASC")
    List<Meal> findMealsAvailableOnDate(@Param("restaurantId") Long restaurantId, 
                                       @Param("date") LocalDate date);
} 