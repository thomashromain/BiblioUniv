import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';
import TokenExpired from './TokenExpired';

function BookDetails() {
    const { id } = useParams();
    const navigate = useNavigate();
    
    const [bookData, setBookData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [borrowing, setBorrowing] = useState(false); // New state for borrow request
    const [tokenExpired, setTokenExpired] = useState(false);
    const [error, setError] = useState(null);

    const fetchBookDetails = async () => {
        setLoading(true);
        try {
            const response = await api.get(`/books/${id}`);
            setBookData(response.data);
        } catch (err) {
            if (err.response?.status === 401 || err.response?.status === 403) {
                setTokenExpired(true);
            } else if (err.response?.status === 404) {
                setError("This book does not exist in our records.");
            } else {
                setError("Failed to connect to the library server.");
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (id) fetchBookDetails();
    }, [id]);

    const handleBorrow = async () => {
        setBorrowing(true);
        try {
            // Hits the new BorrowingController endpoint
            const response = await api.post(`/borrowings/request/${id}`);
            alert(response.data); // "Book borrowed successfully..."
            // Refresh details to update instanceCount
            fetchBookDetails();
        } catch (err) {
            alert(err.response?.data || "Failed to borrow book.");
        } finally {
            setBorrowing(false);
        }
    };

    if (tokenExpired) return <TokenExpired />;
    if (loading) return <div className="details-loading"><p>⏳ Retrieving book info...</p></div>;
    if (error) return <div className="error-container"><p>⚠️ {error}</p><button onClick={() => navigate('/books')}>Back</button></div>;

    const { title, author, publishedYear, isbn, bookImage, instanceCount, canDelete } = bookData;

    return (
        <div className="details-page">
            <button className="back-link" onClick={() => navigate(-1)}>← Back to Catalog</button>

            <div className="details-layout">
                <div className="details-image-section">
                    {bookImage ? <img src={bookImage} alt={title} className="large-cover" /> : <div className="large-placeholder">No Image</div>}
                </div>

                <div className="details-info-section">
                    <h1>{title}</h1>
                    <p className="detail-author">By <strong>{author}</strong></p>
                    
                    <div className="meta-info">
                        <p><strong>Published:</strong> {publishedYear || 'N/A'}</p>
                        <p><strong>ISBN:</strong> {isbn || 'N/A'}</p>
                    </div>

                    <div className="inventory-status">
                        <span className={`status-badge ${instanceCount > 0 ? 'in-stock' : 'out-of-stock'}`}>
                            {instanceCount > 0 ? 'Available' : 'Currently Unavailable'}
                        </span>
                        <p>Copies in library: <strong>{instanceCount}</strong></p>
                    </div>

                    <div className="action-buttons">
                        {/* BORROW BUTTON - Integrated with API */}
                        <button 
                            className="primary-btn" 
                            disabled={instanceCount === 0 || borrowing}
                            onClick={handleBorrow}
                        >
                            {borrowing ? "Processing..." : "Borrow this Book"}
                        </button>

                        {canDelete && (
                            <button 
                                className="danger-btn"
                                onClick={async () => {
                                    if(window.confirm("Delete this book?")) {
                                        await api.delete(`/books/${id}`);
                                        navigate('/books');
                                    }
                                }}
                            >
                                Delete Record
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default BookDetails;