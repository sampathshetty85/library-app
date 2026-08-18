package com.library.api;

import com.library.model.Book;
import com.library.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/books")
public class BookController {

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    private BookService service;

    @Autowired
    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        log.info("GET /books");
        return ResponseEntity.ok(service.getAllBooks());
    }

    @PostMapping
    public ResponseEntity<Void> addBook(@RequestBody BookRequest request) {
        log.info("POST /books - title='{}'", request.getTitle());
        service.addBook(request.getTitle(), request.getAuthor(), request.getPageCount());
        return ResponseEntity.status(201).build();
    }
}

