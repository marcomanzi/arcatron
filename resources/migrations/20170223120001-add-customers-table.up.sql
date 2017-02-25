CREATE TABLE customers
(uuid uuid PRIMARY KEY,
 name VARCHAR(100),
 surname VARCHAR(100),
 fiscal_code VARCHAR(30),
 phone_number VARCHAR(30),
 invoices_payed BOOLEAN,
 profit_margin DECIMAL(10,2),
 address VARCHAR(300),
 city VARCHAR(30));
