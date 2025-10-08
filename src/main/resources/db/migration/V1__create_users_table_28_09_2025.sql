CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL, -- Sẽ lưu password đã được hash
                       email VARCHAR(100) NOT NULL UNIQUE,
                       full_name VARCHAR(100),
                       role VARCHAR(20) NOT NULL DEFAULT 'USER', -- Ví dụ: USER, ADMIN
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;