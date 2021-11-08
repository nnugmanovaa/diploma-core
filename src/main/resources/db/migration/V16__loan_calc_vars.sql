CREATE TABLE IF NOT EXISTS loan.loan_vars
(
    loan_var_id  character varying(20) PRIMARY KEY,
    value_type   VARCHAR(15)  NOT NULL,
    value    VARCHAR(128)
);

INSERT INTO loan.loan_vars
(loan_var_id, value_type, value)
VALUES
    ('MIN_AMOUNT', 'DECIMAL', '5000'),
    ('MAX_AMOUNT', 'DECIMAL', '500000'),
    ('MIN_PERIOD', 'INTEGER', '1'),
    ('MAX_PERIOD', 'INTEGER', '36'),
    ('MCI', 'DECIMAL', '2917'),
    ('LOAN_INTEREST', 'DECIMAL', '36'),
    ('ANNUITY_METHOD_ON', 'BOOL', 'true'),
    ('GRADED_METHOD_ON', 'BOOL', 'true'),
    ('DEFAULT_METHOD', 'STRING', 'annuity'),
    ('DEFAULT_AMOUNT', 'DECIMAL', '100000'),
    ('DEFAULT_PERIOD', 'INTEGER', '18');