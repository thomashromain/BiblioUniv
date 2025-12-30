package fr.upjvthomashromain.bibliouniv.entity;

public class BookWithCount {
    private Long id;
    private String title;
    private String author;
    private Integer publishedYear;
    private String isbn;
    private String bookImage;
    private long instanceCount;
    private boolean canCreate;
    private boolean canDelete;

    public BookWithCount() {}

    public BookWithCount(Book book, long instanceCount, boolean canCreate, boolean canDelete) {
        this.id = (book != null) ? book.getId() : null;
        this.title = (book != null) ? book.getTitle() : null;
        this.author = (book != null) ? book.getAuthor() : null;
        this.publishedYear = (book != null) ? book.getPublishedYear() : null;
        this.isbn = (book != null) ? book.getIsbn() : null;
        this.bookImage = (book != null) ? book.getBookImage() : null;
        this.instanceCount = instanceCount;
        this.canCreate = canCreate;
        this.canDelete = canDelete;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public Integer getPublishedYear() { return publishedYear; }
    public String getIsbn() { return isbn; }
    public String getBookImage() { return bookImage; }
    public long getInstanceCount() { return instanceCount; }
    public boolean isCanCreate() { return canCreate; }
    public boolean isCanDelete() { return canDelete; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublishedYear(Integer publishedYear) { this.publishedYear = publishedYear; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setBookImage(String bookImage) { this.bookImage = bookImage; }
    public void setInstanceCount(long instanceCount) { this.instanceCount = instanceCount; }
    public void setCanCreate(boolean canCreate) { this.canCreate = canCreate; }
    public void setCanDelete(boolean canDelete) { this.canDelete = canDelete; }
}