package ua.deti.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ua.deti.backend.dto.MealDTO;
import ua.deti.backend.dto.ReservationDTO;
import ua.deti.backend.dto.RestaurantDTO;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ReservationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TestUtils testUtils;

    @BeforeEach
    public void setUp() {
        testUtils = new TestUtils();
        testUtils.restTemplate = restTemplate;
    }

    @Test
    public void testCreateAndRetrieveReservation() {
        // Use TestUtils to create a restaurant
        RestaurantDTO createdRestaurant = testUtils.createTestRestaurant();

        // Use TestUtils to create a meal for the restaurant
        MealDTO createdMeal = testUtils.createTestMeal(createdRestaurant.getId());

        // Create a reservation for the meal
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setUserName("Test User");
        reservationDTO.setUserEmail("test@user.com");
        reservationDTO.setUserPhone("987654321");
        reservationDTO.setReservationDate(LocalDateTime.now().plusDays(1));
        reservationDTO.setMealId(createdMeal.getId());

        ResponseEntity<ReservationDTO> reservationResponse = restTemplate.postForEntity(
            "/reservations",
            testUtils.createHttpEntityWithJson(reservationDTO),
            ReservationDTO.class
        );

        assertEquals(200, reservationResponse.getStatusCodeValue());
        ReservationDTO createdReservation = reservationResponse.getBody();
        assertNotNull(createdReservation);
        assertNotNull(createdReservation.getToken());

        // Retrieve the reservation by token
        ResponseEntity<ReservationDTO> getResponse = restTemplate.getForEntity(
            "/reservations/token/" + createdReservation.getToken(),
            ReservationDTO.class
        );

        assertEquals(200, getResponse.getStatusCodeValue());
        ReservationDTO retrievedReservation = getResponse.getBody();
        assertNotNull(retrievedReservation);
        assertEquals(createdReservation.getToken(), retrievedReservation.getToken());
    }
} 