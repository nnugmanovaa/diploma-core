CREATE SEQUENCE loan.address_info_address_info_id_seq INCREMENT BY 1 START WITH 1000;

CREATE TABLE loan.address_info
(
    address_info_id INTEGER DEFAULT nextval('loan.address_info_address_info_id_seq'::regclass),
    region VARCHAR (30),
    city VARCHAR (30),
    postal_code VARCHAR (10),
    street VARCHAR (50),
    house VARCHAR (10),
    apartment VARCHAR (10),
    address_is_valid BOOLEAN NOT NULL DEFAULT false,
    period_of_residence VARCHAR (10),
    clients_id INTEGER,

    PRIMARY KEY(address_info_id)
);
