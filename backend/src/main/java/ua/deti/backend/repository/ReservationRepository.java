package ua.deti.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.deti.backend.model.Reservation;
import ua.deti.backend.model.ReservationStatus;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByToken(String token);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByMealId(Long mealId);
} 