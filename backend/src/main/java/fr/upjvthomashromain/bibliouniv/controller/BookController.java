package fr.upjvthomashromain.bibliouniv.controller;

import fr.upjvthomashromain.bibliouniv.entity.Book;
import fr.upjvthomashromain.bibliouniv.entity.BookWithCount;
import fr.upjvthomashromain.bibliouniv.repository.BookRepository;
import fr.upjvthomashromain.bibliouniv.repository.BookInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookInstanceRepository bookInstanceRepository;

    @GetMapping
    public List<BookWithCount> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(book -> new BookWithCount(book, bookInstanceRepository.countByBookId(book.getId())))
                .collect(Collectors.toList());
    }
}