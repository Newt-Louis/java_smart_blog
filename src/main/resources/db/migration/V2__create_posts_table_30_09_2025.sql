CREATE TABLE posts (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id BIGINT NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       content TEXT NOT NULL,
                       status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       CONSTRAINT fk_posts_users FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;