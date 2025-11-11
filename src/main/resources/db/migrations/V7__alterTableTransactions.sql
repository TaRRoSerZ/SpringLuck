CREATE TYPE transaction_status AS ENUM ('PENDING', 'FAILED', 'CONFIRMED');

ALTER TABLE transactions ADD COLUMN stripe_intent_id VARCHAR(255);
ALTER TABLE transactions ADD COLUMN status transaction_status NOT NULL DEFAULT 'PENDING';