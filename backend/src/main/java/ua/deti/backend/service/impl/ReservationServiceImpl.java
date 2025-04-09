package ua.deti.backend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.deti.backend.model.Reservation;
import ua.deti.backend.model.ReservationStatus;
import ua.deti.backend.repository.ReservationRepository;
import ua.deti.backend.service.ReservationService;

import java.time.LocalDateTime;
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
        logger.info("Creating new reservation for restaurant: {}", reservation.getRestaurant().getId());
        
        // Check if restaurant has reached reservation limit
        if (hasReachedReservationLimit(reservation.getRestaurant().getId())) {
            logger.error("Restaurant {} has reached the maximum number of reservations", reservation.getRestaurant().getId());
            throw new IllegalStateException("Restaurant has reached the maximum number of reservations");
        }
        
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
    public List<Reservation> getReservationsByRestaurant(Long restaurantId) {
        logger.info("Fetching reservations for restaurant: {}", restaurantId);
        return reservationRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public boolean hasReachedReservationLimit(Long restaurantId) {
        long activeReservations = reservationRepository.countActiveReservationsByRestaurantId(restaurantId);
        logger.info("Restaurant {} has {} active reservations", restaurantId, activeReservations);
        return activeReservations >= MAX_RESERVATIONS_PER_RESTAURANT;
    }

    @Override
    public List<Reservation> getPendingReservationsByRestaurantAndDate(Long restaurantId, LocalDateTime date) {
        logger.info("Fetching pending reservations for restaurant {} on date {}", restaurantId, date);
        return reservationRepository.findPendingReservationsByRestaurantAndDate(restaurantId, date);
    }
} 