CREATE TABLE prices
(uuid uuid PRIMARY KEY,
 destination VARCHAR(100),
 prefix VARCHAR(30),
 price_per_minute VARCHAR(30),
 created_on TIMESTAMP);
