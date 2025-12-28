import { useState, useEffect } from 'react';
import api from '../services/api';

function Books() {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        // Refresh token on page load
        api.post('/auth/refresh').then(response => {
            localStorage.setItem('token', response.data.token);
        }).catch(() => {
            // If refresh fails, logout
            localStorage.removeItem('token');
            window.location.reload();
        });

        api.get('/books')
            .then(response => {
                setBooks(response.data);
                setLoading(false);
            })
            .catch(err => {
                console.error('API Error:', err);
                setError(`${err.message} (${err.code || 'Unknown code'})`);
                setLoading(false);
            });
    }, []);

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div>
            <h1>Books</h1>
            <ul>
                {books.map(book => (
                    <li key={book.id}>
                        <h2>{book.title}</h2>
                        <p>Author: {book.author}</p>
                        <p>Published Year: {book.publishedYear}</p>
                        <p>ISBN: {book.isbn}</p>
                        {book.bookImage && <img src={book.bookImage} alt={book.title} style={{width: '100px'}} />}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default Books;