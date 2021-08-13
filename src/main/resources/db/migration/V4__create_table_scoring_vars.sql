create sequence if not exists loan.scoring_vars_seq_id INCREMENT BY 1 START WITH 100;

create table if not exists loan.scoring_vars (
    scoring_vars_id integer default nextval('loan.scoring_vars_seq_id'::regclass) not null,
    display_name varchar not null,
    constant_name varchar,
    type varchar not null,
    value varchar not null,
    description varchar(1000)
);
