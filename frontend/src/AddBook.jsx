import { useState } from 'react'

function AddBook({ onBookAdded }) {
  const [title, setTitle] = useState('')
  const [author, setAuthor] = useState('')
  const [pageCount, setPageCount] = useState('')

  function handleSubmit(e) {
    e.preventDefault()
    fetch('/api/books', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, author, pageCount: parseInt(pageCount) }),
    }).then(() => {
      setTitle('')
      setAuthor('')
      setPageCount('')
      onBookAdded()
    })
  }

  return (
    <div>
      <h2>Add a Book</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Title: </label>
          <input value={title} onChange={e => setTitle(e.target.value)} required />
        </div>
        <div>
          <label>Author: </label>
          <input value={author} onChange={e => setAuthor(e.target.value)} required />
        </div>
        <div>
          <label>Page count: </label>
          <input type="number" value={pageCount} onChange={e => setPageCount(e.target.value)} required />
        </div>
        <button type="submit">Add Book</button>
      </form>
    </div>
  )
}

export default AddBook
