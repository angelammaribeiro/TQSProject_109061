// Navbar.jsx
import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import '../../css/global/NavBar.css';

const Navbar = () => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const toggleMenu = () => {
    setIsMenuOpen(!isMenuOpen);
  };

  return (
      <nav className="navbar">
        <div className="navbar-container">
          <Link to="/" className="navbar-logo">
            Moliceiro
            <span className="logo-highlight">University</span>
          </Link>

          <div className="menu-icon" onClick={toggleMenu}>
            <i className={isMenuOpen ? 'fas fa-times' : 'fas fa-bars'}>
              {isMenuOpen ? '✕' : '☰'}
            </i>
          </div>

          <ul className={isMenuOpen ? 'nav-menu active' : 'nav-menu'}>
            <li className="nav-item">
              <Link to="/" className="nav-link" onClick={() => setIsMenuOpen(false)}>
                Home
              </Link>
            </li>
            <li className="nav-item">
              <Link to="/restaurants" className="nav-link" onClick={() => setIsMenuOpen(false)}>
                Restaurants
              </Link>
            </li>
            <li className="nav-item">
              <Link to="/reservations" className="nav-link" onClick={() => setIsMenuOpen(false)}>
                My Reservations
              </Link>
            </li>
            <li className="nav-item">
              <Link to="/about" className="nav-link" onClick={() => setIsMenuOpen(false)}>
                About
              </Link>
            </li>
          </ul>

          {/* Removed auth-buttons div */}
        </div>
      </nav>
  );
};

export default Navbar;