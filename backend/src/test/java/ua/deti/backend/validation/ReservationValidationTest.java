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
        assertEquals(5, violations.size()); // userName, userEmail, userPhone, reservationDate, restaurantId
    }

    @Test
    void whenInvalidEmailFormat_thenViolation() {
        ReservationDTO reservation = createValidReservation();
        
        // Test various invalid email formats
        String[] invalidEmails = {
            "invalid.email",
            "@domain.com",
            "user@",
            "user@.com",
            "user@domain.",
            "user name@domain.com",
            "user@domain..com",
            "user@domain.c",
            "user.@domain.com",
            ".user@domain.com"
        };

        for (String invalidEmail : invalidEmails) {
            reservation.setUserEmail(invalidEmail);
            Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
            assertFalse(violations.isEmpty(), "Should fail for email: " + invalidEmail);
            assertEquals(1, violations.size(), "Should have exactly one violation for email: " + invalidEmail);
            assertEquals("Email should be valid", violations.iterator().next().getMessage());
        }
    }

    @Test
    void whenValidEmailFormat_thenNoViolation() {
        ReservationDTO reservation = createValidReservation();
        
        // Test various valid email formats
        String[] validEmails = {
            "user@domain.com",
            "user.name@domain.com",
            "user+label@domain.com",
            "user@sub.domain.com",
            "user@domain.co.uk",
            "user@domain.pt",
            "user123@domain.com",
            "user@domain-name.com",
            "user@domain-name.pt"
        };

        for (String validEmail : validEmails) {
            reservation.setUserEmail(validEmail);
            Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
            assertTrue(violations.isEmpty(), "Should pass for email: " + validEmail);
        }
    }

    @Test
    void whenInvalidPortuguesePhoneNumber_thenViolation() {
        ReservationDTO reservation = createValidReservation();
        
        // Test various invalid Portuguese phone formats
        String[] invalidPhones = {
            "123", // Too short
            "123456789012", // Too long
            "abcdefghijk", // Non-numeric
            "+351123", // Too short with country code
            "+351123456789012", // Too long with country code
            "912345", // Incomplete number
            "812345678", // Wrong prefix
            "91234567890", // Wrong format
            "+35191234567890" // Wrong format with country code
        };

        for (String invalidPhone : invalidPhones) {
            reservation.setUserPhone(invalidPhone);
            Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
            assertFalse(violations.isEmpty(), "Should fail for phone: " + invalidPhone);
            assertEquals("Invalid phone number format", violations.iterator().next().getMessage());
        }
    }

    @Test
    void whenValidPortuguesePhoneNumber_thenNoViolation() {
        ReservationDTO reservation = createValidReservation();
        
        // Test various valid Portuguese phone formats
        String[] validPhones = {
            "912345678", // Mobile without country code
        };

        for (String validPhone : validPhones) {
            reservation.setUserPhone(validPhone);
            Set<ConstraintViolation<ReservationDTO>> violations = validator.validate(reservation);
            assertTrue(violations.isEmpty(), "Should pass for phone: " + validPhone);
        }
    }

    private ReservationDTO createValidReservation() {
        ReservationDTO reservation = new ReservationDTO();
        reservation.setUserName("John Doe");
        reservation.setUserEmail("john@example.com");
        reservation.setUserPhone("+351912345678");
        reservation.setReservationDate(LocalDateTime.now().plusDays(1));
        reservation.setRestaurantId(1L);
        return reservation;
    }
} 