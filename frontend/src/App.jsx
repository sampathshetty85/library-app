import { useState } from 'react'
import BookList from './BookList.jsx'
import AddBook from './AddBook.jsx'

function App() {
  const [refreshKey, setRefreshKey] = useState(0)

  function handleBookAdded() {
    setRefreshKey(k => k + 1)
  }

  return (
    <div style={{ padding: '2rem', fontFamily: 'sans-serif' }}>
      <h1>Library App</h1>
      <AddBook onBookAdded={handleBookAdded} />
      <hr />
      <BookList key={refreshKey} />
    </div>
  )
}

export default App
