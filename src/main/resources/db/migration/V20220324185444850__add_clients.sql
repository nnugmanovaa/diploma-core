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
