package ua.deti.backend.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.deti.backend.dto.RestaurantDTO;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidRestaurant_thenNoViolations() {
        RestaurantDTO restaurant = createValidRestaurant();
        Set<ConstraintViolation<RestaurantDTO>> violations = validator.validate(restaurant);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNameTooShort_thenViolation() {
        RestaurantDTO restaurant = createValidRestaurant();
        restaurant.setName("A");
        
        Set<ConstraintViolation<RestaurantDTO>> violations = validator.validate(restaurant);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Name must be between 2 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenNameTooLong_thenViolation() {
        RestaurantDTO restaurant = createValidRestaurant();
        restaurant.setName("A".repeat(101));
        
        Set<ConstraintViolation<RestaurantDTO>> violations = validator.validate(restaurant);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Name must be between 2 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenNameBlank_thenViolation() {
        RestaurantDTO restaurant = createValidRestaurant();
        restaurant.setName("");
        
        Set<ConstraintViolation<RestaurantDTO>> violations = validator.validate(restaurant);
        assertFalse(violations.isEmpty());
        assertEquals(2, violations.size()); // Both @NotBlank and @Size(min=2) will be triggered
    }

    @Test
    void whenLocationBlank_thenViolation() {
        RestaurantDTO restaurant = createValidRestaurant();
        restaurant.setLocation("");
        
        Set<ConstraintViolation<RestaurantDTO>> violations = validator.validate(restaurant);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Location is required", violations.iterator().next().getMessage());
    }

    @Test
    void whenDescriptionTooLong_thenViolation() {
        RestaurantDTO restaurant = createValidRestaurant();
        restaurant.setDescription("A".repeat(501));
        
        Set<ConstraintViolation<RestaurantDTO>> violations = validator.validate(restaurant);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Description cannot exceed 500 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenMissingRequiredFields_thenViolations() {
        RestaurantDTO restaurant = new RestaurantDTO();
        
        Set<ConstraintViolation<RestaurantDTO>> violations = validator.validate(restaurant);
        assertEquals(2, violations.size()); // name and location
    }

    private RestaurantDTO createValidRestaurant() {
        RestaurantDTO restaurant = new RestaurantDTO();
        restaurant.setName("Sushi Master");
        restaurant.setLocation("123 Main Street");
        restaurant.setDescription("Authentic Japanese cuisine");
        restaurant.setCuisineType("Japanese");
        restaurant.setContactInfo("+351912345678");
        return restaurant;
    }
} 