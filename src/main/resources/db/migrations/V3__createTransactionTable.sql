CREATE TYPE transaction_type AS ENUM ('DEPOSIT', 'WITHDRAWAL', 'BET_LOSS', 'BET_WIN', 'BET_PLACED');

CREATE TABLE public.transactions
(
    id          serial primary key,
    amount numeric(10,2) not null,
    bet_id integer,
    type transaction_type not null,
    date timestamp not null
);