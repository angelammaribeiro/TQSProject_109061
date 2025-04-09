import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

// Test configuration
export const options = {
  stages: [
    { duration: '2m', target: 50 },  // Ramp up to 50 users
    { duration: '5m', target: 50 },  // Stay at 50 users
    { duration: '2m', target: 0 },   // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ['p(95)<200'], // 95% of requests should be below 200ms
    http_req_failed: ['rate<0.01'],   // Error rate should be below 1%
    errors: ['rate<0.01'],            // Custom error rate should be below 1%
  },
};

// Base URL for the API
const BASE_URL = 'http://localhost:8080/api';

// Test data
const testRestaurant = {
  name: 'Test Restaurant',
  address: 'Test Address',
  cuisine: 'Test Cuisine',
  rating: 4.5,
  priceRange: 'MEDIUM',
  openingHours: '09:00-22:00',
  capacity: 100
};

const testReservation = {
  userName: 'Test User',
  userEmail: 'test@example.com',
  userPhone: '+351912345678',
  reservationDate: '2024-12-31T19:00:00',
  numberOfPeople: 4,
  restaurantId: 1
};

// Helper function to create a restaurant
function createRestaurant() {
  const response = http.post(`${BASE_URL}/restaurants`, JSON.stringify(testRestaurant), {
    headers: { 'Content-Type': 'application/json' },
  });
  
  check(response, {
    'restaurant created successfully': (r) => r.status === 201,
  });

  try {
    return response.json();
  } catch (e) {
    console.error('Failed to parse restaurant response:', e);
    return null;
  }
}

// Helper function to create a reservation
function createReservation(restaurantId) {
  const reservation = { ...testReservation, restaurantId };
  const response = http.post(`${BASE_URL}/reservations`, JSON.stringify(reservation), {
    headers: { 'Content-Type': 'application/json' },
  });
  
  check(response, {
    'reservation created successfully': (r) => r.status === 201,
  });

  try {
    return response.json();
  } catch (e) {
    console.error('Failed to parse reservation response:', e);
    return null;
  }
}

// Helper function to safely get restaurant details
function getRestaurantDetails(restaurantId) {
  const response = http.get(`${BASE_URL}/restaurants/${restaurantId}`);
  check(response, {
    'restaurant retrieved successfully': (r) => r.status === 200,
  });
  return response;
}

// Helper function to safely get reservation details
function getReservationDetails(reservationId) {
  const response = http.get(`${BASE_URL}/reservations/${reservationId}`);
  check(response, {
    'reservation retrieved successfully': (r) => r.status === 200,
  });
  return response;
}

// Main test function
export default function () {
  // Create a restaurant
  const restaurant = createRestaurant();
  if (restaurant === null) {
    errorRate.add(1);
    sleep(1);
    return;
  }

  // Create a reservation
  const reservation = createReservation(restaurant.id);
  if (reservation === null) {
    errorRate.add(1);
    sleep(1);
    return;
  }

  // Get restaurant details
  const getRestaurantResponse = getRestaurantDetails(restaurant.id);
  errorRate.add(getRestaurantResponse.status !== 200);

  // Get reservation details
  const getReservationResponse = getReservationDetails(reservation.id);
  errorRate.add(getReservationResponse.status !== 200);

  // Search restaurants
  const searchResponse = http.get(`${BASE_URL}/restaurants/search?cuisine=Test`);
  check(searchResponse, {
    'search completed successfully': (r) => r.status === 200,
  });
  errorRate.add(searchResponse.status !== 200);

  sleep(1);
} 