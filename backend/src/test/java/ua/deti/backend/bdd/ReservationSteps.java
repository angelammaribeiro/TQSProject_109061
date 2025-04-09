package ua.deti.backend.bdd;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import ua.deti.backend.dto.ReservationDTO;
import ua.deti.backend.model.ReservationStatus;
import ua.deti.backend.model.Restaurant;
import ua.deti.backend.service.RestaurantService;
import ua.deti.backend.service.ReservationService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ReservationService reservationService;

    private Restaurant testRestaurant;
    private ReservationDTO reservationDTO;
    private ResponseEntity<ReservationDTO> response;
    private ResponseEntity<ReservationDTO[]> listResponse;
    private String reservationToken;

    @Given("the restaurant {string} exists")
    public void theRestaurantExists(String restaurantName) {
        testRestaurant = new Restaurant();
        testRestaurant.setName(restaurantName);
        testRestaurant.setLocation("Test Location");
        testRestaurant = restaurantService.createRestaurant(testRestaurant);
    }

    @Given("the restaurant has available capacity")
    public void theRestaurantHasAvailableCapacity() {
        assertTrue(reservationService.hasReachedReservationLimit(testRestaurant.getId()));
    }

    @When("I create a reservation with the following details:")
    public void iCreateAReservationWithTheFollowingDetails(DataTable dataTable) {
        Map<String, String> details = dataTable.asMap();
        reservationDTO = new ReservationDTO();
        reservationDTO.setUserName(details.get("userName"));
        reservationDTO.setUserEmail(details.get("userEmail"));
        reservationDTO.setUserPhone(details.get("userPhone"));
        reservationDTO.setReservationDate(LocalDateTime.parse(details.get("reservationDate")));
        reservationDTO.setRestaurantId(testRestaurant.getId());

        response = restTemplate.postForEntity("/api/reservations", reservationDTO, ReservationDTO.class);
    }

    @Then("the reservation should be created successfully")
    public void theReservationShouldBeCreatedSuccessfully() {
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Then("I should receive a confirmation token")
    public void iShouldReceiveAConfirmationToken() {
        assertNotNull(response.getBody().getToken());
    }

    @Then("the reservation status should be {string}")
    public void theReservationStatusShouldBe(String status) {
        assertEquals(ReservationStatus.valueOf(status), response.getBody().getStatus());
    }

    @Then("the reservation should fail")
    public void theReservationShouldFail() {
        assertTrue(response.getStatusCodeValue() >= 400);
    }

    @Then("I should receive an error message about invalid email")
    public void iShouldReceiveAnErrorMessageAboutInvalidEmail() {
        assertEquals(400, response.getStatusCodeValue());
    }

    @Then("I should receive an error message about invalid phone number")
    public void iShouldReceiveAnErrorMessageAboutInvalidPhoneNumber() {
        assertEquals(400, response.getStatusCodeValue());
    }

    @Then("I should receive an error message about invalid date")
    public void iShouldReceiveAnErrorMessageAboutInvalidDate() {
        assertEquals(400, response.getStatusCodeValue());
    }

    @Given("I have a valid reservation token")
    public void iHaveAValidReservationToken() {
        // Create a reservation first to get a valid token
        List<List<String>> data = Arrays.asList(
            Arrays.asList("userName", "John Doe"),
            Arrays.asList("userEmail", "john@example.com"),
            Arrays.asList("userPhone", "+351912345678"),
            Arrays.asList("reservationDate", LocalDateTime.now().plusDays(1).toString())
        );
        iCreateAReservationWithTheFollowingDetails(DataTable.create(data));
        reservationToken = response.getBody().getToken();
    }

    @When("I cancel the reservation")
    public void iCancelTheReservation() {
        response = restTemplate.exchange(
            "/api/reservations/" + reservationToken,
            org.springframework.http.HttpMethod.DELETE,
            null,
            ReservationDTO.class
        );
    }

    @Then("the reservation should be cancelled successfully")
    public void theReservationShouldBeCancelledSuccessfully() {
        assertEquals(200, response.getStatusCodeValue());
    }

    @When("I request all reservations for {string}")
    public void iRequestAllReservationsFor(String restaurantName) {
        listResponse = restTemplate.getForEntity(
            "/api/reservations/restaurant/" + testRestaurant.getId(),
            ReservationDTO[].class
        );
    }

    @Then("I should receive a list of reservations")
    public void iShouldReceiveAListOfReservations() {
        assertEquals(200, listResponse.getStatusCodeValue());
        assertNotNull(listResponse.getBody());
    }

    @Then("each reservation should contain valid user details")
    public void eachReservationShouldContainValidUserDetails() {
        ReservationDTO[] reservations = listResponse.getBody();
        for (ReservationDTO reservation : reservations) {
            assertNotNull(reservation.getUserName());
            assertNotNull(reservation.getUserEmail());
            assertNotNull(reservation.getUserPhone());
            assertNotNull(reservation.getReservationDate());
        }
    }
} 