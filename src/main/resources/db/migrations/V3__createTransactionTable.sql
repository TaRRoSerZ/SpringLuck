CREATE TYPE transaction_type AS ENUM ('DEPOSIT', 'WITHDRAWAL', 'BET_LOSS', 'BET_WIN', 'BET_PLACED');

CREATE TABLE public.transactions
(
    id          UUID primary key default gen_random_uuid(),
    amount numeric(10,2) not null,
    bet_id UUID,
    user_id UUID not null,
    type transaction_type not null,
    date timestamp not null
);