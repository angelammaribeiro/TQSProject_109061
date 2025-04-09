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
        MealDTO createdMeal = mealResponse.getBody();
        assertNotNull(createdMeal);
        assertNotNull(createdMeal.getId());

        // 3. Create a reservation for the meal
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setUserName("Test User");
        reservationDTO.setUserEmail("test@user.com");
        reservationDTO.setUserPhone("987654321");
        reservationDTO.setReservationDate(LocalDateTime.now().plusDays(1));
        reservationDTO.setMealId(createdMeal.getId());

        ResponseEntity<ReservationDTO> reservationResponse = restTemplate.postForEntity(
            "/reservations",
            createHttpEntityWithJson(reservationDTO),
            ReservationDTO.class
        );

        assertEquals(200, reservationResponse.getStatusCodeValue());
        ReservationDTO createdReservation = reservationResponse.getBody();
        assertNotNull(createdReservation);
        assertNotNull(createdReservation.getToken());
        assertEquals("PENDING", createdReservation.getStatus());

        // 4. Get the reservation by token
        ResponseEntity<ReservationDTO> getReservationResponse = restTemplate.getForEntity(
            "/reservations/token/" + createdReservation.getToken(),
            ReservationDTO.class
        );

        assertEquals(200, getReservationResponse.getStatusCodeValue());
        ReservationDTO retrievedReservation = getReservationResponse.getBody();
        assertNotNull(retrievedReservation);
        assertEquals(createdReservation.getToken(), retrievedReservation.getToken());

        // 5. Cancel the reservation
        ResponseEntity<Void> cancelResponse = restTemplate.exchange(
            "/api/reservations/" + retrievedReservation.getToken() + "/cancel",
            HttpMethod.PUT,
            null,
            Void.class
        );

        assertEquals(200, cancelResponse.getStatusCodeValue());

        // 6. Verify the reservation was cancelled
        ResponseEntity<ReservationDTO> verifyResponse = restTemplate.getForEntity(
            "/api/reservations/token/" + createdReservation.getToken(),
            ReservationDTO.class
        );

        assertEquals(200, verifyResponse.getStatusCodeValue());
        ReservationDTO cancelledReservation = verifyResponse.getBody();
        assertNotNull(cancelledReservation);
        assertEquals("CANCELLED", cancelledReservation.getStatus());

    }

        // Helper method to create HttpEntity with JSON Content-Type
    private <T> HttpEntity<T> createHttpEntityWithJson(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(body, headers);
    }
} 