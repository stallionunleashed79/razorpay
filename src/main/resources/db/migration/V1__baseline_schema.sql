-- Baseline schema generated from current JPA entities.
-- This migration is treated as the baseline for existing databases
-- (see spring.flyway.baseline-on-migrate / baseline-version in application.yaml),
-- and is the full schema for brand new databases.

-- ============ merchant ============
CREATE TABLE IF NOT EXISTS merchant (
    id                                  UUID PRIMARY KEY,
    name                                VARCHAR(200) NOT NULL,
    email                               VARCHAR(255) NOT NULL UNIQUE,
    contact_number                      VARCHAR(20),
    business_type                       VARCHAR(50),
    business_name                       VARCHAR(50),
    website_url                         VARCHAR(200),
    status                              VARCHAR(20) NOT NULL,
    gst_id                              VARCHAR(20),
    pan_id                              VARCHAR(20),
    settlement_bank_account             VARCHAR(20),
    settlement_bank_ifsc                VARCHAR(20),
    settlement_bank_account_holder_name VARCHAR(200),
    created_by                          VARCHAR(255),
    created_at                          TIMESTAMP,
    updated_by                          VARCHAR(255),
    updated_at                          TIMESTAMP
);

-- ============ app_user ============
CREATE TABLE IF NOT EXISTS app_user (
    id            UUID PRIMARY KEY,
    marchant_id   UUID NOT NULL REFERENCES merchant (id),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(255) NOT NULL,
    created_by    VARCHAR(255),
    created_at    TIMESTAMP,
    updated_by    VARCHAR(255),
    updated_at    TIMESTAMP
);

-- ============ customer ============
CREATE TABLE IF NOT EXISTS customer (
    id             UUID PRIMARY KEY,
    marchant_id    UUID NOT NULL REFERENCES merchant (id),
    name           VARCHAR(200) NOT NULL,
    email          VARCHAR(200) NOT NULL,
    contact_number VARCHAR(20),
    deleted_at     TIMESTAMP,
    created_by     VARCHAR(255),
    created_at     TIMESTAMP,
    updated_by     VARCHAR(255),
    updated_at     TIMESTAMP
);

-- ============ merchant_webhook_config ============
CREATE TABLE IF NOT EXISTS merchant_webhook_config (
    id                 UUID PRIMARY KEY,
    merchant_id        UUID NOT NULL REFERENCES merchant (id),
    target_url         VARCHAR(500) NOT NULL,
    webhook_secret_hash VARCHAR(255) NOT NULL,
    enabled            BOOLEAN NOT NULL,
    event_types        VARCHAR(255)
);

-- ============ api_key ============
CREATE TABLE IF NOT EXISTS api_key (
    id                        UUID PRIMARY KEY,
    marchant_id               UUID NOT NULL REFERENCES merchant (id),
    key_id                    VARCHAR(50) NOT NULL UNIQUE,
    previous_key_secret_hash  VARCHAR(200) UNIQUE,
    key_secret_hash           VARCHAR(200) NOT NULL UNIQUE,
    environment               VARCHAR(50),
    enabled                   BOOLEAN NOT NULL,
    last_used_at              TIMESTAMP,
    rotated_at                TIMESTAMP,
    grace_period_expires_at   TIMESTAMP,
    created_by                VARCHAR(255),
    created_at                TIMESTAMP,
    updated_by                VARCHAR(255),
    updated_at                TIMESTAMP
);

-- ============ order_record ============
CREATE TABLE IF NOT EXISTS order_record (
    id            UUID PRIMARY KEY,
    merchant_id   UUID NOT NULL, -- no FK: cross service boundary
    amount_units  INTEGER NOT NULL,
    currency      VARCHAR(10) NOT NULL,
    order_status  VARCHAR(255),
    attempts      INTEGER NOT NULL,
    notes         JSONB,
    expires_at    TIMESTAMP NOT NULL
);

-- ============ payment ============
CREATE TABLE IF NOT EXISTS payment (
    id                UUID PRIMARY KEY,
    amount_units      INTEGER NOT NULL,
    currency          VARCHAR(10) NOT NULL,
    order_id          UUID NOT NULL REFERENCES order_record (id),
    merchant_id       UUID NOT NULL,
    status            VARCHAR(30) NOT NULL,
    bank_reference    VARCHAR(255),
    payment_method    VARCHAR(30) NOT NULL,
    idempotency_key   VARCHAR(255),
    method_details    JSONB,
    error_code        VARCHAR(255) NOT NULL,
    error_description VARCHAR(255) NOT NULL,
    authorized_at     TIMESTAMP,
    captured_at       TIMESTAMP,
    failed_at         TIMESTAMP,
    refunded_at       TIMESTAMP,
    settled_at        TIMESTAMP
);

-- ============ payment_transition_log ============
CREATE TABLE IF NOT EXISTS payment_transition_log (
    id          UUID PRIMARY KEY,
    payment_id  UUID NOT NULL REFERENCES payment (id),
    event       VARCHAR(30) NOT NULL,
    from_status VARCHAR(30) NOT NULL,
    to_status   VARCHAR(30) NOT NULL,
    actor       VARCHAR(30) NOT NULL,
    occured_at  TIMESTAMP NOT NULL
);

-- ============ refund ============
CREATE TABLE IF NOT EXISTS refund (
    id                UUID PRIMARY KEY,
    payment_id        UUID NOT NULL REFERENCES payment (id),
    merchant_id       UUID NOT NULL,
    amount_units      INTEGER,
    currency          VARCHAR(10),
    refund_status     VARCHAR(30) NOT NULL,
    bank_reference    VARCHAR(100),
    error_code        VARCHAR(100),
    error_description VARCHAR(500),
    notes             JSONB,
    processed_at      TIMESTAMP
);

-- ============ settlement ============
CREATE TABLE IF NOT EXISTS settlement (
    id                    UUID PRIMARY KEY,
    merchant_id           UUID NOT NULL, -- no FK: cross service boundary
    gross_amount_units    INTEGER NOT NULL,
    gross_amount_currency VARCHAR(10) NOT NULL,
    refund_amount_units   INTEGER NOT NULL,
    refund_amount_currency VARCHAR(10) NOT NULL,
    fee_amount_units      INTEGER NOT NULL,
    fee_amount_currency   VARCHAR(10) NOT NULL,
    gst_amount_units      INTEGER NOT NULL,
    gst_amount_currency   VARCHAR(10) NOT NULL,
    net_amount_units      INTEGER NOT NULL,
    net_amount_currency   VARCHAR(10) NOT NULL,
    settlement_status     VARCHAR(30) NOT NULL,
    bank_reference        VARCHAR(30) NOT NULL,
    processed_at          TIMESTAMP
);

-- ============ settlement_payment ============
CREATE TABLE IF NOT EXISTS settlement_payment (
    settlement_id UUID NOT NULL REFERENCES settlement (id),
    payment_id    UUID NOT NULL,
    PRIMARY KEY (settlement_id, payment_id)
);

-- ============ webhook_event ============
CREATE TABLE IF NOT EXISTS webhook_event (
    id                  UUID PRIMARY KEY,
    merchant_id         UUID NOT NULL,
    event_type          VARCHAR(100) NOT NULL,
    payload             JSONB,
    target_url          VARCHAR(255) NOT NULL,
    signature           VARCHAR(255) NOT NULL,
    webhook_event_status VARCHAR(30) NOT NULL,
    attempts            INTEGER NOT NULL,
    next_retry_at       TIMESTAMP,
    last_attempt_at     TIMESTAMP,
    last_response_code  INTEGER,
    last_response_body  VARCHAR(1000) NOT NULL,
    delivered_at        TIMESTAMP
);

-- ============ dlq_event ============
CREATE TABLE IF NOT EXISTS dlq_event (
    id              UUID PRIMARY KEY,
    merchant_id     UUID NOT NULL,
    webhook_event_id UUID NOT NULL UNIQUE REFERENCES webhook_event (id),
    final_error     UUID NOT NULL,
    payload         JSONB,
    moved_at        TIMESTAMP,
    replayed_at     TIMESTAMP
);

-- ============ vault_card ============
CREATE TABLE IF NOT EXISTS vault_card (
    id               UUID PRIMARY KEY,
    last_four        VARCHAR(4) NOT NULL,
    bin              VARCHAR(6) NOT NULL,
    encrypted_pan    BYTEA NOT NULL,
    encrypted_dek    BYTEA NOT NULL,
    brand            VARCHAR(255) NOT NULL,
    expiry_month     INTEGER NOT NULL,
    expiry_year      INTEGER NOT NULL,
    card_holder_name VARCHAR(255) NOT NULL,
    deleted_at       TIMESTAMP
);

-- ============ card_token ============
CREATE TABLE IF NOT EXISTS card_token (
    id            UUID PRIMARY KEY,
    token         VARCHAR(50) NOT NULL UNIQUE,
    vault_card_id UUID NOT NULL REFERENCES vault_card (id),
    customer      UUID NOT NULL,
    merchant      UUID NOT NULL,
    revoked_at    TIMESTAMP
);

