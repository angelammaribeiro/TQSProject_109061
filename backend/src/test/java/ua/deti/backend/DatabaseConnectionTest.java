package ua.deti.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ua.deti.backend.config.TestContainerConfig;
import ua.deti.backend.model.Meal;
import ua.deti.backend.model.Restaurant;
import ua.deti.backend.repository.MealRepository;
import ua.deti.backend.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ContextConfiguration(classes = TestContainerConfig.class)
@Transactional
class DatabaseConnectionTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MealRepository mealRepository;

    @Test
    void testDatabaseConnection() {
        // Create and save a restaurant
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Restaurant");
        restaurant.setLocation("Aveiro");
        restaurant.setDescription("A test restaurant");
        restaurant.setCuisineType("Portuguese");
        restaurant.setContactInfo("test@restaurant.com");
        restaurant.setMeals(new ArrayList<>());

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        assertNotNull(savedRestaurant.getId());

        // Create and save a meal
        Meal meal = new Meal();
        meal.setName("Test Meal");
        meal.setDescription("A delicious test meal");
        meal.setAvailableFrom(LocalDate.now());
        meal.setAvailableTo(LocalDate.now().plusDays(7));
        meal.setPrice(15.99);
        meal.setRestaurant(savedRestaurant);
        
        savedRestaurant.getMeals().add(meal);

        Meal savedMeal = mealRepository.save(meal);
        assertNotNull(savedMeal.getId());

        // Retrieve and verify the meal
        Meal retrievedMeal = mealRepository.findById(savedMeal.getId())
                .orElse(null);
        assertNotNull(retrievedMeal);
        assertEquals("Test Meal", retrievedMeal.getName());
        assertEquals(15.99, retrievedMeal.getPrice());
        assertNotNull(retrievedMeal.getRestaurant());
        assertEquals("Test Restaurant", retrievedMeal.getRestaurant().getName());

        // Retrieve and verify the restaurant
        Restaurant retrievedRestaurant = restaurantRepository.findById(savedRestaurant.getId())
                .orElse(null);
        assertNotNull(retrievedRestaurant);
        assertEquals("Test Restaurant", retrievedRestaurant.getName());
        assertEquals("Aveiro", retrievedRestaurant.getLocation());
        
        assertNotNull(retrievedRestaurant.getMeals());
        assertEquals(1, retrievedRestaurant.getMeals().size());
        assertEquals("Test Meal", retrievedRestaurant.getMeals().get(0).getName());
    }
} 