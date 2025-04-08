package ua.deti.backend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ua.deti.backend.dto.RestaurantDTO;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestaurantIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testCreateAndRetrieveRestaurant() {
        // Create a new restaurant
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        restaurantDTO.setName("Integration Test Restaurant");
        restaurantDTO.setLocation("Integration Test Location");
        restaurantDTO.setDescription("Integration Test Description");
        restaurantDTO.setCuisineType("Fusion");
        restaurantDTO.setContactInfo("contact@integrationtest.com");

        ResponseEntity<RestaurantDTO> createResponse = restTemplate.postForEntity(
            "/restaurants",
            createHttpEntityWithJson(restaurantDTO),
            RestaurantDTO.class
        );

        assertEquals(200, createResponse.getStatusCodeValue());
        RestaurantDTO createdRestaurant = createResponse.getBody();
        assertNotNull(createdRestaurant);
        assertNotNull(createdRestaurant.getId());

        // Retrieve the restaurant by ID
        ResponseEntity<RestaurantDTO> getResponse = restTemplate.getForEntity(
            "/restaurants/" + createdRestaurant.getId(),
            RestaurantDTO.class
        );

        assertEquals(200, getResponse.getStatusCodeValue());
        RestaurantDTO retrievedRestaurant = getResponse.getBody();
        assertNotNull(retrievedRestaurant);
        assertEquals(createdRestaurant.getId(), retrievedRestaurant.getId());
        assertEquals(restaurantDTO.getName(), retrievedRestaurant.getName());
    }

            // Helper method to create HttpEntity with JSON Content-Type
    private <T> HttpEntity<T> createHttpEntityWithJson(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(body, headers);
    }
} 