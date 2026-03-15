CREATE TABLE clients
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    name                 VARCHAR(150)          NOT NULL,
    type                 VARCHAR(20)           NOT NULL,
    document             VARCHAR(20)           NOT NULL,
    email                VARCHAR(150)          NULL,
    phone                VARCHAR(20)           NULL,
    address_street       VARCHAR(150)          NULL,
    address_number       VARCHAR(20)           NULL,
    address_neighborhood VARCHAR(100)          NULL,
    address_city         VARCHAR(100)          NULL,
    address_state        VARCHAR(50)           NULL,
    address_postal_code  VARCHAR(20)           NULL,
    address_country      VARCHAR(100)          NULL,
    active               BIT(1)                NULL,
    created_by           BIGINT                NULL,
    created_at           datetime              NOT NULL,
    updated_at           datetime              NULL,
    CONSTRAINT pk_clients PRIMARY KEY (id)
);

CREATE TABLE contract_items
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    name          VARCHAR(100)          NOT NULL,
    `description` VARCHAR(254)          NULL,
    quantity      DECIMAL               NOT NULL,
    unit_price    DECIMAL               NOT NULL,
    active        BIT(1)                NULL,
    created_at    datetime              NOT NULL,
    contract_id   BIGINT                NOT NULL,
    CONSTRAINT pk_contract_items PRIMARY KEY (id)
);

CREATE TABLE contracts
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    status         VARCHAR(20)           NOT NULL,
    billing_period VARCHAR(20)           NOT NULL,
    start_date     date                  NULL,
    end_date       date                  NULL,
    created_by     BIGINT                NULL,
    client_id      BIGINT                NULL,
    CONSTRAINT pk_contracts PRIMARY KEY (id)
);

CREATE TABLE gateway_transactions
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    invoice_status VARCHAR(20)           NOT NULL,
    external_id    VARCHAR(254)          NOT NULL,
    gateway_name   VARCHAR(254)          NOT NULL,
    amount         DECIMAL               NOT NULL,
    raw_response   VARCHAR(254)          NOT NULL,
    created_at     datetime              NOT NULL,
    updated_at     datetime              NOT NULL,
    invoice_id     BIGINT                NULL,
    payment_id     BIGINT                NOT NULL,
    CONSTRAINT pk_gateway_transactions PRIMARY KEY (id)
);

CREATE TABLE invoices
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    status           VARCHAR(20)           NOT NULL,
    issue_date       date                  NOT NULL,
    due_day          date                  NOT NULL,
    amount           DECIMAL               NOT NULL,
    late_free_amount DECIMAL               NOT NULL,
    interest_amount  DECIMAL               NOT NULL,
    created_at       datetime              NOT NULL,
    updated_at       datetime              NOT NULL,
    contract_id      BIGINT                NULL,
    CONSTRAINT pk_invoices PRIMARY KEY (id)
);

CREATE TABLE payments
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    amount       DECIMAL               NOT NULL,
    payment_date datetime              NOT NULL,
    created_at   datetime              NOT NULL,
    invoice_id   BIGINT                NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    user_id BIGINT       NOT NULL,
    roles   VARCHAR(255) NULL
);

CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    name     VARCHAR(100)          NOT NULL,
    email    VARCHAR(150)          NOT NULL,
    password VARCHAR(254)          NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE gateway_transactions
    ADD CONSTRAINT uc_gateway_transactions_externalid UNIQUE (external_id);

ALTER TABLE gateway_transactions
    ADD CONSTRAINT uc_gateway_transactions_payment UNIQUE (payment_id);

ALTER TABLE clients
    ADD CONSTRAINT FK_CLIENTS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE contracts
    ADD CONSTRAINT FK_CONTRACTS_ON_CLIENT FOREIGN KEY (client_id) REFERENCES clients (id);

ALTER TABLE contracts
    ADD CONSTRAINT FK_CONTRACTS_ON_CREATED_BY FOREIGN KEY (created_by) REFERENCES users (id);

ALTER TABLE contract_items
    ADD CONSTRAINT FK_CONTRACT_ITEMS_ON_CONTRACT FOREIGN KEY (contract_id) REFERENCES contracts (id);

ALTER TABLE gateway_transactions
    ADD CONSTRAINT FK_GATEWAY_TRANSACTIONS_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoices (id);

ALTER TABLE gateway_transactions
    ADD CONSTRAINT FK_GATEWAY_TRANSACTIONS_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES payments (id);

ALTER TABLE invoices
    ADD CONSTRAINT FK_INVOICES_ON_CONTRACT FOREIGN KEY (contract_id) REFERENCES contracts (id);

ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoices (id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_user_roles_on_user FOREIGN KEY (user_id) REFERENCES users (id);