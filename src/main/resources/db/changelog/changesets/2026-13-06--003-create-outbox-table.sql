--liquibase formatted sql

--changeset menejek:3
CREATE TABLE outbox (
                    id BIGSERIAL PRIMARY KEY,
                    aggregate_id UUID NOT NULL,
                    payload JSONB NOT NULL,
                    event_type VARCHAR(255) NOT NULL,
                    status VARCHAR(255) CHECK (status IN(
                                                        'UNDER_REVIEW',
                                                        'APPROVED',
                                                        'REJECTED',
                                                        'DOCUMENTS_PENDING',
                                                        'NEEDS_MANUAL_REVIEW'

                    )),
                    crated_at TIMESTAMP(6) NOT NULL
                );