// App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from './pages/HomePage';
import Navbar from './components/global/NavBar';
import Footer from './components/global/Footer';
import './App.css';

function App() {
  return (
      <Router>
        <div className="app">
          <Navbar />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<HomePage />} />
              {/* Add more routes as needed */}
              <Route path="/restaurants/:id" element={<div className="content-container">Restaurant Detail Page (To be implemented)</div>} />
            </Routes>
          </main>
          <Footer />
        </div>
      </Router>
  );
}

export default App;