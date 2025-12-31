import { useState, useEffect } from 'react';
import api from '../services/api';

export function MyBorrowings() {
    const [borrowings, setBorrowings] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchMyBooks = async () => {
            try {
                const response = await api.get('/borrowings/my-books');
                setBorrowings(response.data);
            } catch (err) {
                console.error("Failed to fetch borrowings", err);
            } finally {
                setLoading(false);
            }
        };
        fetchMyBooks();
    }, []);

    if (loading) return <div>Loading your library card...</div>;

    return (
        <div className="page-container">
            <h1>My Borrowed Books</h1>
            {borrowings.length === 0 ? (
                <p>You don't have any active borrowings.</p>
            ) : (
                <div className="borrowings-list">
                    {borrowings.map(b => (
                        <div key={b.id} className="borrow-card" style={{border: '1px solid #ccc', margin: '10px 0', padding: '10px'}}>
                            <h3>{b.bookTitle}</h3>
                            <p>Author: {b.author}</p>
                            <p>Borrowed on: {new Date(b.borrowedAt).toLocaleDateString()}</p>
                            <p style={{color: 'red'}}>
                                <strong>Return Deadline: {new Date(b.returnDeadline).toLocaleDateString()}</strong>
                            </p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default MyBorrowings;