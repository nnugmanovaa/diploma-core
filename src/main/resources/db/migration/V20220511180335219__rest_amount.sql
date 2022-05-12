ALTER TABLE loan.loan_orders
    ADD COLUMN rest_amount DECIMAL(20, 2) DEFAULT NULL;

ALTER TABLE loan.loan_orders
    ADD COLUMN rest_period INTEGER DEFAULT NULL;
