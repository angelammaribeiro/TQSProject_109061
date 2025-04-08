package ua.deti.backend.service;

import ua.deti.backend.model.Restaurant;
import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    List<Restaurant> getAllRestaurants();
    Optional<Restaurant> getRestaurantById(Long id);
    List<Restaurant> searchRestaurants(String query);
    Restaurant createRestaurant(Restaurant restaurant);
    Restaurant updateRestaurant(Long id, Restaurant restaurant);
    void deleteRestaurant(Long id);
} 