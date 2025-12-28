import { useState, useEffect } from 'react';
import Login from './components/Login';
import Books from './components/Books';
import api from './services/api';
import './App.css';

function App() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (token) {
            setIsLoggedIn(true);
        }
    }, []);

    const handleLogin = () => {
        setIsLoggedIn(true);
    };

    const handleLogout = () => {
        localStorage.removeItem('token');
        setIsLoggedIn(false);
    };

    const handleResetDB = async () => {
        if (window.confirm('Are you sure you want to reset the database? This will delete all data.')) {
            try {
                const response = await api.get('/admin/reset-db');
                alert(response.data);
            } catch (err) {
                alert('Failed to reset database: ' + err.message);
            }
        }
    };

    return (
        <div>
            {import.meta.env.DEV && (
                <button
                    onClick={handleResetDB}
                    style={{
                        position: 'fixed',
                        top: '10px',
                        left: '10px',
                        backgroundColor: 'red',
                        color: 'white',
                        border: 'none',
                        padding: '10px',
                        cursor: 'pointer',
                        zIndex: 1000
                    }}
                >
                    Force Reset DB
                </button>
            )}
            {isLoggedIn ? (
                <div>
                    <button onClick={handleLogout}>Logout</button>
                    <Books />
                </div>
            ) : (
                <Login onLogin={handleLogin} />
            )}
        </div>
    );
}

export default App;
