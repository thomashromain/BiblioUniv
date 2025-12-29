import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import Login from './components/Login';
import Books from './components/Books';
//import BookDetail from './components/BookDetail'; // You'll create this
import api from './services/api';
import './App.css';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));

    const handleLogin = () => setIsLoggedIn(true);
    const handleLogout = () => {
        localStorage.removeItem('token');
        setIsLoggedIn(false);
    };

    const handleResetDB = async () => {
        if (window.confirm('Reset database?')) {
            try {
                const response = await api.get('/admin/reset-db');
                alert(response.data);
            } catch (err) { alert('Failed: ' + err.message); }
        }
    };

    return (
        <Router>
            <div className="app-container">
                {/* Dev Tools */}
                {import.meta.env.DEV && (
                    <button onClick={handleResetDB} className="dev-reset-btn">Force Reset DB</button>
                )}

                {/* Navigation Bar */}
                <nav>
                    <Link to="/">Home</Link> | 
                    <Link to="/books"> Browse Books</Link>
                    {isLoggedIn && <button onClick={handleLogout}>Logout</button>}
                </nav>

                <hr />

                {/* Page Routing */}
                <Routes>
                    {/* Public Route */}
                    <Route 
                        path="/login" 
                        element={!isLoggedIn ? <Login onLogin={handleLogin} /> : <Navigate to="/books" />} 
                    />

                    {/* Protected Routes */}
                    <Route 
                        path="/books" 
                        element={isLoggedIn ? <Books /> : <Navigate to="/login" />} 
                    />

                    {/* Dynamic Route: /books/1, /books/2, etc. */}
                    <Route 
                        path="/books/:id" 
                        element={isLoggedIn ? <Books /> : <Navigate to="/login" />} 
                    />

                    {/* Redirect root to books or login */}
                    <Route path="/" element={<Navigate to={isLoggedIn ? "/books" : "/login"} />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;