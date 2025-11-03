INSERT INTO public.transactions (amount, bet_id, type, date)
VALUES
    (100.00, NULL, 'DEPOSIT', NOW()),
    (50.00,  NULL, 'WITHDRAWAL', NOW()),
    (20.00,  1,    'BET_PLACED', NOW()),
    (40.00,  2,    'BET_WIN', NOW()),
    (15.00,  3,    'BET_LOSS', NOW());
