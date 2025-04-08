package ua.deti.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.deti.backend.dto.RestaurantDTO;
import ua.deti.backend.model.Restaurant;
import ua.deti.backend.service.RestaurantService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        logger.info("GET /api/restaurants - Fetching all restaurants");
        List<RestaurantDTO> restaurants = restaurantService.getAllRestaurants()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurantById(@PathVariable Long id) {
        logger.info("GET /api/restaurants/{} - Fetching restaurant by id", id);
        return restaurantService.getRestaurantById(id)
            .map(this::convertToDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantDTO>> searchRestaurants(@RequestParam String query) {
        logger.info("GET /api/restaurants/search?query={} - Searching restaurants", query);
        List<RestaurantDTO> restaurants = restaurantService.searchRestaurants(query)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(restaurants);
    }

    @PostMapping
    public ResponseEntity<RestaurantDTO> createRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        logger.info("POST /api/restaurants - Creating new restaurant");
        Restaurant restaurant = convertToEntity(restaurantDTO);
        Restaurant savedRestaurant = restaurantService.createRestaurant(restaurant);
        return ResponseEntity.ok(convertToDTO(savedRestaurant));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantDTO restaurantDTO) {
        logger.info("PUT /api/restaurants/{} - Updating restaurant", id);
        Restaurant restaurant = convertToEntity(restaurantDTO);
        Restaurant updatedRestaurant = restaurantService.updateRestaurant(id, restaurant);
        return ResponseEntity.ok(convertToDTO(updatedRestaurant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        logger.info("DELETE /api/restaurants/{} - Deleting restaurant", id);
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok().build();
    }

    private RestaurantDTO convertToDTO(Restaurant restaurant) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setId(restaurant.getId());
        dto.setName(restaurant.getName());
        dto.setLocation(restaurant.getLocation());
        dto.setDescription(restaurant.getDescription());
        dto.setCuisineType(restaurant.getCuisineType());
        dto.setContactInfo(restaurant.getContactInfo());
        return dto;
    }

    private Restaurant convertToEntity(RestaurantDTO dto) {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(dto.getId());
        restaurant.setName(dto.getName());
        restaurant.setLocation(dto.getLocation());
        restaurant.setDescription(dto.getDescription());
        restaurant.setCuisineType(dto.getCuisineType());
        restaurant.setContactInfo(dto.getContactInfo());
        return restaurant;
    }
} 