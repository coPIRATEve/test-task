-- Создание тестовых пользователей
INSERT INTO users (username, password, email, first_name, last_name, role)
VALUES
('admin', '$2a$10$rOzZUV/7y.7sCwJQ6ZzZQuK8t8t8t8t8t8t8t8t8t8t8t8t8t8t8', 'admin@company.com', 'Admin', 'User', 'ADMIN'),
('user1', '$2a$10$rOzZUV/7y.7sCwJQ6ZzZQuK8t8t8t8t8t8t8t8t8t8t8t8t8t8t8', 'user1@mail.com', 'John', 'Doe', 'USER')
ON CONFLICT (username) DO NOTHING;