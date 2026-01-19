-- Legacy payments ETL support columns and constraints.
ALTER TABLE payments
    ADD COLUMN legacy_source_id INT NULL,
    ADD COLUMN legacy_payment_id BIGINT NULL;

ALTER TABLE payments
    ADD CONSTRAINT uq_payments_legacy_source_payment
        UNIQUE (legacy_source_id, legacy_payment_id);
