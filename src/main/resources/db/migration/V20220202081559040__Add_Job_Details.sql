CREATE SEQUENCE loan.job_details_job_details_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE loan.job_details
(
    job_details_id INTEGER DEFAULT nextval('loan.job_details_job_details_id_seq'::regclass),
    education VARCHAR (30),
    employment VARCHAR (30),
    type_of_work VARCHAR (30),
    work_position VARCHAR (30),
    employer VARCHAR (30),
    monthly_income double precision,
    additional_monthly_income double precision,
    marital_status VARCHAR (10),
    number_of_kids INTEGER,
    clients_id INTEGER,

    PRIMARY KEY(job_details_id)
);