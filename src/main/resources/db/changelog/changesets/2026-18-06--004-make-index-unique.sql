--liquibase formatted sql

--changeset menejek:4
DROP INDEX if EXISTS idx_idempotency_key;

CREATE UNIQUE INDEX idx_idempotency_key ON loan_application (idempotency_key);