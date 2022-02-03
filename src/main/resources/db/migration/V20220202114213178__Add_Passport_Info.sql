CREATE SEQUENCE loan.passport_info_passport_info_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE loan.passport_info
(
    passport_info_id INTEGER DEFAULT nextval('loan.passport_info_passport_info_id_seq'::regclass),
    first_name VARCHAR (30),
    last_name VARCHAR (30),
    patronymic VARCHAR (30),
    birth_date TIMESTAMP WITHOUT TIME ZONE,
    national_id_number VARCHAR (30),
    nationality VARCHAR (20),
    national_id_issuer VARCHAR (20),
    national_id_issue_date TIMESTAMP WITHOUT TIME ZONE,
    national_id_valid_date TIMESTAMP WITHOUT TIME ZONE,
    is_ipdl BOOLEAN NOT NULL DEFAULT false,
    clients_id INTEGER,
    PRIMARY KEY(passport_info_id)
);
