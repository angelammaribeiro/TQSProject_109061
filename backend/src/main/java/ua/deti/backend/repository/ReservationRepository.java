package ua.deti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.deti.backend.model.Reservation;
import ua.deti.backend.model.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByToken(String token);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByRestaurantId(Long restaurantId);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.restaurant.id = :restaurantId AND r.status = 'PENDING'")
    long countActiveReservationsByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT r FROM Reservation r WHERE r.restaurant.id = :restaurantId " +
           "AND CAST(r.reservationDate AS date) = CAST(:date AS date) " +
           "AND r.status != 'COMPLETED'")
    List<Reservation> findPendingReservationsByRestaurantAndDate(
        @Param("restaurantId") Long restaurantId,
        @Param("date") LocalDateTime date);
} 