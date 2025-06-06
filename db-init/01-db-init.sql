
CREATE USER docker WITH PASSWORD 'pswd-docker';
CREATE DATABASE docker;
ALTER DATABASE docker OWNER TO docker;

\connect docker


CREATE TABLE IF NOT EXISTS user_account (
  u_id SERIAL PRIMARY KEY,
  username VARCHAR(20) UNIQUE,
  password VARCHAR(100),
  approved boolean DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS authority (
  u_id INT REFERENCES user_account(u_id) ON DELETE CASCADE,
  role VARCHAR(20),
  PRIMARY KEY (u_id, role)
);

CREATE TABLE IF NOT EXISTS fault (
  f_id SERIAL PRIMARY KEY,
  username VARCHAR(50) REFERENCES user_account(username) ON DELETE CASCADE,
  name VARCHAR(50) UNIQUE,
  duration INT,
  scheduled_for INT,
  fault_type VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS cpu_usage (
  f_id INT REFERENCES fault(f_id) ON DELETE CASCADE,
  num_threads INT,
  PRIMARY KEY (f_id)
);

CREATE TABLE IF NOT EXISTS node_restart (
  f_id INT REFERENCES fault(f_id) ON DELETE CASCADE,
  num_nodes INT,
  frequency INT,
  PRIMARY KEY (f_id)
);

CREATE TABLE IF NOT EXISTS bandwidth_throttle (
  f_id INT REFERENCES fault(f_id) ON DELETE CASCADE,
  rate INT,
  PRIMARY KEY (f_id)
);

CREATE TABLE IF NOT EXISTS network_delay (
  f_id INT REFERENCES fault(f_id) ON DELETE CASCADE,
  delay INT,
  PRIMARY KEY (f_id)
);

CREATE TABLE IF NOT EXISTS node_crash (
  f_id INT REFERENCES fault(f_id) ON DELETE CASCADE,
  num_nodes INT,
  PRIMARY KEY (f_id)
);

CREATE TABLE IF NOT EXISTS packet_loss (
  f_id INT REFERENCES fault(f_id) ON DELETE CASCADE,
  percent_loss INT,
  PRIMARY KEY (f_id)
);

ALTER TABLE user_account OWNER TO docker;
ALTER TABLE authority OWNER TO docker;
ALTER TABLE fault OWNER TO docker;
ALTER TABLE cpu_usage OWNER TO docker;
ALTER TABLE node_restart OWNER TO docker;
ALTER TABLE bandwidth_throttle OWNER TO docker;
ALTER TABLE network_delay OWNER TO docker;
ALTER TABLE node_crash OWNER TO docker;
ALTER TABLE packet_loss OWNER TO docker;
