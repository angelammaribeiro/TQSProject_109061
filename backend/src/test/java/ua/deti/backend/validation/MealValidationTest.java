package ua.deti.backend.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ua.deti.backend.dto.MealDTO;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MealValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidMeal_thenNoViolations() {
        MealDTO meal = createValidMeal();
        Set<ConstraintViolation<MealDTO>> violations = validator.validate(meal);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNameTooShort_thenViolation() {
        MealDTO meal = createValidMeal();
        meal.setName("A");
        
        Set<ConstraintViolation<MealDTO>> violations = validator.validate(meal);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Name must be between 2 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenNameTooLong_thenViolation() {
        MealDTO meal = createValidMeal();
        meal.setName("A".repeat(101));
        
        Set<ConstraintViolation<MealDTO>> violations = validator.validate(meal);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Name must be between 2 and 100 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenDescriptionTooLong_thenViolation() {
        MealDTO meal = createValidMeal();
        meal.setDescription("A".repeat(501));
        
        Set<ConstraintViolation<MealDTO>> violations = validator.validate(meal);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Description cannot exceed 500 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenPriceNegative_thenViolation() {
        MealDTO meal = createValidMeal();
        meal.setPrice(-1.0);
        
        Set<ConstraintViolation<MealDTO>> violations = validator.validate(meal);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Price must be greater than 0", violations.iterator().next().getMessage());
    }

    @Test
    void whenPriceZero_thenViolation() {
        MealDTO meal = createValidMeal();
        meal.setPrice(0.0);
        
        Set<ConstraintViolation<MealDTO>> violations = validator.validate(meal);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("Price must be greater than 0", violations.iterator().next().getMessage());
    }

    @Test
    void whenMissingRequiredFields_thenViolations() {
        MealDTO meal = new MealDTO();
        
        Set<ConstraintViolation<MealDTO>> violations = validator.validate(meal);
        assertEquals(5, violations.size()); // name, availableFrom, availableTo, price, restaurantId
    }

    private MealDTO createValidMeal() {
        MealDTO meal = new MealDTO();
        meal.setName("Sushi Platter");
        meal.setDescription("Assorted sushi platter with fresh fish");
        meal.setAvailableFrom(LocalDate.now());
        meal.setAvailableTo(LocalDate.now().plusDays(7));
        meal.setPrice(25.99);
        meal.setRestaurantId(1L);
        return meal;
    }
} 