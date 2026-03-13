CREATE TABLE clients (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,

     name VARCHAR(150) NOT NULL,
     type VARCHAR(20) NOT NULL,

     document VARCHAR(20) NOT NULL,
     email VARCHAR(150),
     phone VARCHAR(20),

     address_street VARCHAR(150),
     address_number VARCHAR(20),
     address_neighborhood VARCHAR(100),
     address_city VARCHAR(100),
     address_state VARCHAR(50),
     address_postal_code VARCHAR(20),
     address_country VARCHAR(100),

     active BOOLEAN DEFAULT TRUE,

     created_by BIGINT,
     created_at DATETIME NOT NULL,
     updated_at DATETIME,

     CONSTRAINT fk_clients_user
         FOREIGN KEY (created_by)
             REFERENCES users(id)
);