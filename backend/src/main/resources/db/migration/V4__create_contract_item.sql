CREATE TABLE contract_items(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(254),
    quantity DECIMAL(15, 2) NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL,

    contract_id BIGINT INT NOT NULL,
    billing_period_id BIGINT NOT NULL,

    FOREIGN KEY (contract_id) REFERENCES contracts(id) DELETE ON CASCADE,
    FOREIGN KEY (billing_period_id) REFERENCES billing_period(id);