package fr.upjvthomashromain.bibliouniv.repository;

import fr.upjvthomashromain.bibliouniv.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.username = :username")
    User findByUsername(@Param("username") String username);

}