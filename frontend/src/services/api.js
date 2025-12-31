import axios from 'axios';

const api = axios.create({
    baseURL: '/api',
});

api.interceptors.request.use(
    (config) => {
        // If the header is already set (e.g., by our test suite), 
        // don't overwrite it with the logged-in user's token.
        if (config.headers.Authorization) {
            return config;
        }

        const token = localStorage.getItem('token'); 
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Optional: Helper for clean manual calls
export const apiWithToken = (token) => {
    return axios.create({
        baseURL: '/api',
        headers: { Authorization: `Bearer ${token}` }
    });
};

export default api;