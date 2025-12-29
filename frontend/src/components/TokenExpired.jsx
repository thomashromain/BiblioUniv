import React from 'react';

function TokenExpired() {
    return (
        <div>
            <h1>Token Expired</h1>
            <p>Your session has expired. Please log in again.</p>
            <button onClick={() => window.location.href = '/'}>Go to Login</button>
        </div>
    );
}

export default TokenExpired;