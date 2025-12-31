// components/Navbar.jsx
import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';

function Navbar({ isLoggedIn, onLogout }) {
    const [lateCount, setLateCount] = useState(0);
    const navigate = useNavigate();

    useEffect(() => {
        if (isLoggedIn) {
            const checkLateBooks = async () => {
                try {
                    const response = await api.get('/borrowings/late-count');
                    setLateCount(response.data);
                } catch (err) {
                    console.error("Could not check late books", err);
                }
            };
            checkLateBooks();
        }
    }, [isLoggedIn]);

    return (
        <nav className="navbar">
            <div className="nav-logo" onClick={() => navigate('/')}>
                BIBLIO<span>UNIV</span>
            </div>

            <div className="nav-links">
                <Link to="/books">Catalog</Link>
                {isLoggedIn && (
                    <>
                        <Link to="/my-borrowings" className="nav-notif-container">
                            My Books
                            {lateCount > 0 && (
                                <span className="notif-badge" title={`${lateCount} books are late!`}>
                                    {lateCount}
                                </span>
                            )}
                        </Link>
                        <button onClick={onLogout} className="logout-btn">Logout</button>
                    </>
                )}
                {!isLoggedIn && <Link to="/login">Login</Link>}
            </div>
        </nav>
    );
}

export default Navbar;