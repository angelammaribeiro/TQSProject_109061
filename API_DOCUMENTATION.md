# Moliceiro University API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
Currently, the API does not require authentication. All endpoints are publicly accessible.

## Endpoints

### Weather API

#### Get Weather Forecast
```http
GET /weather/forecast
```

**Query Parameters:**
- `location` (required): String - The location to get weather for (e.g., "Aveiro")
- `date` (required): DateTime - The date and time for the forecast (ISO format)

**Example Request:**
```javascript
fetch('http://localhost:8080/api/weather/forecast?location=Aveiro&date=2024-03-20T12:00:00')
  .then(response => response.json())
  .then(data => console.log(data));
```

**Response:**
```json
{
  "id": 1,
  "location": "Aveiro",
  "date": "2024-03-20T12:00:00",
  "forecast": "Sunny, 25°C",
  "timestamp": "2024-03-20T10:00:00"
}
```

#### Get Cache Statistics
```http
GET /weather/cache/stats
```

**Example Request:**
```javascript
fetch('http://localhost:8080/api/weather/cache/stats')
  .then(response => response.json())
  .then(data => console.log(data));
```

**Response:**
```json
{
  "totalRequests": 100,
  "hits": 80,
  "misses": 20
}
```

### Restaurant API

#### Get All Restaurants
```http
GET /restaurants
```

**Example Request:**
```javascript
fetch('http://localhost:8080/api/restaurants')
  .then(response => response.json())
  .then(data => console.log(data));
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Test Restaurant",
    "location": "Aveiro",
    "description": "A test restaurant",
    "cuisineType": "Portuguese",
    "contactInfo": "test@restaurant.com"
  }
]
```

#### Get Restaurant by ID
```http
GET /restaurants/{id}
```

**Example Request:**
```javascript
fetch('http://localhost:8080/api/restaurants/1')
  .then(response => response.json())
  .then(data => console.log(data));
```

**Response:**
```json
{
  "id": 1,
  "name": "Test Restaurant",
  "location": "Aveiro",
  "description": "A test restaurant",
  "cuisineType": "Portuguese",
  "contactInfo": "test@restaurant.com"
}
```

### Meal API

#### Get All Meals
```http
GET /meals
```

**Example Request:**
```javascript
fetch('http://localhost:8080/api/meals')
  .then(response => response.json())
  .then(data => console.log(data));
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Test Meal",
    "description": "A delicious test meal",
    "availableFrom": "2024-03-20",
    "availableTo": "2024-03-27",
    "price": 15.99,
    "restaurantId": 1
  }
]
```

#### Get Meals by Restaurant
```http
GET /restaurants/{restaurantId}/meals
```

**Example Request:**
```javascript
fetch('http://localhost:8080/api/restaurants/1/meals')
  .then(response => response.json())
  .then(data => console.log(data));
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Test Meal",
    "description": "A delicious test meal",
    "availableFrom": "2024-03-20",
    "availableTo": "2024-03-27",
    "price": 15.99,
    "restaurantId": 1
  }
]
```

### Reservation API

#### Create Reservation
```http
POST /reservations
```

**Request Body:**
```json
{
  "userName": "John Doe",
  "userEmail": "john@example.com",
  "userPhone": "+351123456789",
  "reservationDate": "2024-03-20T19:00:00",
  "mealId": 1
}
```

**Example Request:**
```javascript
fetch('http://localhost:8080/api/reservations', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    userName: "John Doe",
    userEmail: "john@example.com",
    userPhone: "+351123456789",
    reservationDate: "2024-03-20T19:00:00",
    mealId: 1
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

**Response:**
```json
{
  "id": 1,
  "userName": "John Doe",
  "userEmail": "john@example.com",
  "userPhone": "+351123456789",
  "reservationDate": "2024-03-20T19:00:00",
  "token": "abc123",
  "status": "PENDING",
  "mealId": 1
}
```

#### Get Reservation by Token
```http
GET /reservations/{token}
```

**Example Request:**
```javascript
fetch('http://localhost:8080/api/reservations/abc123')
  .then(response => response.json())
  .then(data => console.log(data));
```

**Response:**
```json
{
  "id": 1,
  "userName": "John Doe",
  "userEmail": "john@example.com",
  "userPhone": "+351123456789",
  "reservationDate": "2024-03-20T19:00:00",
  "token": "abc123",
  "status": "PENDING",
  "mealId": 1
}
```

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-03-20T10:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid input data"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-03-20T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-03-20T10:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

## Notes
- All dates and times should be in ISO 8601 format
- Phone numbers should include country code (e.g., +351 for Portugal)
- Email addresses should be valid
- Prices are in euros (€)
- The API supports CORS for frontend development 