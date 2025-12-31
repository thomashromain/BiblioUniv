package fr.upjvthomashromain.bibliouniv.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "`role_name`", nullable = false)
    private String roleName;

    private boolean canManipulateBooks;

    // Constructors
    public Role() {}

    public Role(String roleName, boolean canManipulateBooks) {
        this.roleName = roleName;
        this.canManipulateBooks = canManipulateBooks;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public boolean isCanManipulateBooks() {
        return canManipulateBooks;
    }
}