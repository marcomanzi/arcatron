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

-- :name count-customers :? :n
-- :doc retrieve the count of customers
SELECT count(*) FROM customers

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

-- :name find-by-phone-number :? :1
-- :doc Fine the customer with phone number in input
SELECT * FROM customers
WHERE phone_number = :phone_number

-- :name create-price! :! :n
-- :doc creates a new price record
INSERT INTO prices
(uuid, destination, prefix, price_per_minute, created_on)
VALUES (:uuid, :destination, :prefix, :price_per_minute, :created_on)

-- :name update-price! :! :n
-- :doc update an existing price record
UPDATE prices
SET destination = :destination, prefix= :prefix, price_per_minute = :price_per_minute
WHERE uuid = :uuid

-- :name get-price :? :1
-- :doc retrieve a customer given the id.
SELECT * FROM prices
WHERE uuid = :uuid

-- :name count-prices :? :n
-- :doc retrieve count of prices
SELECT count(*) FROM prices

-- :name get-prices :? :*
-- :doc retrieve paginated prices
SELECT * FROM prices p order by p.created_on desc limit :limit offset :offset

-- :name get-price-by-prefix :? :*
-- :doc retrieve paginated customers
SELECT * FROM prices p where p.prefix = :prefix

-- :name delete-price! :! :n
-- :doc delete a customer given the id
DELETE FROM prices
WHERE uuid = :uuid

-- :name delete-all-prices! :! :n
-- :doc delete all customers
DELETE FROM prices

-- :name create-cdr! :! :n
-- :doc creates a new price record
INSERT INTO call_data_records
(uuid, time_stamp, record_sequence_number, call_duration, caller, receiver, nv, cid, id_service, caller_uuid, price_uuid, errors)
VALUES (:uuid, :time_stamp, :record_sequence_number, :call_duration, :caller, :receiver, :nv, :cid, :id_service, :caller_uuid, :price_uuid, :errors)

-- :name get-all-cdrs :? :*
-- :doc retrieve all cdrs in the system
SELECT * FROM call_data_records

-- :name delete-cdr! :! :n
-- :doc delete a cdr given the id
DELETE FROM call_data_records
WHERE uuid = :uuid

-- :name delete-all-cdrs! :! :n
-- :doc delete all call data records
DELETE FROM call_data_records
