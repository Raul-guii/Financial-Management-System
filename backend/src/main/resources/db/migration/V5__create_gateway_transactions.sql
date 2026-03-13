CREATE TABLE gateway_transactions(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    externalId VARCHAR(254) NOT NULL UNIQUE,
    gatewayName VARCHAR(254) NOT NULL,
    rawResponse VARCHAR(254) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,

    invoice_id BIGINT,

    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE;
);