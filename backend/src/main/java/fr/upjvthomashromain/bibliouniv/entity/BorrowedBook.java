package fr.upjvthomashromain.bibliouniv.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class BorrowedBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bookInstanceId")
    private BookInstance bookInstance;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private LocalDateTime borrowedAtTime;
    private LocalDateTime returnAtTime;
    private Boolean returnedBoolean;

    // Constructors
    public BorrowedBook() {}

    public BorrowedBook(BookInstance bookInstance, User user, LocalDateTime borrowedAtTime, LocalDateTime returnAtTime, Boolean returnedBoolean) {
        this.bookInstance = bookInstance;
        this.user = user;
        this.borrowedAtTime = borrowedAtTime;
        this.returnAtTime = returnAtTime;
        this.returnedBoolean = returnedBoolean;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BookInstance getBookInstance() {
        return bookInstance;
    }

    public void setBookInstance(BookInstance bookInstance) {
        this.bookInstance = bookInstance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getBorrowedAtTime() {
        return borrowedAtTime;
    }

    public void setBorrowedAtTime(LocalDateTime borrowedAtTime) {
        this.borrowedAtTime = borrowedAtTime;
    }

    public LocalDateTime getReturnAtTime() {
        return returnAtTime;
    }

    public void setReturnAtTime(LocalDateTime returnAtTime) {
        this.returnAtTime = returnAtTime;
    }

    public Boolean getReturnedBoolean() {
        return returnedBoolean;
    }

    public void setReturnedBoolean(Boolean returnedBoolean) {
        this.returnedBoolean = returnedBoolean;
    }
}