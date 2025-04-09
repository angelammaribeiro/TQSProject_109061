// WorkerPanel.jsx
import React, { useState, useEffect } from 'react';
import { UserCog, X, Search } from 'lucide-react';
import { RestaurantDateSelector, ReservationsList } from './WorkerAccessComponents.jsx';

const BACKEND_URL = 'http://localhost:8080/api';

const WorkerPanel = ({ onClose }) => {
    const [activeTab, setActiveTab] = useState('token');
    const [tokenInput, setTokenInput] = useState('');
    const [restaurants, setRestaurants] = useState([]);
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    // Fetch restaurants on component mount
    useEffect(() => {
        fetchRestaurants();
    }, []);

    const fetchRestaurants = async () => {
        try {
            const response = await fetch(`${BACKEND_URL}/restaurants`);
            if (response.ok) {
                const data = await response.json();
                setRestaurants(data);
            } else {
                console.error('Failed to fetch restaurants');
            }
        } catch (err) {
            console.error('Error fetching restaurants:', err);
        }
    };

    const handleTokenSearch = async (e) => {
        e.preventDefault();
        if (!tokenInput.trim()) {
            setError('Please enter a reservation token');
            return;
        }

        setLoading(true);
        setError(null);
        setSuccess(null);

        try {
            const response = await fetch(`${BACKEND_URL}/reservations/token/${tokenInput}`);

            if (!response.ok) {
                if (response.status === 404) {
                    throw new Error('Reservation not found. Please check the token and try again.');
                }
                throw new Error(`Error: ${response.status}`);
            }

            const data = await response.json();
            setReservations([data]); // Set as array to reuse ReservationsList component
        } catch (err) {
            setError(err.message || 'Failed to fetch reservation');
            setReservations([]);
        } finally {
            setLoading(false);
        }
    };

    const handleRestaurantDateSearch = async (restaurantId, date) => {
        if (!restaurantId || !date) {
            setError('Please select both restaurant and date');
            return;
        }

        setLoading(true);
        setError(null);
        setSuccess(null);

        try {
            // Create date object from the selected date
            const dateObj = new Date(date);
            dateObj.setHours(23, 0, 0, 0); // Set to start of day

            // Format as YYYY-MM-DDT00:00:00
            const formattedDate = dateObj.toISOString().split('.')[0];

            // Log what we're sending to help debug
            console.log(`Searching for restaurant ${restaurantId} on date ${formattedDate}`);

            const response = await fetch(
                `${BACKEND_URL}/reservations/restaurant/${restaurantId}/date?date=${formattedDate}`
            );

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            const data = await response.json();
            setReservations(data);

            if (data.length === 0) {
                setSuccess('No reservations found for the selected criteria');
            }
        } catch (err) {
            setError(err.message || 'Failed to fetch reservations');
            setReservations([]);
        } finally {
            setLoading(false);
        }
    };

    const handleMarkAsCompleted = async (token) => {
        setLoading(true);
        setError(null);
        setSuccess(null);

        try {
            const response = await fetch(`${BACKEND_URL}/reservations/${token}/status`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `newStatus=COMPLETED`,
            });

            if (!response.ok) {
                throw new Error(`Error: ${response.status}`);
            }

            // Update the reservation status in the UI
            setReservations(prevReservations =>
                prevReservations.map(res =>
                    res.token === token ? { ...res, status: 'COMPLETED' } : res
                )
            );

            setSuccess(`Reservation ${token} has been marked as completed`);
        } catch (err) {
            setError(err.message || 'Failed to update reservation status');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="worker-panel">
            <div className="worker-panel-header">
                <h2>
                    <UserCog size={20} style={{ marginRight: '8px' }} />
                    Worker Panel
                </h2>
                <button className="close-button" onClick={onClose}>
                    <X size={20} />
                </button>
            </div>

            <div className="worker-panel-content">
                <div className="worker-tabs">
                    <div
                        className={`worker-tab ${activeTab === 'token' ? 'active' : ''}`}
                        onClick={() => setActiveTab('token')}
                    >
                        Search by Token
                    </div>
                    <div
                        className={`worker-tab ${activeTab === 'restaurant' ? 'active' : ''}`}
                        onClick={() => setActiveTab('restaurant')}
                    >
                        Search by Restaurant & Date
                    </div>
                </div>

                {activeTab === 'token' && (
                    <div className="token-search">
                        <form onSubmit={handleTokenSearch}>
                            <div className="form-group">
                                <label htmlFor="tokenInput">Reservation Token</label>
                                <input
                                    type="text"
                                    id="tokenInput"
                                    value={tokenInput}
                                    onChange={(e) => setTokenInput(e.target.value)}
                                    placeholder="Enter reservation token"
                                    required
                                />
                            </div>
                            <button type="submit" className="search-button" disabled={loading}>
                                {loading ? 'Searching...' : (
                                    <>
                                        <Search size={16} />
                                        Search Reservation
                                    </>
                                )}
                            </button>
                        </form>
                    </div>
                )}

                {activeTab === 'restaurant' && (
                    <RestaurantDateSelector
                        restaurants={restaurants}
                        onSearch={handleRestaurantDateSearch}
                    />
                )}

                {error && (
                    <div className="message-container error">
                        <p>{error}</p>
                    </div>
                )}

                {success && (
                    <div className="message-container success">
                        <p>{success}</p>
                    </div>
                )}

                {reservations.length > 0 && (
                    <ReservationsList
                        reservations={reservations}
                        onMarkAsCompleted={handleMarkAsCompleted}
                    />
                )}
            </div>
        </div>
    );
};
 export default WorkerPanel