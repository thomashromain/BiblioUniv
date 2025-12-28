-- Sample seed file for BiblioUniv
-- This creates tables for books, users, roles, book instances, and borrowed books.
-- Edit this file to match the PDF-provided data if necessary.

CREATE TABLE IF NOT EXISTS book (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  author VARCHAR(255),
  published_year INT,
  isbn VARCHAR(50),
  book_image VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  roleName VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS `user` (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) UNIQUE NOT NULL,
  password_sha256 VARCHAR(64) NOT NULL,
  role_id BIGINT,
  FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE IF NOT EXISTS book_instance (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  book_id BIGINT NOT NULL,
  FOREIGN KEY (book_id) REFERENCES book(id)
);

CREATE TABLE IF NOT EXISTS borrowed_book (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  book_instance_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  borrowedAtTime DATETIME NOT NULL,
  returnAtTime DATETIME,
  returnedBoolean BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (book_instance_id) REFERENCES book_instance(id),
  FOREIGN KEY (user_id) REFERENCES `user`(id)
);

INSERT INTO book (title, author, published_year, isbn, book_image) VALUES
('Introduction to Algorithms', 'Cormen et al.', 2009, '9780262033848', 'algo.jpg'),
('Clean Code', 'Robert C. Martin', 2008, '9780132350884', 'cleancode.jpg'),
('Design Patterns', 'Gamma et al.', 1994, '9780201633610', 'patterns.jpg');

INSERT INTO role (roleName) VALUES
('admin'),
('user');

INSERT INTO `user` (username, password_sha256, role_id) VALUES
('admin', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 1), 
('user1', '04f8996da763b7a969b1028ee3007569eaf3a635486ddab211d512c85b9df8fb', 2);

INSERT INTO book_instance (book_id) VALUES
(1),
(2),
(3);

INSERT INTO borrowed_book (book_instance_id, user_id, borrowedAtTime, returnAtTime, returnedBoolean) VALUES
(1, 2, '2023-01-01 10:00:00', '2023-01-15 10:00:00', TRUE);
