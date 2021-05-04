CREATE OR REPLACE PACKAGE pkg_funds AS

    FUNCTION save(p_fund_id NUMBER, p_name VARCHAR2, p_ticker VARCHAR2) RETURN NUMBER;
    PROCEDURE delete(p_fund_id NUMBER);
    no_comm EXCEPTION;
    no_sal EXCEPTION;

END pkg_funds;
/

CREATE OR REPLACE PACKAGE BODY pkg_funds AS

    FUNCTION save(p_fund_id NUMBER, p_name VARCHAR2, p_ticker VARCHAR2) RETURN NUMBER
    IS
        new_fund_id NUMBER;
    BEGIN
        IF p_fund_id IS NULL THEN
          SELECT seq_fund_id.NEXTVAL INTO new_fund_id FROM DUAL;
          INSERT INTO funds(fund_id, name, ticker) VALUES (new_fund_id, p_name, p_ticker);
          RETURN new_fund_id;
        ELSE
          UPDATE funds SET name = p_name, ticker = p_ticker WHERE fund_id = p_fund_id;
          RETURN p_fund_id;
        END IF;
    END;

    PROCEDURE delete(p_fund_id NUMBER) IS
    BEGIN
        UPDATE funds SET deleted = CURRENT_TIMESTAMP WHERE fund_id = p_fund_id;
    END;

END pkg_funds;
/