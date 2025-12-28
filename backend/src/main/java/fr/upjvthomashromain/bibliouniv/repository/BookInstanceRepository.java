package fr.upjvthomashromain.bibliouniv.repository;

import fr.upjvthomashromain.bibliouniv.entity.BookInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookInstanceRepository extends JpaRepository<BookInstance, Long> {
}