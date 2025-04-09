// RestaurantCard.jsx
import React, { useState } from 'react';
import { MapPin, Phone, Utensils } from 'lucide-react';
import '../../css/HomePage/RestaurantCard.css';
import ReservationForm from './ReservationForm';
import portugueseSeafoodImg from '../../images/portuguese-seafood.jpg';
import mixedCuisineImg from '../../images/mixed-cuisine.jpg';
import portugueseContemporary from '../../images/potuguese-contemporary.jpg';
import portugueseTraditional from '../../images/portuguesee-traditional.jpeg';
import fusionFood from '../../images/fusion.jpeg';
import snack from '../../images/snack.jpg';
import regional from '../../images/regional.jpeg'

const BACKEND_URL = 'http://localhost:8080/api';

const RestaurantCard = ({ restaurant, selectedDate }) => {
    const [meals, setMeals] = useState([]);
    const [showMeals, setShowMeals] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showReservationForm, setShowReservationForm] = useState(false);

    // Function to fetch meals for the selected date
    const fetchMeals = async () => {
        try {
            setLoading(true);
            setError(null);
            const formattedDate = selectedDate.toISOString().split('T')[0];
            const response = await fetch(`${BACKEND_URL}/meals/restaurant/${restaurant.id}/date/${formattedDate}`);

            if (!response.ok) {
                throw new Error('Failed to fetch meals');
            }

            const data = await response.json();
            setMeals(data);
            setShowMeals(true);
        } catch (err) {
            setError(err.message);
            console.error('Error fetching meals:', err);
        } finally {
            setLoading(false);
        }
    };

    // Function to generate a placeholder image based on cuisine type
    const getPlaceholderImage = (cuisineType) => {
        const cuisineImages = {
            'Mixed': mixedCuisineImg,
            'Portuguese Seafood': portugueseSeafoodImg,
            'default': '/images/default-restaurant.jpg',
            'Portuguese Contemporary' : portugueseContemporary,
            'Portuguese Traditional' : portugueseTraditional,
            'Fusion' : fusionFood,
            'Café & Casual Dining' : snack,
            'Portuguese Regional' : regional
        };

        return cuisineImages[cuisineType] || cuisineImages.default;
    };

    // Function to truncate description
    const truncateDescription = (desc, maxLength = 100) => {
        return desc.length > maxLength
            ? `${desc.substring(0, maxLength)}...`
            : desc;
    };

    // Function to handle successful reservation
    const handleReservationSuccess = (data) => {
        console.log('Reservation created successfully:', data);
        // You could add additional logic here if needed
    };

    return (
        <>
            <div className={`restaurant-card-modern ${showMeals ? 'showing-meals' : ''}`}>
                <div className="restaurant-card-image-container">
                    <img
                        src={getPlaceholderImage(restaurant.cuisineType)}
                        alt={restaurant.name}
                        className="restaurant-card-image"
                        onError={(e) => {
                            e.target.onerror = null;
                            e.target.src = '/images/default-restaurant.jpg';
                        }}
                    />
                    <div className="restaurant-card-cuisine-badge">
                        <Utensils size={16} />
                        <span>{restaurant.cuisineType}</span>
                    </div>
                    <button
                        className="restaurant-card-button make-reservation-button top-reservation-button"
                        onClick={(e) => {
                            e.preventDefault();
                            setShowReservationForm(true);
                        }}
                    >
                        Make a Reservation
                    </button>
                </div>

                <div className="restaurant-card-content">
                    <h2 className="restaurant-card-title">{restaurant.name}</h2>

                    <div className="restaurant-card-details">
                        <div className="restaurant-card-detail">
                            <MapPin size={16} />
                            <span>{restaurant.location}</span>
                        </div>

                        <div className="restaurant-card-detail">
                            <Phone size={16} />
                            <span>{restaurant.contactInfo}</span>
                        </div>
                    </div>

                    <p className="restaurant-card-description">
                        {truncateDescription(restaurant.description)}
                    </p>

                    {showMeals && (
                        <div className="meals-section">
                            <h3>Available Meals</h3>
                            {loading ? (
                                <p>Loading meals...</p>
                            ) : error ? (
                                <p className="error-message">{error}</p>
                            ) : meals.length > 0 ? (
                                <div className="meals-list">
                                    {meals.map(meal => (
                                        <div key={meal.id} className="meal-item">
                                            <h4>{meal.name}</h4>
                                            <p>{meal.description}</p>
                                            <p className="meal-price">€{meal.price.toFixed(2)}</p>
                                        </div>
                                    ))}
                                </div>
                            ) : (
                                <p>No meals available for this date.</p>
                            )}
                        </div>
                    )}

                    <div className="restaurant-card-actions">
                        <button
                            className="restaurant-card-button view-menu-button"
                            onClick={(e) => {
                                e.preventDefault();
                                if (showMeals) {
                                    setShowMeals(false);
                                } else {
                                    fetchMeals();
                                }
                            }}
                        >
                            {showMeals ? 'Hide Menus' : 'View Menus'}
                        </button>
                    </div>
                </div>
            </div>

            {showReservationForm && (
                <ReservationForm
                    restaurantName={restaurant.name}
                    selectedDate={selectedDate}
                    onClose={() => setShowReservationForm(false)}
                    onSuccess={handleReservationSuccess}
                />
            )}
        </>
    );
};

export default RestaurantCard;