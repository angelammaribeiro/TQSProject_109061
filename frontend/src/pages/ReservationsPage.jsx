// ReservationsPage.jsx
import React, {useEffect, useState} from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft, Search, Calendar, MapPin, User, Mail, Phone,
    Check, X, Trash2, AlertTriangle, UserCog } from 'lucide-react';
import '../css/ReservationPage/ReservationPage.css';
import '../css/ReservationPage/WorkerAcess.css';
import { WorkerLoginModal } from '../components/ReservationPage/WorkerAccessComponents.jsx';
import WorkerPanel from '../components/ReservationPage/WorkerPanelComponent.jsx';

const BACKEND_URL = 'http://localhost:8080/api';

const ReservationsPage = () => {
    const [token, setToken] = useState('');
    const [reservation, setReservation] = useState(null);
    const [restaurant, setRestaurant] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);
    const [showConfirmDelete, setShowConfirmDelete] = useState(false);

    // Worker access states
    const [showWorkerLoginModal, setShowWorkerLoginModal] = useState(false);
    const [isWorkerLoggedIn, setIsWorkerLoggedIn] = useState(false);

    const handleTokenChange = (e) => {
        setToken(e.target.value);
    };

    // Fetch restaurant details if reservation is loaded
    useEffect(() => {
        if (reservation && reservation.restaurantId) {
            fetchRestaurantDetails(reservation.restaurantId);
        }
    }, [reservation]);

    const fetchRestaurantDetails = async (restaurantId) => {
        try {
            const response = await fetch(`${BACKEND_URL}/restaurants/${restaurantId}`);

            if (response.ok) {
                const data = await response.json();
                setRestaurant(data);
            } else {
                console.error('Failed to fetch restaurant details');
            }
        } catch (err) {
            console.error('Error fetching restaurant:', err);
        }
    };

    const fetchReservation = async (e) => {
        e.preventDefault();

        if (!token.trim()) {
            setError('Please enter a reservation token');
            return;
        }

        setLoading(true);
        setError(null);
        setSuccessMessage(null);

        try {
            const response = await fetch(`${BACKEND_URL}/reservations/token/${token}`);

            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error('Reservation not found. Please check your token and try again.');
                }
                throw new Error(`Error: ${response.status}`);
            }

            const data = await response.json();
            setReservation(data);
        } catch (err) {
            setError(err.message || 'Failed to fetch reservation');
            setReservation(null);
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteReservation = async () => {
        setLoading(true);
        setError(null);
        setSuccessMessage(null);

        try {
            const response = await fetch(`${BACKEND_URL}/reservations/${token}`, {
                method: 'DELETE',
            });

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            setSuccessMessage('Reservation has been successfully cancelled');
            setReservation(null);
            setToken('');
            setShowConfirmDelete(false);
        } catch (err) {
            setError(err.message || 'Failed to cancel reservation');
        } finally {
            setLoading(false);
        }
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return 'N/A';

        const date = new Date(dateStr);
        return date.toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric',
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

    // Worker access handlers
    const handleWorkerLogin = () => {
        setIsWorkerLoggedIn(true);
    };

    const handleWorkerLogout = () => {
        setIsWorkerLoggedIn(false);
    };

    return (
        <div className="reservations-page">
            <div className="reservations-container">
                <div className="reservations-header">
                    <Link to="/" className="back-link">
                        <ArrowLeft size={18} />
                        <span>Back to Restaurants</span>
                    </Link>
                    <h1>My Reservation</h1>
                    {!isWorkerLoggedIn && (
                        <button
                            className="worker-access-button"
                            onClick={() => setShowWorkerLoginModal(true)}
                        >
                            <UserCog size={18} />
                            Access as Worker
                        </button>
                    )}
                    {isWorkerLoggedIn && (
                        <button
                            className="worker-access-button"
                            onClick={handleWorkerLogout}
                        >
                            <UserCog size={18} />
                            Exit Worker Mode
                        </button>
                    )}
                </div>

                {/* Show worker login modal */}
                {showWorkerLoginModal && (
                    <WorkerLoginModal
                        onClose={() => setShowWorkerLoginModal(false)}
                        onLogin={handleWorkerLogin}
                    />
                )}

                {/* Show either customer or worker view based on login status */}
                {!isWorkerLoggedIn ? (
                    // Regular customer view
                    <>
                        <div className="lookup-card">
                            <h2>Check Your Reservation</h2>
                            <p>Enter your reservation token to view or manage your reservation</p>

                            <form onSubmit={fetchReservation} className="token-form">
                                <div className="token-input-container">
                                    <input
                                        type="text"
                                        placeholder="Enter your reservation token"
                                        value={token}
                                        onChange={handleTokenChange}
                                        className="token-input"
                                    />
                                    <button
                                        type="submit"
                                        className="lookup-button"
                                        disabled={loading}
                                    >
                                        {loading ? 'Searching...' : <><Search size={16} /> Look Up</>}
                                    </button>
                                </div>
                            </form>

                            {error && (
                                <div className="message-container error">
                                    <AlertTriangle size={20} />
                                    <p>{error}</p>
                                </div>
                            )}

                            {successMessage && (
                                <div className="message-container success">
                                    <p>{successMessage}</p>
                                </div>
                            )}
                        </div>

                        {reservation && (
                            <div className="details-card">
                                <div className="details-header">
                                    <h2>Reservation Details</h2>
                                    {getStatusBadge(reservation.status)}
                                </div>

                                <div className="details-grid">
                                    <div className="detail-item location">
                                        <MapPin size={20} className="detail-icon" />
                                        <div className="detail-content">
                                            <span className="detail-label">Restaurant</span>
                                            <span className="detail-value">
                                                {restaurant ? restaurant.name : 'Restaurant information not available'}
                                            </span>
                                        </div>
                                    </div>

                                    <div className="detail-item date">
                                        <Calendar size={20} className="detail-icon" />
                                        <div className="detail-content">
                                            <span className="detail-label">Date</span>
                                            <span className="detail-value">{formatDate(reservation.reservationDate)}</span>
                                        </div>
                                    </div>

                                    <div className="detail-item name">
                                        <User size={20} className="detail-icon" />
                                        <div className="detail-content">
                                            <span className="detail-label">Name</span>
                                            <span className="detail-value">{reservation.userName}</span>
                                        </div>
                                    </div>

                                    <div className="detail-item email">
                                        <Mail size={20} className="detail-icon" />
                                        <div className="detail-content">
                                            <span className="detail-label">Email</span>
                                            <span className="detail-value">{reservation.userEmail}</span>
                                        </div>
                                    </div>

                                    <div className="detail-item phone">
                                        <Phone size={20} className="detail-icon" />
                                        <div className="detail-content">
                                            <span className="detail-label">Phone</span>
                                            <span className="detail-value">{reservation.userPhone}</span>
                                        </div>
                                    </div>
                                </div>

                                <div className="token-section">
                                    <span className="token-label">Reservation Token</span>
                                    <span className="token-value">{reservation.token}</span>
                                </div>

                                {reservation.status !== 'CANCELLED' && reservation.status !== 'COMPLETED' && (
                                    <div className="reservation-actions">
                                        {!showConfirmDelete ? (
                                            <button
                                                className="cancel-button"
                                                onClick={() => setShowConfirmDelete(true)}
                                                disabled={loading}
                                            >
                                                <Trash2 size={16} />
                                                Cancel Reservation
                                            </button>
                                        ) : (
                                            <div className="confirm-delete">
                                                <p>Are you sure you want to cancel this reservation?</p>
                                                <div className="confirm-buttons">
                                                    <button
                                                        className="no-button"
                                                        onClick={() => setShowConfirmDelete(false)}
                                                        disabled={loading}
                                                    >
                                                        No, Keep It
                                                    </button>
                                                    <button
                                                        className="yes-button"
                                                        onClick={handleDeleteReservation}
                                                        disabled={loading}
                                                    >
                                                        {loading ? 'Cancelling...' : 'Yes, Cancel It'}
                                                    </button>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                        )}
                    </>
                ) : (
                    // Worker view
                    <WorkerPanel onClose={handleWorkerLogout} />
                )}
            </div>
        </div>
    );
};

export default ReservationsPage;