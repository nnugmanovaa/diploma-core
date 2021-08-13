CREATE SEQUENCE loan.loan_orders_id_seq INCREMENT BY 1 START WITH 100000;
COMMENT ON SEQUENCE loan.loan_orders_id_seq IS 'Loan orders Sequence.';

CREATE TABLE IF NOT EXISTS loan.loan_orders
(
    order_id                 INTEGER                     DEFAULT nextval('loan.loan_orders_id_seq'::regclass) NOT NULL,
    parent_order_id          INTEGER,                                                                                                 --if PRIMARY NULL, if ALTERNATIVE then PRIMARY order_id
    order_type               VARCHAR(50)                                                                      NOT NULL,               -- ENUM (PRIMARY, ALTERNATIVE)
    order_status             VARCHAR(50)                                                                      NOT NULL DEFAULT 'NEW', -- ENUM (NEW, SCORING, APPROVED, REJECTED, CONFIRMED, CASHED_OUT_WALLET, CASHED_OUT_CARD, CLOSED)

    inserted_time            TIMESTAMP WITHOUT TIME ZONE DEFAULT now()                                        NOT NULL,
    updated_time             TIMESTAMP WITHOUT TIME ZONE DEFAULT now()                                        NOT NULL,
    closed_time              TIMESTAMP WITHOUT TIME ZONE,                                                                             --defined when order goes to status CLOSED

    client_id                INTEGER        NOT NULL,               -- foreign key to client
    client_info              VARCHAR        NOT NULL,
    msisdn                   VARCHAR(16)    NOT NULL,               -- msisdn in format 77XXXXXXXXX
    iin                      VARCHAR(16)    NOT NULL,
    personal_info            jsonb,

    cashout_info             jsonb,

    loan_amount              DECIMAL(20, 2) NOT NULL,
    loan_period_months       INTEGER        NOT NULL,
    loan_method              VARCHAR        NOT NULL,
    loan_product             VARCHAR,
    loan_effective_rate      DECIMAL(20, 2),

    order_ext_ref_id         VARCHAR,                                                                                                 --reference order id in 1C MFO
    order_ext_ref_time       TIMESTAMP WITHOUT TIME ZONE,                                                                             --reference order date in 1C MFO
    contract_ext_ref_id      VARCHAR,                                                                                                 --reference order id in 1C MFO
    contract_ext_ref_time    TIMESTAMP WITHOUT TIME ZONE,                                                                             --reference order date in 1C MFO
    contract_document_s3_key VARCHAR        NULL,

    scoring_reject_reason    VARCHAR,
    ip_address               VARCHAR
);

/* Add Primary Key */
ALTER TABLE loan.loan_orders
    ADD CONSTRAINT pk_loan_orders
        PRIMARY KEY (order_id);

CREATE INDEX idx_loan_orders_clients ON loan.loan_orders (client_id);
CREATE INDEX idx_loan_orders_iin ON loan.loan_orders (iin);
CREATE INDEX idx_loan_orders_msisdn ON loan.loan_orders (msisdn);
CREATE INDEX idx_loan_orders_status ON loan.loan_orders (order_status);
CREATE INDEX idx_loan_orders_inserted_time ON loan.loan_orders (inserted_time);

CREATE SEQUENCE loan.loan_orders_log_id_seq INCREMENT BY 1 START WITH 1000;
COMMENT ON SEQUENCE loan.loan_orders_log_id_seq IS 'Loan Orders Log Sequence.';

CREATE TABLE IF NOT EXISTS loan.loan_orders_log
(
    order_log_id INTEGER                     DEFAULT nextval('loan.loan_orders_log_id_seq'::regclass) NOT NULL,
    order_id     INTEGER                                                                              NOT NULL, --foreign key to loan_orders.order_id
    log_datetime TIMESTAMP WITHOUT TIME ZONE DEFAULT now()                                            NOT NULL,
    change_type  VARCHAR(32),                                                                                   --ENUM (CHANGE_STATUS,... etc)
    old_values   jsonb,                                                                                         --fields that have been changed and their old values
    new_values   jsonb                                                                                          --fields that have been changed and their new values
) partition by range (log_datetime);

/* Add Primary Key */
ALTER TABLE loan.loan_orders_log
    ADD CONSTRAINT pk_loan_orders_log
        PRIMARY KEY (order_log_id, log_datetime);

CREATE INDEX idx_loan_orders_log_inserted_time ON loan.loan_orders_log (log_datetime);
CREATE INDEX idx_loan_orders_log_order_id ON loan.loan_orders_log (order_id);

CREATE TABLE loan.loan_orders_log_2020 PARTITION OF loan.loan_orders_log
    FOR VALUES FROM ('2015-01-01') TO ('2021-01-01');
