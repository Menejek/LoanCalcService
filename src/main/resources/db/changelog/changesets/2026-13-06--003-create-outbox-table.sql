--liquibase formatted sql

--changeset menejek:3
CREATE TABLE outbox (
                    id BIGSERIAL PRIMARY KEY,
                    aggregate_id UUID NOT NULL,
                    payload JSONB NOT NULL,
                    event_type VARCHAR(255) NOT NULL,
                    status VARCHAR(255) CHECK (status IN(
                                                        'PENDING',
                                                        'SENT',
                                                        'FAILED'

                    )),
                    created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP
                );

CREATE INDEX idx_outbox_status ON outbox (status);

--rollback DROP TABLE outbox