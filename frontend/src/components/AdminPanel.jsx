import { useState } from 'react';
import api from '../services/api';

export function AdminPanel() {
    const [testResults, setTestResults] = useState([]);
    const [isRunning, setIsRunning] = useState(false);

    // Helper to log results with JSON
    const log = (step, status, details, json = null) => {
        setTestResults(prev => [...prev, { step, status, details, json }]);
    };

    const runFullSuite = async () => {
        setIsRunning(true);
        setTestResults([]); // Clear previous logs

        // --- TEST 1: ADMIN SUITE ---
        await runTestSequence('admin', 'password', 'Admin User');

        // --- TEST 2: RESTRICTED USER SUITE ---
        await runTestSequence('user1', 'user', 'Restricted User');

        setIsRunning(false);
    };

    const runTestSequence = async (username, password, label) => {
    log("SYSTEM", "INFO", `--- Starting test for ${label} (${username}) ---`);
    
    try {
        // 1. LOGIN - Get the specific token
        // We use the standard 'api' here because login is public
        const loginRes = await api.post('/login', { username, password });
        const testToken = loginRes.data.token;
        log(`${username}:Auth`, "SUCCESS", "Login successful", loginRes.data);

        // 2. Define the config with the FRESH token from the response
        const testConfig = { 
            headers: { 
                Authorization: `Bearer ${testToken}` 
            } 
        };

        // 3. CATALOG TEST (Using the generated token)
        try {
            const booksRes = await api.get('/books', testConfig);
            log(`${username}:Books`, "SUCCESS", "Catalog fetched", booksRes.data);
        } catch (err) {
            log(`${username}:Books`, "FAIL", `Error ${err.response?.status}`, err.response?.data);
        }

        // 4. ADMIN ACCESS TEST (The critical check)
        try {
            const adminRes = await api.get('/admin/test', testConfig); 
            log(`${username}:Admin`, "SUCCESS", "Access granted", adminRes.data);
        } catch (err) {
            const status = err.response?.status;
            const isExpectedForbidden = (username !== 'admin' && status === 403);
            
            log(`${username}:Admin`, isExpectedForbidden ? "SUCCESS" : "FAIL", 
                isExpectedForbidden ? "Forbidden (Correct Security)" : `Error: ${status}`, 
                err.response?.data);
        }

    } catch (err) {
        log(`${username}:Fatal`, "FAIL", "Auth failed", err.response?.data);
    }
};
    return (
        <div className="admin-container">
            <h1>Library System Diagnostics</h1>
            
            <div className="admin-card">
                <button className="test-btn" onClick={runFullSuite} disabled={isRunning}>
                    {isRunning ? "Testing..." : "Run Multi-User API Test"}
                </button>

                <div className="results-terminal">
                    {testResults.length === 0 && <p className="details">No logs to display. Press "Run Test".</p>}
                    
                    {testResults.map((res, i) => (
                        <div key={i} className={`log-entry ${res.status.toLowerCase()}`}>
                            <div className="log-header">
                                <span className="step-label">[{res.step}]</span>
                                <span className="status-indicator">{res.status}</span>
                                <span className="details">{res.details}</span>
                            </div>
                            
                            {/* JSON is now rendered automatically if it exists */}
                            {res.json && (
                                <div className="json-container">
                                    <div className="json-label">Response Body:</div>
                                    <pre className="json-viewer">
                                        {JSON.stringify(res.json, null, 2)}
                                    </pre>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}

export default AdminPanel;