package fr.upjvthomashromain.bibliouniv.controller;

import fr.upjvthomashromain.bibliouniv.entity.Book;
import fr.upjvthomashromain.bibliouniv.entity.BookWithCount;
import fr.upjvthomashromain.bibliouniv.repository.BookRepository;
import fr.upjvthomashromain.bibliouniv.repository.BookInstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

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
        boolean isAdmin = checkIsAdmin();
        return bookRepository.findAll().stream()
                .map(book -> new BookWithCount(
                    book, 
                    bookInstanceRepository.countByBookId(book.getId()), 
                    isAdmin, 
                    isAdmin
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookWithCount> getBookById(@PathVariable Long id) {
        boolean isAdmin = checkIsAdmin();
        return bookRepository.findById(id)
                .map(book -> {
                    long count = bookInstanceRepository.countByBookId(book.getId());
                    // Assign to a variable first to help the compiler
                    BookWithCount dto = new BookWithCount(book, count, isAdmin, isAdmin);
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private boolean checkIsAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        
        // Match the uppercase "ROLE_ADMIN" produced by your CustomUserDetailsService
        return auth.getAuthorities().stream()
                   .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}