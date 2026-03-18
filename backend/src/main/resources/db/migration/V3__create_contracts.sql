CREATE TABLE contracts(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    billing_period VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date   DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,
    created_by BIGINT,
    client_id BIGINT,

    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (client_id) REFERENCES clients(id)
);