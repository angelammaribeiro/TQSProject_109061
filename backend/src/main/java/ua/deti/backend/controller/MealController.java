package ua.deti.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.deti.backend.dto.MealDTO;
import ua.deti.backend.model.Meal;
import ua.deti.backend.model.Restaurant;
import ua.deti.backend.service.MealService;
import ua.deti.backend.service.RestaurantService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/meals")
public class MealController {
    private static final Logger logger = LoggerFactory.getLogger(MealController.class);

    @Autowired
    private MealService mealService;

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public String Hello(){
        return "Hello World";
    }

    @GetMapping("/restaurant/{restaurantId}/upcoming")
    public ResponseEntity<List<MealDTO>> getUpcomingMeals(
            @PathVariable Long restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        logger.info("GET /meals/restaurant/{}/upcoming - Fetching upcoming meals", restaurantId);
        List<MealDTO> meals = mealService.getUpcomingMeals(restaurantId, startDate, endDate)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/restaurant/{restaurantId}/date/{date}")
    public ResponseEntity<List<MealDTO>> getMealsByRestaurantAndDate(
            @PathVariable Long restaurantId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("GET /meals/restaurant/{}/date/{} - Fetching meals for specific date", restaurantId, date);
        List<MealDTO> meals = mealService.getMealsByRestaurantAndDate(restaurantId, date)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealDTO> getMealById(@PathVariable Long id) {
        logger.info("GET /meals/{} - Fetching meal by id", id);
        return mealService.getMealById(id)
            .map(this::convertToDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MealDTO> createMeal(@RequestBody MealDTO mealDTO) {
        logger.info("Received POST request to create meal: {}", mealDTO);
        try {
            Meal meal = convertToEntity(mealDTO);
            Meal savedMeal = mealService.createMeal(meal);
            MealDTO response = convertToDTO(savedMeal);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating meal: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealDTO> updateMeal(
            @PathVariable Long id,
            @RequestBody MealDTO mealDTO) {
        logger.info("PUT /meals/{} - Updating meal", id);
        Meal meal = convertToEntity(mealDTO);
        Meal updatedMeal = mealService.updateMeal(id, meal);
        return ResponseEntity.ok(convertToDTO(updatedMeal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id) {
        logger.info("DELETE /meals/{} - Deleting meal", id);
        mealService.deleteMeal(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MealDTO>> getMealsByRestaurant(@PathVariable Long restaurantId) {
        logger.info("GET /meals/restaurant/{} - Fetching meals by restaurant", restaurantId);
        List<MealDTO> meals = mealService.getMealsByRestaurant(restaurantId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(meals);
    }

    private MealDTO convertToDTO(Meal meal) {
        MealDTO dto = new MealDTO();
        dto.setId(meal.getId());
        dto.setName(meal.getName());
        dto.setDescription(meal.getDescription());
        dto.setAvailableFrom(meal.getAvailableFrom());
        dto.setAvailableTo(meal.getAvailableTo());
        dto.setPrice(meal.getPrice());
        dto.setRestaurantId(meal.getRestaurant().getId());
        return dto;
    }

    private Meal convertToEntity(MealDTO dto) {
        logger.info("Converting DTO to entity: {}", dto);
        
        // Validate input
        if (dto == null) {
            throw new IllegalArgumentException("MealDTO cannot be null");
        }
        
        if (dto.getRestaurantId() == null) {
            throw new IllegalArgumentException("Restaurant ID is required");
        }
        
        Restaurant restaurant = restaurantService.getRestaurantById(dto.getRestaurantId())
            .orElseThrow(() -> {
                logger.error("Restaurant not found with id: {}", dto.getRestaurantId());
                return new IllegalArgumentException("Restaurant not found with id: " + dto.getRestaurantId());
            });
        
        Meal meal = new Meal();
        meal.setId(dto.getId());  // This will be null for new meals
        meal.setName(dto.getName());
        meal.setDescription(dto.getDescription());
        meal.setAvailableFrom(dto.getAvailableFrom());
        meal.setAvailableTo(dto.getAvailableTo());
        meal.setPrice(dto.getPrice());
        meal.setRestaurant(restaurant);
        
        return meal;
    }
} 