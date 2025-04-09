// WorkerLoginModal.jsx
import React, { useState } from 'react';
import { X, LogIn, Calendar, Search, Check , User, Mail, Phone } from 'lucide-react';
import '../../css/ReservationPage/WorkerAcess.css';

const WorkerLoginModal = ({ onClose, onLogin }) => {
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (password === 'password') {
            onLogin();
            onClose();
        } else {
            setError('Incorrect password. Please try again.');
        }
    };

    return (
        <div className="worker-modal-overlay">
            <div className="worker-modal">
                <div className="worker-modal-header">
                    <h2>Worker Access</h2>
                    <button className="close-button" onClick={onClose}>
                        <X size={20} />
                    </button>
                </div>
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="password">Enter staff password</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            placeholder="Enter password"
                        />
                    </div>
                    {error && <div className="error-message">{error}</div>}
                    <div className="form-buttons">
                        <button type="button" className="cancel-button" onClick={onClose}>
                            Cancel
                        </button>
                        <button type="submit" className="login-button">
                            <LogIn size={16} />
                            Login
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

const RestaurantDateSelector = ({ onSearch, restaurants }) => {
    const [selectedRestaurant, setSelectedRestaurant] = useState('');
    const [selectedDate, setSelectedDate] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const handleSearch = (e) => {
        e.preventDefault();
        setIsLoading(true);
        onSearch(selectedRestaurant, selectedDate);
        setTimeout(() => setIsLoading(false), 500);
    };

    return (
        <div className="restaurant-date-selector">
            <h3>Search Reservations by Restaurant and Date</h3>
            <form onSubmit={handleSearch}>
                <div className="form-row">
                    <div className="form-group">
                        <label htmlFor="restaurant">Restaurant</label>
                        <select
                            id="restaurant"
                            value={selectedRestaurant}
                            onChange={(e) => setSelectedRestaurant(e.target.value)}
                            required
                        >
                            <option value="">Select a restaurant</option>
                            {restaurants.map((restaurant) => (
                                <option key={restaurant.id} value={restaurant.id}>
                                    {restaurant.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group">
                        <label htmlFor="date">Date</label>
                        <div className="date-input-container">
                            <Calendar size={16} className="input-icon" />
                            <input
                                type="date"
                                id="date"
                                value={selectedDate}
                                onChange={(e) => setSelectedDate(e.target.value)}
                                required
                            />
                        </div>
                    </div>
                </div>

                <button type="submit" className="search-button" disabled={isLoading}>
                    {isLoading ? 'Searching...' : (
                        <>
                            <Search size={16} />
                            Search Reservations
                        </>
                    )}
                </button>
            </form>
        </div>
    );
};

const ReservationsList = ({ reservations, onMarkAsCompleted }) => {
    if (!reservations || reservations.length === 0) {
        return (
            <div className="reservations-list empty">
                <p>No reservations found for the selected criteria.</p>
            </div>
        );
    }

    const formatDate = (dateStr) => {
        if (!dateStr) return 'N/A';
        const date = new Date(dateStr);
        return date.toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const getStatusBadge = (status) => {
        switch (status) {
            case 'CONFIRMED':
                return <div className="status-badge confirmed">CONFIRMED</div>;
            case 'PENDING':
                return <div className="status-badge pending">PENDING</div>;
            case 'CANCELLED':
                return <div className="status-badge cancelled">CANCELLED</div>;
            case 'COMPLETED':
                return <div className="status-badge completed">COMPLETED</div>;
            default:
                return <div className="status-badge default">{status}</div>;
        }
    };

    return (
        <div className="reservations-list">
            <h3>Reservations</h3>
            {reservations.map((reservation) => (
                <div key={reservation.id} className="reservation-card">
                    <div className="reservation-header">
                        <div className="reservation-id">#{reservation.id}</div>
                        {getStatusBadge(reservation.status)}
                    </div>

                    <div className="reservation-details">

                        <div className="detail-row">
                            <User size={16} className="detail-icon" />
                            <span className="detail-label">Name:</span>
                            <span className="detail-value">{reservation.userName}</span>
                        </div>

                        <div className="detail-row">
                            <Mail size={16} className="detail-icon" />
                            <span className="detail-label">Email:</span>
                            <span className="detail-value">{reservation.userEmail}</span>
                        </div>

                        <div className="detail-row">
                            <Phone size={16} className="detail-icon" />
                            <span className="detail-label">Phone:</span>
                            <span className="detail-value">{reservation.userPhone}</span>
                        </div>
                    </div>

                    <div className="token-display">
                        <span className="token-label">Token:</span>
                        <span className="token-value">{reservation.token}</span>
                    </div>

                    {reservation.status !== 'COMPLETED' && reservation.status !== 'CANCELLED' && (
                        <button
                            className="complete-button"
                            onClick={() => onMarkAsCompleted(reservation.token)}
                        >
                            <Check size={16} />
                            Mark as Completed
                        </button>
                    )}
                </div>
            ))}
        </div>
    );
};

export { WorkerLoginModal, RestaurantDateSelector, ReservationsList };