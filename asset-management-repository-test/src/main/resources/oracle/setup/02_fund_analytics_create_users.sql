ALTER SESSION SET "_ORACLE_SCRIPT" = true;
CREATE USER fund_analytics IDENTIFIED BY P4$$word123;
GRANT CREATE SESSION, ALTER SESSION, CREATE DATABASE LINK, CREATE MATERIALIZED VIEW, CREATE PROCEDURE, CREATE PUBLIC SYNONYM, CREATE ROLE, CREATE SEQUENCE, CREATE SYNONYM, CREATE TABLE, CREATE TRIGGER, CREATE TYPE, CREATE VIEW, UNLIMITED TABLESPACE TO fund_analytics;