CREATE SEQUENCE seq_fund_id;

CREATE TABLE funds(
  fund_id NUMBER        NOT NULL PRIMARY KEY,
  name    VARCHAR2(200) NOT NULL,
  ticker  VARCHAR2(20)  NOT NULL,
  deleted TIMESTAMP     NULL
);