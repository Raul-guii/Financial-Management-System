CREATE TABLE reconciliation_items(
    id BIGINT AUTO_INCREMENT PRIMARY_KEY,
    system_amount DECIMAL(15, 2),
    gateway_amount DECIMAL(15, 2),

    reconciliation_id BIGINT,
    invoice_id BIGINT,
    payment_id BIGINT,
    gateway_transaction_id BIGINT,

    FOREIGN KEY (reconciliation_id) REFERENCES reconciliations(id) DELETE ON CASCADE,
    FOREIGN KEY (invoice_id) REFERENCES invoices(id),
    FOREIGN KEY (payment_id) REFERENCES payments(id),
    FOREIGN KEY (gateway_transaction_id) REFERENCES gateway_transactions(id);

)