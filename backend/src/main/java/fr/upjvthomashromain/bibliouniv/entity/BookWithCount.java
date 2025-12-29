package fr.upjvthomashromain.bibliouniv.entity;

public class BookWithCount {
    private Long id;
    private String title;
    private String author;
    private Integer publishedYear;
    private String isbn;
    private String bookImage;
    private long instanceCount;

    public BookWithCount() {}

    public BookWithCount(Book book, long instanceCount) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publishedYear = book.getPublishedYear();
        this.isbn = book.getIsbn();
        this.bookImage = book.getBookImage();
        this.instanceCount = instanceCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookImage() {
        return bookImage;
    }

    public void setBookImage(String bookImage) {
        this.bookImage = bookImage;
    }

    public long getInstanceCount() {
        return instanceCount;
    }

    public void setInstanceCount(long instanceCount) {
        this.instanceCount = instanceCount;
    }
}