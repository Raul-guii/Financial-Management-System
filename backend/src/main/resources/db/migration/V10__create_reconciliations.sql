CREATE TABLE reconciliation(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    executed_at DATETIME NOT NULL,

    executed_by BIGINT,

    FOREIGN KEY (executed_by) REFERENCES users(id);
)