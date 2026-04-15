ALTER TABLE invoices
    ADD COLUMN original_amount DECIMAL(15,2);

UPDATE invoices SET original_amount = amount;