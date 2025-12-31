import { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import Login from './components/Login';
import Books from './components/Books';
import BookDetails from './components/BookDetails';
import Navbar from './components/Navbar';
import {MyBorrowings} from './components/MyBorrowings';
import api from './services/api';
import './App.css';
import AdminPanel from './components/AdminPanel';

function App() {
    // Sync login state with localStorage
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
            <Navbar isLoggedIn={isLoggedIn} onLogout={handleLogout} />
            <div className="app-container">
                {/* Dev Tools - Only shows in development */}
                {import.meta.env.DEV && (
                    <button onClick={handleResetDB} className="dev-reset-btn">Force Reset DB</button>
                )}

                <nav className="main-nav">
                    <Link to="/">Home</Link>
                    <Link to="/books">Browse Books</Link>
                    {isLoggedIn && (
                        <>
                            <Link to="/my-borrowings">My Borrowings</Link>
                            <button onClick={handleLogout} className="logout-link">Logout</button>
                        </>
                    )}
                </nav>

                <hr />

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

                    <Route 
                        path="/books/:id" 
                        element={isLoggedIn ? <BookDetails /> : <Navigate to="/login" />} 
                    />

                    <Route 
                        path="/my-borrowings" 
                        element={isLoggedIn ? <MyBorrowings /> : <Navigate to="/login" />} 
                    />

                    <Route 
                        path="/admin" 
                        element={isLoggedIn ? <AdminPanel /> : <Navigate to="/login" />} 
                    />

                    {/* Redirect root */}
                    <Route path="/" element={<Navigate to={isLoggedIn ? "/books" : "/login"} />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;