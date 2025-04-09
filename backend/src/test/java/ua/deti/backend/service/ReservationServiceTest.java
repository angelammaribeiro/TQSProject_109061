package ua.deti.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ua.deti.backend.model.Reservation;
import ua.deti.backend.model.Restaurant;
import ua.deti.backend.repository.ReservationRepository;
import ua.deti.backend.service.impl.ReservationServiceImpl;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Restaurant testRestaurant;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test restaurant
        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");

        // Setup test reservation
        testReservation = new Reservation();
        testReservation.setRestaurant(testRestaurant);
        testReservation.setUserName("Test User");
        testReservation.setUserEmail("test@example.com");
        testReservation.setUserPhone("1234567890");
        testReservation.setReservationDate(LocalDateTime.now().plusDays(1));
    }

    @Test
    void whenRestaurantHasSpaceForReservations_shouldCreateReservation() {
        // Arrange
        when(reservationRepository.countActiveReservationsByRestaurantId(testRestaurant.getId()))
            .thenReturn(199L); // Just below limit
        when(reservationRepository.save(any(Reservation.class)))
            .thenReturn(testReservation);

        // Act
        Reservation createdReservation = reservationService.createReservation(testReservation);

        // Assert
        assertNotNull(createdReservation);
        assertNotNull(createdReservation.getToken());
        assertEquals(testRestaurant.getId(), createdReservation.getRestaurant().getId());
    }

    @Test
    void whenRestaurantIsAtCapacity_shouldThrowException() {
        // Arrange
        when(reservationRepository.countActiveReservationsByRestaurantId(testRestaurant.getId()))
            .thenReturn(200L); // At limit

        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> reservationService.createReservation(testReservation)
        );

        assertEquals("Restaurant has reached the maximum number of reservations", exception.getMessage());
    }

    @Test
    void whenCheckingReservationLimit_shouldReturnCorrectStatus() {
        // Test when under limit
        when(reservationRepository.countActiveReservationsByRestaurantId(testRestaurant.getId()))
            .thenReturn(199L);
        assertFalse(reservationService.hasReachedReservationLimit(testRestaurant.getId()));

        // Test when at limit
        when(reservationRepository.countActiveReservationsByRestaurantId(testRestaurant.getId()))
            .thenReturn(200L);
        assertTrue(reservationService.hasReachedReservationLimit(testRestaurant.getId()));

        // Test when over limit
        when(reservationRepository.countActiveReservationsByRestaurantId(testRestaurant.getId()))
            .thenReturn(201L);
        assertTrue(reservationService.hasReachedReservationLimit(testRestaurant.getId()));
    }
} 