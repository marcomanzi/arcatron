CREATE TABLE call_data_records
(uuid uuid PRIMARY KEY,
 time_stamp TIMESTAMP,
 record_sequence_number VARCHAR(100),
 call_duration BIGINT,
 caller VARCHAR(30),
 receiver VARCHAR(30),
 nv VARCHAR(30),
 cid VARCHAR(30),
 id_service VARCHAR(30),
 caller_uuid uuid,
 price_uuid uuid,
 errors VARCHAR(300));
