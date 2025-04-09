// WeatherForecast.jsx
import React from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import '../../css/HomePage/WeatherForecast.css';

const WeatherForecast = ({ weatherData, isLoading, selectedDate, onDateChange }) => {
    // Function to format date in a more readable format
    const formatDate = (date) => {
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        return date.toLocaleDateString('en-US', options);
    };

    // Function to get appropriate weather icon based on temperature and chance of rain
    const getWeatherIcon = (temperature, chanceOfRain) => {
        if (chanceOfRain > 0.5) return '🌧️'; // High chance of rain
        if (chanceOfRain > 0.2) return '🌦️'; // Some chance of rain
        if (temperature > 25) return '☀️'; // Hot and sunny
        if (temperature > 20) return '🌤️'; // Warm with some clouds
        if (temperature > 15) return '⛅'; // Mild and partly cloudy
        return '☁️'; // Cool and cloudy
    };

    // Calculate min and max selectable dates (today to 5 days in future)
    const today = new Date();
    const maxDate = new Date();
    maxDate.setDate(today.getDate() + 5);

    return (
        <div className="weather-forecast">
            <div className="weather-container">
                <div className="date-selector">
                    <h3>Select Date for Weather Forecast</h3>
                    <DatePicker
                        selected={selectedDate}
                        onChange={onDateChange}
                        dateFormat="MMMM d, yyyy"
                        minDate={today}
                        maxDate={maxDate}
                        className="date-picker"
                    />
                    <p className="date-display">{formatDate(selectedDate)}</p>
                </div>

                <div className="weather-display">
                    {isLoading ? (
                        <div className="weather-loading">Loading weather data...</div>
                    ) : weatherData ? (
                        <div className="weather-info">
                            <div className="weather-icon">
                                {weatherData.maxTemperature && weatherData.chanceOfRain !== undefined ?
                                    getWeatherIcon(weatherData.maxTemperature, weatherData.chanceOfRain) :
                                    '⛅'}
                            </div>
                            <div className="weather-details">
                                <h4>{weatherData.location || 'Aveiro'}</h4>
                                <div className="temperature-range">
                                    <span className="max-temp">{weatherData.maxTemperature ? `${weatherData.maxTemperature}°C` : '--°C'}</span>
                                    <span className="temp-separator">/</span>
                                    <span className="min-temp">{weatherData.minTemperature ? `${weatherData.minTemperature}°C` : '--°C'}</span>
                                </div>
                                <div className="weather-metrics">
                                    <p>Humidity: {weatherData.humidity ? `${weatherData.humidity}%` : '--'}</p>
                                    <p>Chance of Rain: {weatherData.chanceOfRain !== undefined
                                        ? `${Math.round(weatherData.chanceOfRain)}%`
                                        : '--'}</p>
                                </div>
                            </div>
                        </div>
                    ) : (
                        <div className="weather-error">
                            <p>Weather forecast unavailable</p>
                        </div>
                    )}
                </div>
            </div>

            <div className="weather-message">
                <p>
                    {weatherData ? (
                        weatherData.chanceOfRain > 0.3 ?
                            "Don't forget your umbrella!" :
                            weatherData.maxTemperature > 25 ?
                                "It's going to be hot! Stay hydrated." :
                                weatherData.minTemperature < 15 ?
                                    "It's a bit chilly. Consider bringing a jacket." :
                                    "Perfect weather for visiting campus restaurants!"
                    ) : (
                        "Check the weather forecast before heading out to campus restaurants!"
                    )}
                </p>
            </div>
        </div>
    );
};

export default WeatherForecast;