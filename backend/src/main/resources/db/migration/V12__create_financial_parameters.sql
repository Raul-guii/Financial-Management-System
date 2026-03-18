CREATE TABLE financial_parameters(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    value VARCHAR(254) NOT NULL,
    type VARCHAR(30) NOT NULL,
    description VARCHAR(254) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,

    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,

    FOREIGN KEY (created_by) REFERENCES users(id),
    FOREIGN KEY (updated_by) REFERENCES users(id)
);