package ua.deti.backend.service;

import ua.deti.backend.model.Meal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MealService {
    List<Meal> getUpcomingMeals(Long restaurantId, LocalDate startDate, LocalDate endDate);
    Optional<Meal> getMealById(Long id);
    Meal createMeal(Meal meal);
    Meal updateMeal(Long id, Meal meal);
    void deleteMeal(Long id);
    List<Meal> getMealsByRestaurant(Long restaurantId);
} 