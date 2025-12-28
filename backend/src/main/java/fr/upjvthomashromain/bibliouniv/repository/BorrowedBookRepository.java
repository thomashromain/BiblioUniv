package fr.upjvthomashromain.bibliouniv.repository;

import fr.upjvthomashromain.bibliouniv.entity.BorrowedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {
}