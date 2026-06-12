--liquibase formatted sql

--changeset menejek:2
CREATE INDEX idx_application_id ON loan_application (application_id);

CREATE INDEX idx_idempotency_key ON loan_application (idempotency_key);