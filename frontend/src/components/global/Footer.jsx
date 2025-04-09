// Footer.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import '../../css/global/Footer.css';

const Footer = () => {
  return (
      <footer className="footer">
        <div className="footer-container">
          <div className="footer-content">
            <h3>Moliceiro University</h3>
            <p>Your student dining solution for finding and booking meals across campus restaurants.</p>
          </div>
        </div>

        <div className="footer-bottom">
          <p>&copy; {new Date().getFullYear()} Moliceiro Dining. All rights reserved.</p>
          <div className="footer-social">
            <a href="https://facebook.com" target="_blank" rel="noreferrer">Facebook</a>
            <a href="https://instagram.com" target="_blank" rel="noreferrer">Instagram</a>
            <a href="https://twitter.com" target="_blank" rel="noreferrer">Twitter</a>
          </div>
        </div>
      </footer>
  );
};

export default Footer;