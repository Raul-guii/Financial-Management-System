CREATE TABLE invoice_lines(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(254),
    quantity DECIMAL(15, 2) NOT NULL,
    unit_price DECIMAL(15, 2) NOT NULL,
    line_total DECIMAL(15, 2) NOT NULL,
    created_at DATETIME NOT NULL,

    contract_item_id BIGINT NOT NULL,
    invoice_id BIGINT NOT NULL,

    FOREIGN KEY (contract_item_id) REFERENCES contract_items(id),
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE;
)