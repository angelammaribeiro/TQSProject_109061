package ua.deti.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import ua.deti.backend.dto.MealDTO;
import ua.deti.backend.dto.RestaurantDTO;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MealIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TestUtils testUtils;

    @BeforeEach
    public void setUp() {
        testUtils = new TestUtils();
        testUtils.restTemplate = restTemplate;
    }

    @Test
    public void testCreateMealForRestaurant() {
        // Use TestUtils to create a restaurant
        RestaurantDTO createdRestaurant = testUtils.createTestRestaurant();

        // Use TestUtils to create a meal for the restaurant
        MealDTO createdMeal = testUtils.createTestMeal(createdRestaurant.getId());

        assertNotNull(createdMeal);
        assertNotNull(createdMeal.getId());
        assertEquals("Test Meal", createdMeal.getName());
    }
} 