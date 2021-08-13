/* creating a sequence*/
CREATE SEQUENCE loan.loan_payments_id_seq INCREMENT BY 1 START WITH 1000;
COMMENT ON SEQUENCE loan.loan_payments_id_seq IS 'Loan payments Sequence.';

/* create a table */
CREATE TABLE IF NOT EXISTS loan.loan_payments
(
    payment_id                  INTEGER                     DEFAULT nextval('loan.loan_payments_id_seq'::regclass) NOT NULL,
    amount                      DECIMAL(20, 2) NOT NULL,
    client_ref                  VARCHAR(16)    NOT NULL,
    contract_date               VARCHAR(16)    NOT NULL,
    contract_number             VARCHAR        NOT NULL,
    currency                    VARCHAR(10)    NOT NULL,
    description                 VARCHAR,
    ext_ref_id                  VARCHAR,
    ext_ref_time                TIMESTAMP WITHOUT TIME ZONE,
    ext_uuid                    VARCHAR,
    init_payment_status         VARCHAR(50),
    loan_pay_type               VARCHAR(50),
    mfo_processing_message      VARCHAR,
    mfo_processing_status       VARCHAR(50),
    order_state                 VARCHAR(50),
    init_payment_response       jsonb,
    inserted_time               TIMESTAMP WITHOUT TIME ZONE,
    updated_time                TIMESTAMP WITHOUT TIME ZONE
);

/* adding primary key */
ALTER TABLE loan.loan_payments
    ADD CONSTRAINT pk_loan_payments
        PRIMARY KEY (payment_id);

/* adding indexes */
CREATE INDEX idx_loan_payments_iin                      ON loan.loan_payments (client_ref);
CREATE INDEX idx_loan_payments_mfo_processing_status    ON loan.loan_payments (mfo_processing_status);
CREATE INDEX idx_loan_payments_order_state              ON loan.loan_payments (order_state);
CREATE INDEX idx_loan_payments_inserted_time            ON loan.loan_payments (inserted_time);

