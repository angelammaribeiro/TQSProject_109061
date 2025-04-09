// RestaurantCard.jsx
import React from 'react';
import { MapPin, Phone, Utensils } from 'lucide-react';
import '../../css/HomePage/RestaurantCard.css';
import portugueseSeafoodImg from '../../images/portuguese-seafood.jpg';
import mixedCuisineImg from '../../images/mixed-cuisine.jpg';
import portugueseContemporary from '../../images/potuguese-contemporary.jpg';
import portugueseTraditional from '../../images/portuguesee-traditional.jpeg';
import fusionFood from '../../images/fusion.jpeg';
import snack from '../../images/snack.jpg';
import regional from '../../images/regional.jpeg'

const RestaurantCard = ({ restaurant }) => {
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

    return (
        <div className="restaurant-card-modern">
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

                <div className="restaurant-card-actions">
                    <button className="restaurant-card-button">
                        View Menus
                    </button>
                </div>
            </div>
        </div>
    );
};

export default RestaurantCard;