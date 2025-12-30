import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../services/api';

function BookDetails() {
    const { id } = useParams();
    const [bookData, setBookData] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api.get(`/books/${id}`)
            .then(res => {
                console.log("API Response:", res.data); // DEBUG: Look at this in your browser console!
                setBookData(res.data);
                setLoading(false);
            })
            .catch(() => setLoading(false));
    }, [id]);

    if (loading) return <div>Loading...</div>;
    
    // If bookData exists, but we aren't sure of the structure yet:
    if (!bookData) return <div>Book not found in database.</div>;

    // Use a fallback: if res.data.book exists, use it. Otherwise, assume res.data IS the book.
    const book = bookData.book || bookData; 
    const count = bookData.instanceCount || 0;
    const isAdmin = bookData.admin || false;

    return (
        <div className="book-detail-page">
            <h1>{book.title}</h1>
            <p>Author: {book.author}</p>
            <p>Copies: {count}</p>
            
            {isAdmin && (
                <div className="admin-zone">
                    <button onClick={handleDelete}>Delete Book</button>
                    <button onClick={handleAddInstance}>+ Add Instance</button>
                </div>
            )}
        </div>
    );
}

export default BookDetails;