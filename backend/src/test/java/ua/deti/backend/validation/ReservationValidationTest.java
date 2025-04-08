package ua.deti.backend.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.deti.backend.dto.ReservationDTO;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ReservationValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidReservation_thenNoViolations() {
        ReservationDTO reservation = createValidReservation();
        Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenReservationDateInPast_thenViolation() {
        ReservationDTO reservation = createValidReservation();
        reservation.setReservationDate(LocalDateTime.now().minusDays(1));
        
        Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Reservation date must be in the future", violations.iterator().next().getMessage());
    }

    @Test
    void whenUserNameTooShort_thenViolation() {
        ReservationDTO reservation = createValidReservation();
        reservation.setUserName("A");
        
        Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("User name must be between 2 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenUserNameTooLong_thenViolation() {
        ReservationDTO reservation = createValidReservation();
        reservation.setUserName("A".repeat(101));
        
        Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("User name must be between 2 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenInvalidEmail_thenViolation() {
        ReservationDTO reservation = createValidReservation();
        reservation.setUserEmail("invalid-email");
        
        Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    void whenInvalidPhoneNumber_thenViolation() {
        ReservationDTO reservation = createValidReservation();
        reservation.setUserPhone("123"); // Too short
        
        Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Invalid phone number format", violations.iterator().next().getMessage());
    }

    @Test
    void whenMissingRequiredFields_thenViolations() {
        ReservationDTO reservation = new ReservationDTO();
        
        Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
        assertEquals(4, violations.size()); // userName, userEmail, userPhone, reservationDate
    }

    private ReservationDTO createValidReservation() {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setUserName("John Doe");
        reservation.setUserEmail("john@example.com");
        reservation.setUserPhone("+351912345678");
        reservation.setReservationDate(LocalDateTime.now().plusDays(1));
        reservation.setMealId(1L);
        return reservation;
    }
} 