CREATE TABLE audit_logs(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    action VARCHAR(254) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    time_stamp DATETIME NOT NULL,

    user_id BIGINT NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id);
)