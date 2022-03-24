CREATE SEQUENCE users_user_id_seq INCREMENT BY 1 START WITH 100;
COMMENT ON SEQUENCE users_user_id_seq IS 'Users Sequence.';

CREATE TABLE main.users
(
    user_id       INTEGER     DEFAULT nextval('users_user_id_seq'::regclass) NOT NULL,
    username      VARCHAR(250)                                                    NOT NULL,
    password      TEXT                                                            NOT NULL,
    inserted_time TIMESTAMP(3)                                                    NOT NULL,
    updated_time  TIMESTAMP(3),
    operators_id  INTEGER                                                         NULL,
    owner_id      INTEGER                                                         NULL,
    owner_type    VARCHAR(20)                                                     NULL CHECK (owner_type in
        ('OPERATOR',
        'AGENT',
        'MERCHANT',
        'BANK',
        'CLIENT',
        'SALES_POINT')),
    updated_by    VARCHAR(50)                                                     NULL,
    inserted_by   VARCHAR(50)                                                     NOT NULL,
    status        VARCHAR(10) DEFAULT 'ACTIVE'                                    NOT NULL CHECK (status in ('ACTIVE', 'INACTIVE', 'DELETED'))
)