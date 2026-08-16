-- Adds the receipt column (added to OrderRecord entity after baseline) and
-- backfills/defaults numeric "attempts"/status columns at the DB level so that
-- inserts which omit these fields (e.g. via Lombok @Builder without
-- @Builder.Default) don't violate NOT NULL constraints.

ALTER TABLE order_record
    ADD COLUMN IF NOT EXISTS receipt VARCHAR(100);

ALTER TABLE order_record
    ALTER COLUMN attempts SET DEFAULT 0;
UPDATE order_record SET attempts = 0 WHERE attempts IS NULL;

ALTER TABLE webhook_event
    ALTER COLUMN attempts SET DEFAULT 0;
UPDATE webhook_event SET attempts = 0 WHERE attempts IS NULL;

ALTER TABLE merchant
    ALTER COLUMN status SET DEFAULT 'PENDING_KYC';
UPDATE merchant SET status = 'PENDING_KYC' WHERE status IS NULL;

ALTER TABLE refund
    ALTER COLUMN refund_status SET DEFAULT 'PENDING';
UPDATE refund SET refund_status = 'PENDING' WHERE refund_status IS NULL;

