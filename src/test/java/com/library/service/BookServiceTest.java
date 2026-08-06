package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    private BookRepository repository;
    private BookService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(BookRepository.class);
        service = new BookService(repository);
    }

    @Test
    void addBook_savesBookWithCorrectFields() {
        service.addBook("Dune", "Frank Herbert", 412);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(repository, times(1)).save(captor.capture());

        Book saved = captor.getValue();
        assertEquals("Dune", saved.getTitle());
        assertEquals("Frank Herbert", saved.getAuthor());
        assertEquals(412, saved.getPageCount());
        assertTrue(saved.isAvailable());
    }

    @Test
    void getAllBooks_returnsAllBooksFromRepository() {
        List<Book> books = List.of(
            new Book("Dune", "Frank Herbert", 412),
            new Book("Atomic Habits", "James Clear", 320)
        );
        when(repository.findAll()).thenReturn(books);

        List<Book> result = service.getAllBooks();

        assertEquals(2, result.size());
        assertEquals("Dune", result.get(0).getTitle());
    }

    @Test
    void getBookSummary_whenBookExists_returnsSummary() {
        Book book = new Book("Dune", "Frank Herbert", 412);
        when(repository.findByTitle("Dune")).thenReturn(Optional.of(book));

        String summary = service.getBookSummary("Dune");

        assertTrue(summary.contains("Dune"));
        assertTrue(summary.contains("Frank Herbert"));
    }

    @Test
    void getBookSummary_whenBookNotFound_returnsNotFoundMessage() {
        when(repository.findByTitle("Unknown")).thenReturn(Optional.empty());

        String summary = service.getBookSummary("Unknown");

        assertEquals("Book not found: Unknown", summary);
    }
}
