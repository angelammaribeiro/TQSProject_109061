# TQS Project

This project consists of a Spring Boot backend and a React frontend application. Below you'll find instructions for running the project both with and without Docker.

## Project Structure

- `backend/` - Spring Boot application
- `frontend/` - React application
- `docker-compose.yml` - Docker configuration (coming soon)

## Running Without Docker

### Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- Maven
- npm or yarn

### Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   ./mvnw clean install
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

The backend will be available at `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm run dev
   ```

The frontend will be available at `http://localhost:5173`

## Running With Docker (Coming Soon)

Docker support is currently under development. The project includes Dockerfile configurations for both frontend and backend services, as well as a docker-compose.yml file. These will be fully implemented in a future update to provide a containerized deployment solution.

## Worker Dashboard Access

To access the worker dashboard, use the following credentials:
- Password: `password`

## Additional Documentation

- For API documentation, see `API_DOCUMENTATION.md`
- For performance testing information, see `backend/README-PERFORMANCE.md`