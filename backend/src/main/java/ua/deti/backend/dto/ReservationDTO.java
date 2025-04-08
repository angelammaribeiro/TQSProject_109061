package ua.deti.backend.dto;

import jakarta.validation.constraints.*;
import ua.deti.backend.model.ReservationStatus;
import java.time.LocalDateTime;

public class ReservationDTO {
    private Long id;

    @NotBlank(message = "User name is required")
    @Size(min = 2, max = 100, message = "User name must be between 2 and 100 characters")
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String userEmail;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9\\s-]{10,}$", message = "Invalid phone number format")
    private String userPhone;

    @NotNull(message = "Reservation date is required")
    @Future(message = "Reservation date must be in the future")
    private LocalDateTime reservationDate;

    private String token;
    private ReservationStatus status;
    private Long mealId;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public LocalDateTime getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Long getMealId() {
        return mealId;
    }

    public void setMealId(Long mealId) {
        this.mealId = mealId;
    }
} 