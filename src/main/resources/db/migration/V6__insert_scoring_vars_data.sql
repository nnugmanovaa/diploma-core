INSERT INTO loan.scoring_vars (display_name, constant_name, type, value) VALUES
('minScoreBall', 'MIN_SCORE_BALL', 'INTEGER', '0'),
('maxPDBadRate', 'MAX_PD_BAD_RATE', 'DOUBLE', '5.0'),
('ficoScoring', 'FICO_SCORING', 'BOOLEAN', 'false'),
('behavioralScoring', 'BEHAVIORAL_SCORING', 'BOOLEAN', 'true'),
('minRate', 'MIN_RATE', 'DOUBLE', '1.0'),
('maxGESV', 'MAX_GESV', 'DOUBLE', '1.0'),
('minLoanAmount', 'MIN_LOAN_AMOUNT', 'DOUBLE', '100'),
('maxLoanAmount', 'MAX_LOAN_AMOUNT', 'DOUBLE', '1000000'),
('minLoanPeriod', 'MIN_LOAN_PERIOD', 'INTEGER', '1'),
('maxLoanPeriod', 'MAX_LOAN_PERIOD', 'INTEGER', '12'),
('alternativeAmountStep', 'ALT_AMOUNT_STEP', 'DOUBLE', '50000'),
('alternativePeriodStep', 'ALT_PERIOD_STEP', 'INTEGER', '6'),
('costOfLiving', 'COST_OF_LIVING', 'DOUBLE', '42500');
