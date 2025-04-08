package ua.deti.backend.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import ua.deti.backend.dto.MealDTO;
import ua.deti.backend.dto.RestaurantDTO;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestUtils {

    @Autowired
    TestRestTemplate restTemplate;

    public RestaurantDTO createTestRestaurant() {
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        restaurantDTO.setName("Test Restaurant");
        restaurantDTO.setLocation("Test Location");
        restaurantDTO.setDescription("Test Description");
        restaurantDTO.setCuisineType("Fusion");
        restaurantDTO.setContactInfo("contact@test.com");

        ResponseEntity<RestaurantDTO> response = restTemplate.postForEntity(
            "/restaurants",
            createHttpEntityWithJson(restaurantDTO),
            RestaurantDTO.class
        );

        assertEquals(200, response.getStatusCodeValue());
        RestaurantDTO createdRestaurant = response.getBody();
        assertNotNull(createdRestaurant);
        assertNotNull(createdRestaurant.getId());
        return createdRestaurant;
    }

    public MealDTO createTestMeal(Long restaurantId) {
        MealDTO mealDTO = new MealDTO();
        mealDTO.setName("Test Meal");
        mealDTO.setDescription("Test Meal Description");
        mealDTO.setAvailableFrom(LocalDate.now().plusDays(1));
        mealDTO.setAvailableTo(LocalDate.now().plusDays(7));
        mealDTO.setPrice(15.99);
        mealDTO.setRestaurantId(restaurantId);

        ResponseEntity<MealDTO> response = restTemplate.postForEntity(
            "/meals",
            mealDTO,
            MealDTO.class
        );

        assertEquals(200, response.getStatusCodeValue());
        MealDTO createdMeal = response.getBody();
        assertNotNull(createdMeal);
        assertNotNull(createdMeal.getId());
        return createdMeal;
    }

    public <T> HttpEntity<T> createHttpEntityWithJson(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(body, headers);
    }
} 
