package ua.deti.backend.service;

import ua.deti.backend.model.Reservation;
import ua.deti.backend.model.ReservationStatus;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Reservation createReservation(Reservation reservation);
    Optional<Reservation> getReservationByToken(String token);
    List<Reservation> getReservationsByStatus(ReservationStatus status);
    Reservation updateReservationStatus(String token, ReservationStatus newStatus);
    void cancelReservation(String token);
    List<Reservation> getReservationsByMeal(Long mealId);
} 