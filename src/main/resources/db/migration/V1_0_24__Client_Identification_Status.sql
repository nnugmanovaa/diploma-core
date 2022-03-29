ALTER TABLE main.clients
    ADD COLUMN IF NOT EXISTS identification_status VARCHAR(20) DEFAULT 'UNIDENTIFIED' NOT NULL CHECK (identification_status IN ('UNIDENTIFIED','IDENTIFIED'));

ALTER TABLE main.clients
    ADD COLUMN IF NOT EXISTS mname varchar(100) NULL;


alter table main.clients
    add column IF NOT EXISTS lname VARCHAR(100) NULL,
    add column IF NOT EXISTS iin VARCHAR(12)  NULL,
    add column IF NOT EXISTS birth_date DATE NULL;

ALTER TABLE main.clients
    ADD COLUMN IF NOT EXISTS avatar_url varchar(250) NULL;

ALTER TABLE main.clients
    ADD COLUMN IF NOT EXISTS notification_token VARCHAR(250) NULL;