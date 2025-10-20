create table public.bets
(
    id          serial primary key,
    amount numeric(10,2) not null,
    date timestamp not null,
    isWinningBet boolean not null
);