import { useState, useEffect } from 'react'
import api from './services/api'
import './App.css'

function App() {
  const [books, setBooks] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    api.get('/books')
      .then(response => {
        setBooks(response.data)
        setLoading(false)
      })
      .catch(err => {
        console.error('API Error:', err)
        setError(`${err.message} (${err.code || 'Unknown code'})`)
        setLoading(false)
      })
  }, [])

  if (loading) return <div>Loading...</div>
  if (error) return <div>Error: {error}</div>

  return (
    <div>
      <h1>Books from Backend</h1>
      <pre>{JSON.stringify(books, null, 2)}</pre>
    </div>
  )
}

export default App
