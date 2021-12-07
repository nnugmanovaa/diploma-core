create sequence if not exists loan.blocked_users_seq_id INCREMENT BY 1 START WITH 100;

CREATE TABLE IF NOT EXISTS blocked_users (
    id integer default nextval('loan.blocked_users_seq_id':: regclass) not null,
    iin CHARACTER VARYING(20) NOT NULL,
    blocked_reason CHARACTER VARYING(128),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    author CHARACTER VARYING(128)
    );