package ua.deti.backend.service;

import ua.deti.backend.model.Reservation;
import ua.deti.backend.model.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    int MAX_RESERVATIONS_PER_RESTAURANT = 200;
    
    Reservation createReservation(Reservation reservation);
    Optional<Reservation> getReservationByToken(String token);
    List<Reservation> getReservationsByStatus(ReservationStatus status);
    Reservation updateReservationStatus(String token, ReservationStatus newStatus);
    void cancelReservation(String token);
    List<Reservation> getReservationsByRestaurant(Long restaurantId);
    boolean hasReachedReservationLimit(Long restaurantId);
    List<Reservation> getPendingReservationsByRestaurantAndDate(Long restaurantId, LocalDateTime date);
} 