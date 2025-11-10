INSERT INTO users (id, email, balance, is_active, created_at, updated_at)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'alice@example.com', 150.50, TRUE, NOW(), NOW()),
    ('22222222-2222-2222-2222-222222222222', 'bob@example.com', 75.00, TRUE, NOW(), NOW()),
    ('33333333-3333-3333-3333-333333333333', 'charlie@example.com', 0.00, FALSE, NOW(), NOW()),
    ('44444444-4444-4444-4444-444444444444', 'diana@example.com', 1200.00, TRUE, NOW(), NOW()),
    ('55555555-5555-5555-5555-555555555555', 'eve@example.com', 300.75, TRUE, NOW(), NOW());
