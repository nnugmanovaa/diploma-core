CREATE SCHEMA main;

CREATE SEQUENCE clients_clients_id_seq INCREMENT BY 1 START WITH 1000;
COMMENT ON SEQUENCE clients_clients_id_seq IS 'Clients Sequence.';
CREATE TABLE main.clients
(
    clients_id     INTEGER     DEFAULT nextval('clients_clients_id_seq'::regclass) NOT NULL,
    clientname     VARCHAR(50)                                                          NOT NULL,
    name           VARCHAR(250)                                                         NOT NULL,
    status         VARCHAR(10) DEFAULT 'ACTIVE'                                         NOT NULL CHECK (status in ('ACTIVE', 'SUSPENDED', 'BLACK')),
    status_time    TIMESTAMP(3)                                                         NOT NULL,
    status_comment VARCHAR(250)                                                         NULL,
    status_by      VARCHAR(50)                                                          NOT NULL,
    validate_data  JSONB                                                                NULL,
    inserted_by    VARCHAR(50)                                                          NOT NULL,
    updated_by     VARCHAR(50)                                                          NULL,
    inserted_time  TIMESTAMP(3)                                                         NOT NULL,
    updated_time   TIMESTAMP(3)                                                         NOT NULL
);

CREATE TABLE main.user_roles
(
    user_id INTEGER      NOT NULL,
    role    VARCHAR(250) NOT NULL
);

INSERT INTO main.user_roles(user_id, role)
VALUES (1, 'ROOT');

ALTER TABLE main.clients
    ADD COLUMN IF NOT EXISTS identification_status VARCHAR(20) DEFAULT 'UNIDENTIFIED' NOT NULL CHECK (identification_status IN ('UNIDENTIFIED','IDENTIFIED'));

ALTER TABLE main.clients
    ADD COLUMN IF NOT EXISTS mname varchar(100) NULL;


alter table main.clients
    add column IF NOT EXISTS lname VARCHAR(100) NULL,
    add column IF NOT EXISTS iin VARCHAR(12)  NULL,
    add column IF NOT EXISTS birth_date DATE NULL;

ALTER TABLE main.clients
    ADD COLUMN IF NOT EXISTS avatar_url varchar(250) NULL;

ALTER TABLE main.clients
    ADD COLUMN IF NOT EXISTS notification_token VARCHAR(250) NULL;
