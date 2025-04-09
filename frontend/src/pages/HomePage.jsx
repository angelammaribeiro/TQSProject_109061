// HomePage.jsx
import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import RestaurantCard from '../components/HomePage/RestaurantCard';
import SearchBar from '../components/HomePage/SearchBar';
import WeatherForecast from '../components/HomePage/WeatherForecast';
import '../css/HomePage/HomePage.css';

const BACKEND_URL = 'http://localhost:8080/api'; // Add backend base URL

const HomePage = () => {
  const [restaurants, setRestaurants] = useState([]);
  const [filteredRestaurants, setFilteredRestaurants] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [weatherData, setWeatherData] = useState(null);
  const [weatherLoading, setWeatherLoading] = useState(false);

  useEffect(() => {
    const fetchRestaurants = async () => {
      try {
        setIsLoading(true);
        const response = await fetch(`${BACKEND_URL}/restaurants`);

        if (!response.ok) {
          throw new Error(`Error: ${response.status}`);
        }

        const data = await response.json();

        // Log the raw data to understand its structure
        console.log('Raw restaurant data:', data);

        // Flatten the data if it's a nested array, otherwise use as-is
        const restaurantsData = Array.isArray(data)
            ? (Array.isArray(data[0]) ? data[0] : data)
            : (data.content || data); // Add an additional check for .content property

        setRestaurants(restaurantsData);
        setFilteredRestaurants(restaurantsData);
      } catch (err) {
        setError(err.message);
        console.error('Failed to fetch restaurants:', err);
      } finally {
        setIsLoading(false);
      }
    };

    // Remove mock data and use actual fetch method
    fetchRestaurants();
  }, []);

  // Fetch weather data when date changes
  useEffect(() => {
    const fetchWeatherData = async () => {
      try {
        setWeatherLoading(true);

        // Format date to ISO string for the API (YYYY-MM-DDTHH:MM:SS)
        const formattedDate = `${selectedDate.toISOString().split('T')[0]}T10:00:00`;

        // Default to Aveiro location
        const location = "Aveiro";

        // Use the correct API URL format based on the Postman screenshot
        const url = `${BACKEND_URL}/weather/forecast?location=${location}&date=${formattedDate}`;

        console.log('Fetching weather from:', url);
        const response = await fetch(url);

        if (!response.ok) {
          throw new Error(`Error: ${response.status}`);
        }

        const data = await response.json();
        console.log('Weather data received:', data);
        setWeatherData(data);
      } catch (err) {
        console.error('Failed to fetch weather data:', err);
        setWeatherData(null);
      } finally {
        setWeatherLoading(false);
      }
    };

    fetchWeatherData();
  }, [selectedDate]);

  const handleSearch = (query) => {
    if (!query.trim()) {
      setFilteredRestaurants(restaurants);
      return;
    }

    const filtered = restaurants.filter(restaurant =>
        restaurant.name.toLowerCase().includes(query.toLowerCase()) ||
        restaurant.cuisineType.toLowerCase().includes(query.toLowerCase()) ||
        restaurant.location.toLowerCase().includes(query.toLowerCase())
    );

    setFilteredRestaurants(filtered);
  };

  const handleDateChange = (date) => {
    setSelectedDate(date);
  };

  if (isLoading) {
    return <div className="loading-container">Loading restaurants...</div>;
  }

  if (error) {
    return <div className="error-container">Error loading restaurants: {error}</div>;
  }

  return (
      <div className="home-page">
        <div className="hero-section">
          <div className="hero-content">
            <h1>Student Dining Options</h1>
            <p>Find and book your meals across campus restaurants</p>
          </div>
        </div>

        <div className="weather-section">
          <WeatherForecast
              weatherData={weatherData}
              isLoading={weatherLoading}
              selectedDate={selectedDate}
              onDateChange={handleDateChange}
          />
        </div>

        <div className="search-section">
          <SearchBar onSearch={handleSearch} />
        </div>

        <div className="restaurants-container">
          {filteredRestaurants.length === 0 ? (
              <div className="no-results">
                <p>No restaurants found. Try a different search term.</p>
              </div>
          ) : (
              <div className="restaurants-grid">
                {filteredRestaurants.map(restaurant => (
                    <RestaurantCard 
                        key={restaurant.id}
                        restaurant={restaurant} 
                        selectedDate={selectedDate}
                    />
                ))}
              </div>
          )}
        </div>
      </div>
  );
};

export default HomePage;