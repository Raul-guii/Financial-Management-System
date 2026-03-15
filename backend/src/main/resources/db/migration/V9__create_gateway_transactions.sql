CREATE TABLE gateway_transactions(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    external_id VARCHAR(254) NOT NULL UNIQUE,
    gateway_name VARCHAR(254) NOT NULL,
    raw_response VARCHAR(254) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,

    invoice_id BIGINT,

    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE;
);