INSERT INTO loan.scoring_vars
(display_name, constant_name, type, value, description)
VALUES
    ('numberOfActiveLoans', 'NUMBER_OF_ACTIVE_LOANS', 'DOUBLE', '0.2146', 'Description'),
    ('income', 'INCOME', 'DOUBLE', '-0.6156', 'Description'),
    ('debt', 'DEBT', 'DOUBLE', '0.2760', 'Description'),
    ('maxDelayInDays', 'MAX_DELAY_IN_DAYS', 'DOUBLE', '0.6576', 'Description'),
    ('totalNumberOfDelays', 'TOTAL_NUMBER_OF_DELAYS', 'DOUBLE', '0.5245', 'Description'),
    ('maxOverdueAmount', 'MAX_OVERDUE_AMOUNT', 'DOUBLE', '0.6482', 'Description'),
    ('loanAmount', 'LOAN_AMOUNT', 'DOUBLE', '-0.4358', 'Description'),
    ('loanPeriod', 'PERIOD', 'DOUBLE', '0.3988', 'Description'),
    ('numberOfKids', 'NUMBER_OF_KIDS', 'DOUBLE', '0.7897', 'Description');
