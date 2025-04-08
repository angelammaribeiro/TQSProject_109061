package ua.deti.backend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.deti.backend.model.Restaurant;
import ua.deti.backend.repository.RestaurantRepository;
import ua.deti.backend.service.RestaurantService;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Override
    public List<Restaurant> getAllRestaurants() {
        logger.info("Fetching all restaurants");
        return restaurantRepository.findAll();
    }

    @Override
    public Optional<Restaurant> getRestaurantById(Long id) {
        logger.info("Fetching restaurant with id: {}", id);
        return restaurantRepository.findById(id);
    }

    @Override
    public List<Restaurant> searchRestaurants(String query) {
        logger.info("Searching restaurants with query: {}", query);
        List<Restaurant> byName = restaurantRepository.findByNameContainingIgnoreCase(query);
        List<Restaurant> byLocation = restaurantRepository.findByLocationContainingIgnoreCase(query);
        
        // Combine and remove duplicates
        byName.addAll(byLocation);
        return byName.stream().distinct().toList();
    }

    @Override
    public Restaurant createRestaurant(Restaurant restaurant) {
        logger.info("Creating new restaurant: {}", restaurant.getName());
        return restaurantRepository.save(restaurant);
    }

    @Override
    public Restaurant updateRestaurant(Long id, Restaurant restaurant) {
        logger.info("Updating restaurant with id: {}", id);
        if (!restaurantRepository.existsById(id)) {
            logger.error("Restaurant with id {} not found", id);
            throw new RuntimeException("Restaurant not found");
        }
        restaurant.setId(id);
        return restaurantRepository.save(restaurant);
    }

    @Override
    public void deleteRestaurant(Long id) {
        logger.info("Deleting restaurant with id: {}", id);
        restaurantRepository.deleteById(id);
    }
} 