CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role VARCHAR(20) DEFAULT 'USER'
);

-- Тестовые данные
INSERT INTO users (username, password, email, first_name, last_name, role)
VALUES
('admin', '$2a$10$rOzZUV/7y.7sCwJQ6ZzZQuK8t8t8t8t8t8t8t8t8t8t8t8t8t8t8', 'admin@company.com', 'Admin', 'User', 'ADMIN'),
('user1', '$2a$10$rOzZUV/7y.7sCwJQ6ZzZQuK8t8t8t8t8t8t8t8t8t8t8t8t8t8t8', 'user1@mail.com', 'John', 'Doe', 'USER');