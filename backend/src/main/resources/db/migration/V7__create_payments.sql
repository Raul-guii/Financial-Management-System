CREATE TABLE payments(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_method VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    payment_date DATETIME NOT NULL,
    payment_status VARCHAR(30) NOT NULL,
    payer_email VARCHAR(255),
    date_of_expiration DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,

    invoice_id BIGINT,

    FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);