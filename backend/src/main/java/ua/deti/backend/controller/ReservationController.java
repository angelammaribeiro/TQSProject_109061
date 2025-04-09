package ua.deti.backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.deti.backend.dto.ReservationDTO;
import ua.deti.backend.dto.CreateReservationDTO;
import ua.deti.backend.model.Reservation;
import ua.deti.backend.model.Restaurant;
import ua.deti.backend.model.ReservationStatus;
import ua.deti.backend.service.ReservationService;
import ua.deti.backend.service.MealService;
import ua.deti.backend.service.RestaurantService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private MealService mealService;

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping
    public String Hello(){
        return "Hello World";
    }

    @PostMapping(value = "/create-by-name", consumes = "application/json")
    public ResponseEntity<ReservationDTO> createReservationByRestaurantName(@RequestBody CreateReservationDTO createDTO) {
        logger.info("POST /api/reservations/create-by-name - Creating new reservation for restaurant: {}", createDTO.getRestaurantName());
        try {
            // Find restaurant by name
            Optional<Restaurant> restaurant = restaurantService.getRestaurantByName(createDTO.getRestaurantName());
            if (restaurant.isEmpty()) {
                logger.error("Restaurant not found with name: {}", createDTO.getRestaurantName());
                return ResponseEntity.notFound().build();
            }

            // Check reservation limit before creating
            if (reservationService.hasReachedReservationLimit(restaurant.get().getId())) {
                logger.error("Restaurant {} has reached maximum reservation limit", createDTO.getRestaurantName());
                return ResponseEntity.unprocessableEntity().build();
            }

            // Create reservation
            Reservation reservation = new Reservation();
            reservation.setUserName(createDTO.getUserName());
            reservation.setUserEmail(createDTO.getUserEmail());
            reservation.setUserPhone(createDTO.getUserPhone());
            reservation.setReservationDate(createDTO.getReservationDate());
            reservation.setRestaurant(restaurant.get());

            // Save reservation
            Reservation savedReservation = reservationService.createReservation(reservation);
            return ResponseEntity.ok(convertToDTO(savedReservation));
        } catch (IllegalStateException e) {
            logger.error("Restaurant has reached maximum reservation limit: {}", e.getMessage());
            return ResponseEntity.unprocessableEntity().build();
        } catch (Exception e) {
            logger.error("Error creating reservation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ReservationDTO> createReservation(@RequestBody ReservationDTO reservationDTO) {
        logger.info("POST /api/reservations - Creating new reservation: {}", reservationDTO);
        try {
            // Check reservation limit before creating
            if (reservationService.hasReachedReservationLimit(reservationDTO.getRestaurantId())) {
                logger.error("Restaurant {} has reached maximum reservation limit", reservationDTO.getRestaurantId());
                return ResponseEntity.unprocessableEntity().build();
            }

            Reservation reservation = convertToEntity(reservationDTO);
            Reservation savedReservation = reservationService.createReservation(reservation);
            ReservationDTO response = convertToDTO(savedReservation);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            logger.error("Restaurant has reached maximum reservation limit: {}", e.getMessage());
            return ResponseEntity.unprocessableEntity().build();
        } catch (Exception e) {
            logger.error("Error creating reservation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<ReservationDTO> getReservationByToken(@PathVariable String token) {
        logger.info("GET /api/reservations/token/{} - Fetching reservation by token", token);
        return reservationService.getReservationByToken(token)
            .map(this::convertToDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByStatus(
            @PathVariable ReservationStatus status) {
        logger.info("GET /api/reservations/status/{} - Fetching reservations by status", status);
        List<ReservationDTO> reservations = reservationService.getReservationsByStatus(status)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/{token}/status")
    public ResponseEntity<ReservationDTO> updateReservationStatus(
            @PathVariable String token,
            @RequestParam ReservationStatus newStatus) {
        logger.info("PUT /api/reservations/{}/status - Updating reservation status to {}", token, newStatus);
        try {
            Reservation updatedReservation = reservationService.updateReservationStatus(token, newStatus);
            return ResponseEntity.ok(convertToDTO(updatedReservation));
        } catch (Exception e) {
            logger.error("Error updating reservation status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Void> cancelReservation(@PathVariable String token) {
        logger.info("DELETE /api/reservations/{} - Cancelling reservation", token);
        try {
            reservationService.cancelReservation(token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error cancelling reservation: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByRestaurant(@PathVariable Long restaurantId) {
        logger.info("GET /api/reservations/restaurant/{} - Fetching reservations by restaurant", restaurantId);
        List<ReservationDTO> reservations = reservationService.getReservationsByRestaurant(restaurantId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/restaurant/{restaurantId}/date")
    public ResponseEntity<List<ReservationDTO>> getPendingReservationsByRestaurantAndDate(
            @PathVariable Long restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        logger.info("GET /api/reservations/restaurant/{}/date - Fetching pending reservations for date {}", restaurantId, date);
        try {
            List<ReservationDTO> reservations = reservationService.getPendingReservationsByRestaurantAndDate(restaurantId, date)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(reservations);
        } catch (Exception e) {
            logger.error("Error fetching reservations: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setUserName(reservation.getUserName());
        dto.setUserEmail(reservation.getUserEmail());
        dto.setUserPhone(reservation.getUserPhone());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setToken(reservation.getToken());
        dto.setStatus(reservation.getStatus());
        dto.setRestaurantId(reservation.getRestaurant().getId());
        return dto;
    }

    private Reservation convertToEntity(ReservationDTO dto) {
        logger.info("Converting DTO to entity: {}", dto);
        
        // Validate input
        if (dto == null) {
            throw new IllegalArgumentException("ReservationDTO cannot be null");
        }
        
        if (dto.getRestaurantId() == null) {
            throw new IllegalArgumentException("Restaurant ID is required");
        }
        
        Reservation reservation = new Reservation();
        reservation.setId(dto.getId());
        reservation.setUserName(dto.getUserName());
        reservation.setUserEmail(dto.getUserEmail());
        reservation.setUserPhone(dto.getUserPhone());
        reservation.setReservationDate(dto.getReservationDate());
        reservation.setToken(dto.getToken());
        reservation.setStatus(dto.getStatus());
        reservation.setRestaurant(restaurantService.getRestaurantById(dto.getRestaurantId())
            .orElseThrow(() -> {
                logger.error("Restaurant not found with id: {}", dto.getRestaurantId());
                return new IllegalArgumentException("Restaurant not found with id: " + dto.getRestaurantId());
            }));
        
        return reservation;
    }
} 