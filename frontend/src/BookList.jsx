import { useState, useEffect } from 'react'

function BookList() {
  const [books, setBooks] = useState([])

  useEffect(() => {
    fetch('http://localhost:8080/books')
      .then(res => res.json())
      .then(data => setBooks(data))
  }, [])

  return (
    <div>
      <h2>Books in the Library</h2>
      {books.length === 0 ? (
        <p>No books yet.</p>
      ) : (
        <ul>
          {books.map(book => (
            <li key={book.id}>
              <strong>{book.title}</strong> by {book.author} ({book.pageCount} pages)
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

export default BookList
