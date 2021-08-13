create schema if not exists loan;

create table if not exists loan.pkb_checks_management (
    code varchar primary key not null,
    title varchar(1000) not null,
    is_active boolean default true,
    rejection_text varchar
);

create sequence if not exists loan.pkb_checks_result_history_seq_id INCREMENT BY 1 START WITH 100;

create table if not exists loan.pkb_checks_result_history (
    pkb_checks_result_history_id integer default nextval('loan.pkb_checks_result_history_seq_id'::regclass) not null,
    iin varchar not null,
    request_date timestamp,
    code varchar not null,
    title varchar(1000),
    status varchar not null,
    source varchar,
    refresh_date timestamp,
    actual_date timestamp,
    search_by varchar
)
