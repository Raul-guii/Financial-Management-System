CREATE TABLE invoices(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    issueDate DATE NOT NULL,
    dueDay DATE NOT NULL,
    lateFreeAmount DECIMAL(15, 2) NOT NULL,
    interestAmount DECIMAL(15, 2) NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NOT NULL,

    contract_id BIGINT,

    FOREIGN KEY (contract_id) REFERENCES contracts(id);
);
