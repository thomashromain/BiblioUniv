import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // Import navigation
import api from '../services/api';
import TokenExpired from './TokenExpired';

const BookCard = ({ book, onClick }) => (
    <div className="book-card">
        {/* ... existing image and details code ... */}
        <div className="book-details">
            <span className="instance-badge">{book.instanceCount} Available</span>
            <h3>{book.title}</h3>
            <p className="author">By {book.author}</p>
            {/* Call the navigation function on click */}
            <button className="borrow-btn" onClick={() => onClick(book.id)}>
                View Details
            </button>
        </div>
    </div>
);

function Books() {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [tokenExpired, setTokenExpired] = useState(false);
    const navigate = useNavigate();

    // In a real app, determine this by decoding the JWT token
    const isAdmin = localStorage.getItem('role') === 'ROLE_ADMIN';

    useEffect(() => {
        const fetchBooks = async () => {
            try {
                const response = await api.get('/books');
                setBooks(response.data);
            } catch (err) {
                if (err.response?.status === 401) setTokenExpired(true);
                else setError(err.message);
            } finally {
                setLoading(false);
            }
        };
        fetchBooks();
    }, []);

    const handleViewDetails = (id) => {
        navigate(`/books/${id}`);
    };

    if (tokenExpired) return <TokenExpired />;
    if (loading) return <div className="status-message">Loading...</div>;

    return (
        <div className="books-container">
            <header className="books-header">
                <h1>University Library</h1>
                {isAdmin && <button className="admin-add-btn">Add New Book</button>}
            </header>
            
            <div className="books-grid">
                {books.map(book => (
                    <BookCard key={book.id} book={book} onClick={handleViewDetails} />
                ))}
            </div>
        </div>
    );
}

export default Books;