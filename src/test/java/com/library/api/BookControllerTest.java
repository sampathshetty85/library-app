package com.library.api;

import com.library.model.Book;
import com.library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// No Spring context needed — we build MockMvc by hand.
// This tests the full HTTP layer (routing, JSON, status codes) without starting a server.
class BookControllerTest {

    private BookService bookService;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        // Create a plain Mockito mock — same pattern as BookServiceTest
        bookService = Mockito.mock(BookService.class);
        // Wire controller with the mock, then build MockMvc around it
        BookController controller = new BookController(bookService);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getAllBooks_returns200WithJsonArray() throws Exception {
        // ARRANGE — tell the fake service what to return
        List<Book> books = List.of(
            new Book("Dune", "Frank Herbert", 412),
            new Book("Atomic Habits", "James Clear", 320)
        );
        when(bookService.getAllBooks()).thenReturn(books);

        // ACT + ASSERT — send fake GET /books, check response
        mvc.perform(get("/books"))
            .andExpect(status().isOk())                               // HTTP 200
            .andExpect(jsonPath("$[0].title").value("Dune"))          // first book title
            .andExpect(jsonPath("$[1].author").value("James Clear")); // second book author
    }

    @Test
    void addBook_returns201() throws Exception {
        // ARRANGE — doNothing is the default for void methods, but explicit is clearer
        doNothing().when(bookService).addBook(anyString(), anyString(), anyInt());

        // ACT + ASSERT — send fake POST /books with JSON body
        mvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"Dune\",\"author\":\"Frank Herbert\",\"pageCount\":412}"))
            .andExpect(status().isCreated()); // HTTP 201
    }
}
