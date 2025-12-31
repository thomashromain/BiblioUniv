import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import TokenExpired from './TokenExpired';

const BookCard = ({ item, onView, onDelete }) => {
    // CHANGE 1: The DTO is flat now. We destructure directly from 'item'.
    // There is no 'item.book' anymore.
    const { id, title, author, bookImage, instanceCount, canDelete } = item;

    return (
        <div className="book-card">
            {/* CHANGE 2: Added image display since your Entity has 'bookImage' */}
            {bookImage ? (
                <img src={bookImage} alt={title} className="book-cover" style={{height: '150px', objectFit: 'cover'}} />
            ) : (
                <div className="no-image-placeholder">No Image</div>
            )}
            
            <div className="book-content">
                <span className="stock-label">{instanceCount} copies available</span>
                <h3>{title}</h3>
                <p className="author-text">By {author}</p>
                
                <div className="card-footer">
                    <button onClick={() => onView(id)}>View Details</button>
                    {/* canDelete comes directly from the DTO boolean now */}
                    {canDelete && (
                        <button className="danger-btn" onClick={() => onDelete(id)}>
                            Delete Book
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

function Books() {
    const [booksData, setBooksData] = useState([]);
    const [loading, setLoading] = useState(true);
    const [tokenExpired, setTokenExpired] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchBooks = async () => {
            try {
                // This hits your BookController @GetMapping
                const response = await api.get('/books'); 
                setBooksData(response.data);
            } catch (err) {
                console.error("Fetch error:", err);
                if (err.response?.status === 401 || err.response?.status === 403) {
                    setTokenExpired(true);
                }
            } finally {
                setLoading(false);
            }
        };
        fetchBooks();
    }, []);

    if (tokenExpired) return <TokenExpired />;
    
    // Loading State
    if (loading) return (
        <div className="loading-state">
             <p>⏳ Fetching Library Catalog...</p>
        </div>
    );

    // CHANGE 3: Logic to check if user is Admin/CanCreate
    // Since the boolean is repeated on every object, we can just check the first one
    const canCreateAny = booksData.length > 0 ? booksData[0].canCreate : false;

    return (
        <div className="page-container">
            <header className="page-header">
                <h1>Library Catalog</h1>
                {canCreateAny && (
                    <button onClick={() => navigate('/books/new')}>+ Add New Book</button>
                )}
            </header>
            
            <div className="books-grid">
                {booksData.length === 0 ? (
                    <p>No books found in the library.</p>
                ) : (
                    booksData.map((item) => (
                        <BookCard 
                            key={item.id} // CHANGE 4: Use item.id directly
                            item={item} 
                            onView={(id) => navigate(`/books/${id}`)}
                            onDelete={async (id) => {
                                if (window.confirm("Delete book?")) {
                                    try {
                                        await api.delete(`/api/books/${id}`);
                                        // CHANGE 5: Filter by b.id, not b.book.id
                                        setBooksData(prev => prev.filter(b => b.id !== id));
                                    } catch (e) {
                                        alert("Failed to delete book");
                                    }
                                }
                            }}
                        />
                    ))
                )}
            </div>
        </div>
    );
}

export default Books;