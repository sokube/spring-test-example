-- CREATE SEQUENCE SEQ_PK_T_STAT INCREMENT BY 1;

CREATE TABLE T_COUNTER
(
    PK_T_COUNTER SERIAL PRIMARY KEY NOT NULL,
    S_NAME       VARCHAR(255)       NOT NULL,
    L_COUNT      INTEGER            NOT NULL
);