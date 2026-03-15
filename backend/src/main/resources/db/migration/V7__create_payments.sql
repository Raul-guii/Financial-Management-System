CREATE TABLE payments(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    paymentMethod VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    paymentDate DATETIME NOT NULL,
    createdAt DATETIME NOT NULL,
    invoice_id BIGINT,
    gateway_transaction_id BIGINT,

    FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    FOREIGN KEY (gateway_transaction_id) REFERENCES gateway_transactions(id);
);