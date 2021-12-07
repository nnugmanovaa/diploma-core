ALTER TABLE loan.loan_payments
    ADD CONSTRAINT fk_loan_payments_loan_orders
        FOREIGN KEY (loan_order_id) REFERENCES loan.loan_orders (order_id)
            ON UPDATE NO ACTION ON DELETE NO ACTION;