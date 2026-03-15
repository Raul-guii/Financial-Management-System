CREATE TABLE gateway_transactions(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    external_id VARCHAR(254) NOT NULL UNIQUE,
    gateway_name VARCHAR(254) NOT NULL,
    raw_response VARCHAR(254) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATED CURRENT_TIMESTAMP,

    gateway_transaction_id BIGINT,

    FOREIGN KEY (gateway_transaction_id) REFERENCES gateway_transactions(id) ON DELETE CASCADE;
);