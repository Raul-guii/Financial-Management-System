CREATE TABLE contracts(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    billingPeriod VARCHAR(20) NOT NULL,
    startDate DATE NOT NULL,
    endDate   DATE NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedBy DATETIME NOT NULL,
    created_by BIGINT,
    client_id BIGINT,

    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (client_id) REFERENCES clients(id);
);