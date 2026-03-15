CREATE TABLE refund_requests(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    reason VARCHAR(150) NOT NULL,
    requested_at DATETIME NOT NULL,
    approved_at DATETIME NOT NULL,

    payment_id BIGINT NOT NULL,
    requested_by BIGINT NOT NULL,
    approved_by BIGINT NOT NULL,

    FOREIGN KEY (payment_id) REFERENCES payments(id),
    FOREIGN KEY (requested_by) REFERENCES users(id),
    FOREIGN KEY (approved_by) REFERENCES users(id)
)