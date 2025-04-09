// ReservationForm.jsx
import React, { useState } from 'react';
import '../../css/HomePage/ReservationForm.css';

const BACKEND_URL = 'http://localhost:8080/api';

const ReservationForm = ({ restaurantName, selectedDate, onClose, onSuccess }) => {
    const [formData, setFormData] = useState({
        restaurantName: restaurantName,
        userName: '',
        userEmail: '',
        userPhone: '',
        reservationDate: selectedDate,
    });

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);
    const [reservationToken, setReservationToken] = useState(null);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            // Ensure reservationDate is in ISO format
            const reservationDateTime = new Date(formData.reservationDate);
            // Set time to noon (12:00) if no time is specified
            reservationDateTime.setHours(12, 0, 0);

            const payload = {
                ...formData,
                reservationDate: reservationDateTime.toISOString()
            };

            const response = await fetch(`${BACKEND_URL}/reservations/create-by-name`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            });

            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }

            const data = await response.json();
            setSuccess(true);

            // Save the reservation token
            setReservationToken(data.token);

            // Call the onSuccess callback if provided
            if (onSuccess) {
                onSuccess(data);
            }

            // Reset form after successful submission
            setFormData({
                restaurantName: restaurantName,
                userName: '',
                userEmail: '',
                userPhone: '',
                reservationDate: selectedDate,
            });

        } catch (err) {
            setError(err.message || 'Failed to create reservation');
            console.error('Error creating reservation:', err);
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (date) => {
        const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        return new Date(date).toLocaleDateString('en-US', options);
    };

    const handleCopyToken = () => {
        navigator.clipboard.writeText(reservationToken)
            .then(() => {
                // Visual feedback that the token was copied
                const tokenElement = document.getElementById('reservation-token');
                tokenElement.classList.add('copied');
                setTimeout(() => {
                    tokenElement.classList.remove('copied');
                }, 1500);
            })
            .catch(err => {
                console.error('Failed to copy token: ', err);
            });
    };

    if (success) {
        return (
            <div className="reservation-form-container">
                <div className="reservation-form success-message">
                    <h2>Reservation Successful!</h2>
                    <p>Your reservation has been created. Check your email for confirmation.</p>

                    <div className="token-container">
                        <p>Your reservation token:</p>
                        <div className="token-display">
                            <span id="reservation-token">{reservationToken}</span>
                            <button
                                className="copy-token-button"
                                onClick={handleCopyToken}
                                title="Copy token to clipboard"
                            >
                                Copy
                            </button>
                        </div>
                        <p className="token-info">
                            Keep this token to check or cancel your reservation later.
                        </p>
                    </div>

                    <button
                        className="reservation-form-button"
                        onClick={onClose}
                    >
                        Close
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="reservation-form-container">
            <div className="reservation-form">
                <div className="reservation-form-header">
                    <h2>Make a Reservation</h2>
                    <button className="close-button" onClick={onClose}>×</button>
                </div>

                {error && (
                    <div className="error-message">
                        <p>{error}</p>
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label>Restaurant</label>
                        <input
                            type="text"
                            name="restaurantName"
                            value={formData.restaurantName}
                            disabled
                        />
                    </div>

                    <div className="form-group">
                        <label>Date</label>
                        <input
                            type="text"
                            value={formatDate(formData.reservationDate)}
                            disabled
                        />
                    </div>

                    <div className="form-group">
                        <label>Your Name*</label>
                        <input
                            type="text"
                            name="userName"
                            value={formData.userName}
                            onChange={handleChange}
                            required
                            placeholder="Enter your full name"
                        />
                    </div>

                    <div className="form-group">
                        <label>Email*</label>
                        <input
                            type="email"
                            name="userEmail"
                            value={formData.userEmail}
                            onChange={handleChange}
                            required
                            placeholder="Enter your email"
                        />
                    </div>

                    <div className="form-group">
                        <label>Phone Number*</label>
                        <input
                            type="tel"
                            name="userPhone"
                            value={formData.userPhone}
                            onChange={handleChange}
                            required
                            placeholder="Enter your phone number"
                        />
                    </div>

                    <div className="form-actions">
                        <button
                            type="button"
                            className="reservation-form-button cancel-button"
                            onClick={onClose}
                            disabled={loading}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="reservation-form-button submit-button"
                            disabled={loading}
                        >
                            {loading ? 'Creating...' : 'Create Reservation'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default ReservationForm;