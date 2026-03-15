CREATE TABLE financial_parameters(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    key VARCHAR(100) UNIQUE NOT NULL,
    value VARCHAR(254) NOT NULL,
    type VARCHAR(30) NOT NULL,
    description VARCHAR(254) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    updated_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,

    updated_by BIGINT NOT NULL,

    FOREIGN KEY (updated_by) REFERENCES users(id);
)