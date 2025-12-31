package fr.upjvthomashromain.bibliouniv.repository;

import fr.upjvthomashromain.bibliouniv.entity.BorrowedBook;
import fr.upjvthomashromain.bibliouniv.entity.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {
    // Find all books currently borrowed by a user (not yet returned)
    List<BorrowedBook> findByUserAndReturnedBooleanFalse(User user);
    
    // Find all borrowing history for a user
    List<BorrowedBook> findByUserId(Long userId);
}