package fr.upjvthomashromain.bibliouniv.repository;

import fr.upjvthomashromain.bibliouniv.entity.BookInstance;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookInstanceRepository extends JpaRepository<BookInstance, Long> {

    @Query("SELECT COUNT(bi) FROM BookInstance bi WHERE bi.book.id = :bookId")
    long countByBookId(@Param("bookId") Long bookId);

    @Query("SELECT bi FROM BookInstance bi WHERE bi.book.id = :bookId")
    List<BookInstance> findFirstByBookId(Long bookId);
}