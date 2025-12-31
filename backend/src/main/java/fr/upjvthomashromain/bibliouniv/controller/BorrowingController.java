package fr.upjvthomashromain.bibliouniv.controller;

import fr.upjvthomashromain.bibliouniv.configuration.CustomUserDetails;
import fr.upjvthomashromain.bibliouniv.dto.BorrowingResponse;
import fr.upjvthomashromain.bibliouniv.entity.*;
import fr.upjvthomashromain.bibliouniv.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    @Autowired private BorrowedBookRepository borrowedBookRepository;
    @Autowired private BookInstanceRepository bookInstanceRepository;

    @GetMapping("/my-books")
    public List<BorrowingResponse> getMyBorrowedBooks() {
        User currentUser = getCurrentUser();
        return borrowedBookRepository.findByUserAndReturnedBooleanFalse(currentUser)
                .stream()
                .map(b -> new BorrowingResponse(
                        b.getId(),
                        b.getBookInstance().getBook().getTitle(),
                        b.getBookInstance().getBook().getAuthor(),
                        b.getBorrowedAtTime(),
                        b.getReturnAtTime(),
                        b.getReturnedBoolean()
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/request/{bookId}")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookId) {
        User currentUser = getCurrentUser();

        // 1. Find an available instance of this book
        // Assumes you have a method findAvailableInstance in your repository
        List<BookInstance> availableInstances = bookInstanceRepository.findFirstByBookId(bookId);
        
        // Filter logic: In a real app, check if the instance is already borrowed
        BookInstance instanceToBorrow = availableInstances.stream()
                .findFirst()
                .orElse(null);

        if (instanceToBorrow == null) {
            return ResponseEntity.badRequest().body("No copies available for this book.");
        }

        // 2. Create the borrowing record
        BorrowedBook borrowing = new BorrowedBook();
        borrowing.setUser(currentUser);
        borrowing.setBookInstance(instanceToBorrow);
        borrowing.setBorrowedAtTime(LocalDateTime.now());
        borrowing.setReturnAtTime(LocalDateTime.now().plusWeeks(2)); // 2 weeks limit
        borrowing.setReturnedBoolean(false);

        borrowedBookRepository.save(borrowing);

        return ResponseEntity.ok("Book borrowed successfully until " + borrowing.getReturnAtTime());
    }

    @GetMapping("/late-count")
    public ResponseEntity<Integer> getLateBooksCount() {
    User currentUser = getCurrentUser();
    List<BorrowedBook> myBooks = borrowedBookRepository.findByUserAndReturnedBooleanFalse(currentUser);
    
    long lateCount = myBooks.stream()
            .filter(b -> b.getReturnAtTime().isBefore(LocalDateTime.now()))
            .count();
            
    return ResponseEntity.ok((int) lateCount);
}

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails details = (CustomUserDetails) auth.getPrincipal();
        return details.getUser();
    }
}