create table public.bets
(
    id          UUID primary key default gen_random_uuid(),
    user_id     UUID not null,
    amount numeric(10,2) not null,
    date timestamp not null,
    isWinningBet boolean not null
);