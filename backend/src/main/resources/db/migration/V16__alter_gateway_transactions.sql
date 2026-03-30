ALTER TABLE gateway_transactions
    ADD COLUMN qr_code TEXT;

ALTER TABLE gateway_transactions
    ADD COLUMN ticket_url VARCHAR(255);