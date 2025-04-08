package ua.deti.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ua.deti.backend.config.TestConfig;
import ua.deti.backend.dto.MealDTO;
import ua.deti.backend.dto.ReservationDTO;
import ua.deti.backend.dto.RestaurantDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
public class EndToEndTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCompleteFlow() {
        // 1. Create a restaurant
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        restaurantDTO.setName("Test Restaurant");
        restaurantDTO.setLocation("Test Location");
        restaurantDTO.setDescription("Test Description");
        restaurantDTO.setCuisineType("Italian");
        restaurantDTO.setContactInfo("test@restaurant.com");

        // Remove the /api prefix in your test
        ResponseEntity<RestaurantDTO> restaurantResponse = restTemplate.postForEntity(
            "/restaurants",  // Remove /api here since context path is already configured
            createHttpEntityWithJson(restaurantDTO),  // Use helper method to set Content-Type
            RestaurantDTO.class
        );

        assertEquals(200, restaurantResponse.getStatusCodeValue());
        RestaurantDTO createdRestaurant = restaurantResponse.getBody();
        assertNotNull(createdRestaurant);
        assertNotNull(createdRestaurant.getId());

        // 2. Create a meal for the restaurant
        MealDTO mealDTO = new MealDTO();
        mealDTO.setName("Test Meal");
        mealDTO.setDescription("Test Meal Description");
        mealDTO.setAvailableFrom(LocalDate.now().plusDays(1));
        mealDTO.setAvailableTo(LocalDate.now().plusDays(7));
        mealDTO.setPrice(10.99);
        mealDTO.setRestaurantId(createdRestaurant.getId());
        
        ResponseEntity<MealDTO> mealResponse = restTemplate.postForEntity(
            "/meals",
            createHttpEntityWithJson(mealDTO),
            MealDTO.class
        );
        
        assertEquals(200, mealResponse.getStatusCodeValue());

    }

        // Helper method to create HttpEntity with JSON Content-Type
    private <T> HttpEntity<T> createHttpEntityWithJson(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(body, headers);
    }
} 