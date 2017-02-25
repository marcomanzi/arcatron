-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(id, first_name, last_name, email, pass)
VALUES (:id, :first_name, :last_name, :email, :pass)

-- :name update-user! :! :n
-- :doc update an existing user record
UPDATE users
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieve a user given the id.
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc delete a user given the id
DELETE FROM users
WHERE id = :id

/* Customers queries */

-- :name create-customer! :! :n
-- :doc creates a new customer record
INSERT INTO customers
(uuid, name,surname,fiscal_code,phone_number,invoices_payed,profit_margin,address,city)
VALUES (:uuid,:name,:surname,:fiscal_code,:phone_number,:invoices_payed,:profit_margin,:address, :city)

-- :name update-customer! :! :n
-- :doc update an existing customer record
UPDATE customers
SET name = :name, surname = :surname, fiscal_code = :fiscal_code,
phone_number = :phone_number, invoices_payed = :invoices_payed, profit_margin = :profit_margin,
address city = :address city
WHERE uuid = :uuid

-- :name get-customer :? :1
-- :doc retrieve a customer given the id.
SELECT * FROM customers
WHERE uuid = :uuid

-- :name get-customers :? :*
-- :doc retrieve paginated customers
SELECT * FROM customers c order by c.surname, c.name desc limit :limit offset :offset

-- :name delete-customer! :! :n
-- :doc delete a customer given the id
DELETE FROM customers
WHERE uuid = :uuid

-- :name delete-all-customers! :! :n
-- :doc delete all customers
DELETE FROM customers
