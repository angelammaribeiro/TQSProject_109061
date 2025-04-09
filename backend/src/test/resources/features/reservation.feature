Feature: Restaurant Reservation System
  As a customer
  I want to make restaurant reservations
  So that I can secure a table for my meal

  Background:
    Given the restaurant "Test Restaurant" exists
    And the restaurant has available capacity

  Scenario: Successful reservation creation
    When I create a reservation with the following details:
      | userName    | userEmail           | userPhone      | reservationDate           |
      | John Doe    | john@example.com    | +351912345678 | 2024-12-31T19:00:00     |
    Then the reservation should be created successfully
    And I should receive a confirmation token
    And the reservation status should be "PENDING"

  Scenario: Failed reservation due to invalid email
    When I create a reservation with the following details:
      | userName    | userEmail        | userPhone      | reservationDate           |
      | John Doe    | invalid.email    | +351912345678 | 2024-12-31T19:00:00     |
    Then the reservation should fail
    And I should receive an error message about invalid email

  Scenario: Failed reservation due to invalid phone number
    When I create a reservation with the following details:
      | userName    | userEmail           | userPhone   | reservationDate           |
      | John Doe    | john@example.com    | 123456     | 2024-12-31T19:00:00     |
    Then the reservation should fail
    And I should receive an error message about invalid phone number

  Scenario: Failed reservation due to past date
    When I create a reservation with the following details:
      | userName    | userEmail           | userPhone      | reservationDate           |
      | John Doe    | john@example.com    | +351912345678 | 2020-01-01T19:00:00     |
    Then the reservation should fail
    And I should receive an error message about invalid date

  Scenario: Cancel existing reservation
    Given I have a valid reservation token
    When I cancel the reservation
    Then the reservation should be cancelled successfully
    And the reservation status should be "CANCELLED"

  Scenario: View restaurant reservations
    When I request all reservations for "Test Restaurant"
    Then I should receive a list of reservations
    And each reservation should contain valid user details 