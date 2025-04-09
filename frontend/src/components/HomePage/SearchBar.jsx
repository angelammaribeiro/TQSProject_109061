// SearchBar.jsx
import React, { useState } from 'react';
import '../../css/HomePage/SearchBar.css'; 

const SearchBar = ({ onSearch }) => {
  const [searchQuery, setSearchQuery] = useState('');

  const handleInputChange = (e) => {
    const query = e.target.value;
    setSearchQuery(query);
    onSearch(query);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch(searchQuery);
  };

  return (
    <div className="search-bar">
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Search by name, cuisine, or location..."
          value={searchQuery}
          onChange={handleInputChange}
        />
        <button type="submit">
          <i className="search-icon">🔍</i>
        </button>
      </form>
    </div>
  );
};

export default SearchBar;