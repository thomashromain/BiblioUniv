-- Sample seed file for BiblioUniv
-- This creates a simple `book` table and inserts a few rows.
-- Edit this file to match the PDF-provided data if necessary.

CREATE TABLE IF NOT EXISTS book (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255),
  published_year INT,
  isbn VARCHAR(50)
);

INSERT INTO book (title, author, published_year, isbn) VALUES
('Introduction to Algorithms', 'Cormen et al.', 2009, '9780262033848'),
('Clean Code', 'Robert C. Martin', 2008, '9780132350884'),
('Design Patterns', 'Gamma et al.', 1994, '9780201633610');
