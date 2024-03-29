create sequence if not exists loan.loan_requests_history_seq_id INCREMENT BY 1 START WITH 100;

create table if not exists loan.loan_requests_history (
    loan_requests_history_id integer default nextval('loan.loan_requests_history_seq_id'::regclass) not null,
    iin varchar(12),
    lastname varchar,
    firstname varchar,
    middlename varchar,
    birthdate timestamp,
    id_number varchar,
    nationality varchar,
    issued_by varchar,
    issued_date timestamp,
    expire_date timestamp,
    residence_oblast varchar,
    residence_region varchar,
    residence_city varchar,
    residence_postal_code varchar,
    residence_street varchar,
    residence_house varchar,
    residence_apartment varchar,
    registration_oblast varchar,
    registration_region varchar,
    registration_city varchar,
    registration_postal_code varchar,
    registration_street varchar,
    registration_house varchar,
    registration_apartment varchar,
    education varchar,
    employment varchar,
    type_of_work varchar,
    work_position varchar,
    employer varchar,
    monthly_income double precision,
    additional_monthly_income double precision,
    work_experience integer,
    work_phone_num varchar,
    marital_status varchar,
    number_of_kids integer,
    loan_amount double precision,
    loan_period integer,
    loan_method varchar
);
