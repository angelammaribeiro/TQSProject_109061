package ua.deti.backend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.deti.backend.model.Reservation;
import ua.deti.backend.model.ReservationStatus;
import ua.deti.backend.repository.ReservationRepository;
import ua.deti.backend.service.ReservationService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Reservation createReservation(Reservation reservation) {
        logger.info("Creating new reservation for meal: {}", reservation.getMeal().getId());
        // Generate a unique token for the reservation
        reservation.setToken(UUID.randomUUID().toString());
        reservation.setStatus(ReservationStatus.PENDING);
        return reservationRepository.save(reservation);
    }

    @Override
    public Optional<Reservation> getReservationByToken(String token) {
        logger.info("Fetching reservation with token: {}", token);
        return reservationRepository.findByToken(token);
    }

    @Override
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        logger.info("Fetching reservations with status: {}", status);
        return reservationRepository.findByStatus(status);
    }

    @Override
    public Reservation updateReservationStatus(String token, ReservationStatus newStatus) {
        logger.info("Updating reservation status for token: {} to {}", token, newStatus);
        Reservation reservation = reservationRepository.findByToken(token)
            .orElseThrow(() -> {
                logger.error("Reservation with token {} not found", token);
                return new RuntimeException("Reservation not found");
            });
        
        reservation.setStatus(newStatus);
        return reservationRepository.save(reservation);
    }

    @Override
    public void cancelReservation(String token) {
        logger.info("Cancelling reservation with token: {}", token);
        updateReservationStatus(token, ReservationStatus.CANCELLED);
    }

    @Override
    public List<Reservation> getReservationsByMeal(Long mealId) {
        logger.info("Fetching reservations for meal: {}", mealId);
        return reservationRepository.findByMealId(mealId);
    }
} 