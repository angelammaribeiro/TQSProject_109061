// Navbar.jsx
import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Home, Calendar, Menu, X } from 'lucide-react';
import '../../css/global/NavBar.css';

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const location = useLocation();

  // Add scroll event listener to change navbar style on scroll
  useEffect(() => {
    const handleScroll = () => {
      const isScrolled = window.scrollY > 20;
      if (isScrolled !== scrolled) {
        setScrolled(isScrolled);
      }
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [scrolled]);

  // Close mobile menu when route changes
  useEffect(() => {
    setMenuOpen(false);
  }, [location]);

  // Check if the link is active
  const isActive = (path) => {
    return location.pathname === path;
  };

  return (
      <nav className={`navbar ${scrolled ? 'scrolled' : ''}`}>
        <div className="navbar-container">
          <Link to="/" className="navbar-logo">
            <span className="logo-text">Moliceiro University</span>
          </Link>

          <div className="navbar-mobile-toggle" onClick={() => setMenuOpen(!menuOpen)}>
            {menuOpen ? <X size={24} /> : <Menu size={24} />}
          </div>

          <ul className={`navbar-menu ${menuOpen ? 'active' : ''}`}>
            <li className={`navbar-item ${isActive('/') ? 'active' : ''}`}>
              <Link to="/" className="navbar-link">
                <Home size={18} />
                <span>Home</span>
              </Link>
            </li>
            <li className={`navbar-item ${isActive('/reservations') ? 'active' : ''}`}>
              <Link to="/reservations" className="navbar-link">
                <Calendar size={18} />
                <span>My Reservation</span>
              </Link>
            </li>
            {/* Add other navigation items here if needed */}
          </ul>
        </div>
      </nav>
  );
};

export default Navbar;