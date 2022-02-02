create sequence if not exists loan.repayment_schedule_seq_id INCREMENT BY 1 START WITH 100;
COMMENT ON SEQUENCE loan.repayment_schedule_seq_id IS 'Loan repayment schedule Sequence.';

create sequence if not exists loan.repayment_schedule_items_seq_id INCREMENT BY 1 START WITH 1000;
COMMENT ON SEQUENCE loan.repayment_schedule_items_seq_id IS 'Loan repayment schedule items Sequence.';

CREATE TABLE IF NOT EXISTS repayment_schedule (
    repayment_schedule_id INTEGER DEFAULT nextval('loan.repayment_schedule_seq_id':: regclass) NOT NULL,
    order_id INTEGER NOT NULL,
    amount_overpayment DECIMAL(20,2),
    amount_remain DECIMAL(20,2)
    );

CREATE TABLE IF NOT EXISTS repayment_schedule_items (
    item_id INTEGER DEFAULT nextval('loan.repayment_schedule_items_seq_id':: regclass) NOT NULL,
    repayment_schedule_id INTEGER NOT NULL,
    payment_id INTEGER,
    number_id INTEGER,
    payment_date VARCHAR(20),
    reward DECIMAL(20, 2),
    total_amount_debt DECIMAL(20, 2),
    amount_to_be_paid DECIMAL(20, 2),
    status VARCHAR(20)
    );