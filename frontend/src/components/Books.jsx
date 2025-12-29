import { useState, useEffect } from 'react';
import api from '../services/api';
import TokenExpired from './TokenExpired';

function Books() {
    const [books, setBooks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [tokenExpired, setTokenExpired] = useState(false);

    useEffect(() => {
        // Refresh token on page load
        api.post('/auth/refresh').then(response => {
            localStorage.setItem('token', response.data.token);
            fetchBooks();
        }).catch(() => {
            // If refresh fails, token expired
            localStorage.removeItem('token');
            setTokenExpired(true);
            setLoading(false);
        });
    }, []);

    const fetchBooks = () => {
        api.get('/books')
            .then(response => {
                setBooks(response.data);
                setLoading(false);
            })
            .catch(err => {
                console.error('API Error:', err);
                if (err.response && err.response.status === 401) {
                    setTokenExpired(true);
                } else {
                    setError(`${err.message} (${err.code || 'Unknown code'})`);
                }
                setLoading(false);
            });
    };

    if (tokenExpired) return <TokenExpired />;
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
                        <p>Instances in Library: {book.instanceCount}</p>
                        {book.bookImage && <img src={book.bookImage} alt={book.title} style={{width: '100px'}} />}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default Books;