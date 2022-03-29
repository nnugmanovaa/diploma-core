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
)

CREATE SEQUENCE users_user_id_seq INCREMENT BY 1 START WITH 100;
COMMENT ON SEQUENCE users_user_id_seq IS 'Users Sequence.';

CREATE TABLE main.users
(
    user_id       INTEGER     DEFAULT nextval('users_user_id_seq'::regclass) NOT NULL,
    username      VARCHAR(250)                                                    NOT NULL,
    password      TEXT                                                            NOT NULL,
    inserted_time TIMESTAMP(3)                                                    NOT NULL,
    updated_time  TIMESTAMP(3),
    operators_id  INTEGER                                                         NULL,
    owner_id      INTEGER                                                         NULL,
    owner_type    VARCHAR(20)                                                     NULL CHECK (owner_type in
        ('OPERATOR',
        'AGENT',
        'MERCHANT',
        'BANK',
        'CLIENT',
        'SALES_POINT')),
    updated_by    VARCHAR(50)                                                     NULL,
    inserted_by   VARCHAR(50)                                                     NOT NULL,
    status        VARCHAR(10) DEFAULT 'ACTIVE'                                    NOT NULL CHECK (status in ('ACTIVE', 'INACTIVE', 'DELETED'))
)

ALTER TABLE loan.users
    ADD COLUMN IF NOT EXISTS firstname      VARCHAR(100)             NULL,
    ADD COLUMN IF NOT EXISTS lastname       VARCHAR(100)             NULL,
    ADD COLUMN IF NOT EXISTS email          VARCHAR(100)             NULL;

CREATE TABLE main.user_roles
(
    user_id INTEGER      NOT NULL,
    role    VARCHAR(250) NOT NULL
);

INSERT INTO main.user_roles(user_id, role)
VALUES (1, 'ROOT');
