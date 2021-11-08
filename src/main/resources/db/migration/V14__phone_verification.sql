CREATE TABLE IF NOT EXISTS phone_verification (
   verification_id SERIAL PRIMARY KEY,
   msisdn VARCHAR(15) NOT NULL UNIQUE,
   code VARCHAR(6) NOT NULL,
   last_request_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL
);