ALTER TABLE audit_logs
    ADD COLUMN user_name VARCHAR(150),
    ADD COLUMN description VARCHAR(500);