package com.library.service;

import com.library.model.Book;
import com.library.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private BookRepository repository;

    @Autowired
    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public void addBook(String title, String author, int pageCount) {
        log.info("Adding book: title='{}', author='{}', pageCount={}", title, author, pageCount);
        Book book = new Book(title, author, pageCount);
        repository.save(book);
        log.info("Book saved with id={}", book.getId());
    }

    public List<Book> getAllBooks() {
        List<Book> books = repository.findAll();
        log.debug("getAllBooks() returned {} book(s)", books.size());
        return books;
    }

    public String getBookSummary(String title) {
        log.debug("Looking up summary for title='{}'", title);
        return repository.findByTitle(title)
                .map(Book::getSummary)
                .orElseGet(() -> {
                    log.warn("Book not found: '{}'", title);
                    return "Book not found: " + title;
                });
    }
}

