CREATE TABLE reconciliations(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    executed_at DATETIME NOT NULL,
    total_in DECIMAL(19,2) NOT NULL DEFAULT 0,
    total_out DECIMAL(19,2) NOT NULL DEFAULT 0,
    net_balance DECIMAL(19,2) NOT NULL DEFAULT 0,

    executed_by BIGINT,

    FOREIGN KEY (executed_by) REFERENCES users(id)
);