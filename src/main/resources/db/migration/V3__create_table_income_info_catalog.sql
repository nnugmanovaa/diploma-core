create sequence if not exists loan.income_info_catalog_seq_id INCREMENT BY 1 START WITH 100;

create table if not exists loan.income_info_catalog (
    income_info_catalog_id integer default nextval('loan.income_info_catalog_seq_id'::regclass) not null,
    type varchar not null,
    value varchar not null,
    description varchar(1000)
);
