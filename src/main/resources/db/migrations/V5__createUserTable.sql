CREATE TABLE users (
           id UUID PRIMARY KEY,
           email VARCHAR(255) NOT NULL UNIQUE,
           balance NUMERIC(12, 2) DEFAULT 0.00,
           is_active BOOLEAN DEFAULT TRUE,
           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
