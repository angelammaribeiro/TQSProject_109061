package ua.deti.backend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.deti.backend.model.Meal;
import ua.deti.backend.repository.MealRepository;
import ua.deti.backend.service.MealService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MealServiceImpl implements MealService {
    private static final Logger logger = LoggerFactory.getLogger(MealServiceImpl.class);

    @Autowired
    private MealRepository mealRepository;

    @Override
    public List<Meal> getUpcomingMeals(Long restaurantId, LocalDate startDate, LocalDate endDate) {
        logger.info("Fetching upcoming meals for restaurant {} between {} and {}", 
            restaurantId, startDate, endDate);
        return mealRepository.findByRestaurantIdAndAvailableFromLessThanEqualAndAvailableToGreaterThanEqual(
            restaurantId, endDate, startDate);
    }

    @Override
    public Optional<Meal> getMealById(Long id) {
        logger.info("Fetching meal with id: {}", id);
        return mealRepository.findById(id);
    }

    @Override
    public Meal createMeal(Meal meal) {
        logger.info("Creating new meal: {}", meal.getName());
        return mealRepository.save(meal);
    }

    @Override
    public Meal updateMeal(Long id, Meal meal) {
        logger.info("Updating meal with id: {}", id);
        if (!mealRepository.existsById(id)) {
            logger.error("Meal with id {} not found", id);
            throw new RuntimeException("Meal not found");
        }
        meal.setId(id);
        return mealRepository.save(meal);
    }

    @Override
    public void deleteMeal(Long id) {
        logger.info("Deleting meal with id: {}", id);
        mealRepository.deleteById(id);
    }

    @Override
    public List<Meal> getMealsByRestaurant(Long restaurantId) {
        logger.info("Fetching all meals for restaurant: {}", restaurantId);
        return mealRepository.findByRestaurantId(restaurantId);
    }
} 