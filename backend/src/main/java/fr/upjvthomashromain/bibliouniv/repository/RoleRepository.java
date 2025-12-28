package fr.upjvthomashromain.bibliouniv.repository;

import fr.upjvthomashromain.bibliouniv.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}