#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER docker WITH PASSWORD 'pswd-docker';
  CREATE DATABASE docker;
  ALTER DATABASE docker OWNER TO docker;
  \c docker

  CREATE TABLE IF NOT EXISTS user_account (
    u_id SERIAL PRIMARY KEY,
    username VARCHAR(20) UNIQUE
  );

  CREATE TABLE IF NOT EXISTS authority (
    u_id INT REFERENCES user_account(u_id) ON DELETE CASCADE,
    role VARCHAR(20),
    PRIMARY KEY (u_id, role)
  );

  CREATE TABLE IF NOT EXISTS fault_scenario (
    f_id SERIAL PRIMARY KEY,
    u_id INT REFERENCES user_account(u_id) ON DELETE CASCADE,
    name VARCHAR(50),
    duration INT,
    scheduled_for INT,
    fault_type VARCHAR(50)
  );

  CREATE TABLE IF NOT EXISTS cpu_usage (
    f_id INT REFERENCES fault_scenario(f_id) ON DELETE CASCADE,
    num_threads INT,
    PRIMARY KEY (f_id)
  );

  CREATE TABLE IF NOT EXISTS node_restart (
    f_id INT REFERENCES fault_scenario(f_id) ON DELETE CASCADE,
    frequency INT,
    PRIMARY KEY (f_id)
  );

  CREATE TABLE IF NOT EXISTS corrupted_data (
    f_id INT REFERENCES fault_scenario(f_id) ON DELETE CASCADE,
    block_size INT,
    num_blocks INT,
    PRIMARY KEY (f_id)
  );

  CREATE TABLE IF NOT EXISTS memory_exhaustion (
    f_id INT REFERENCES fault_scenario(f_id) ON DELETE CASCADE,
    num_processes INT,
    memory INT,
    PRIMARY KEY (f_id)
  );

  CREATE TABLE IF NOT EXISTS bandwidth_throttle (
    f_id INT REFERENCES fault_scenario(f_id) ON DELETE CASCADE,
    rate INT,
    burst INT,
    latency INT,
    PRIMARY KEY (f_id)
  );

  CREATE TABLE IF NOT EXISTS network_latency (
    f_id INT REFERENCES fault_scenario(f_id) ON DELETE CASCADE,
    delay INT,
    delta INT,
    PRIMARY KEY (f_id)
  );

  CREATE TABLE IF NOT EXISTS volume_deletion (
    f_id INT REFERENCES fault_scenario(f_id) ON DELETE CASCADE,
    num_volumes INT,
    PRIMARY KEY (f_id)
  );

  CREATE TABLE IF NOT EXISTS node_crash (
    f_id INT REFERENCES fault_scenario(f_id) ON DELETE CASCADE,
    num_nodes INT,
    PRIMARY KEY (f_id)
  );

  CREATE TABLE IF NOT EXISTS packet_loss (
    f_id INT REFERENCES fault_scenario(f_id) ON DELETE CASCADE,
    percent_loss INT,
    PRIMARY KEY (f_id)
  );

  ALTER TABLE user_account OWNER TO docker;
  ALTER TABLE authority OWNER TO docker;
  ALTER TABLE fault_scenario OWNER TO docker;
  ALTER TABLE cpu_usage OWNER TO docker;
  ALTER TABLE node_restart OWNER TO docker;
  ALTER TABLE corrupted_data OWNER TO docker;
  ALTER TABLE memory_exhaustion OWNER TO docker;
  ALTER TABLE bandwidth_throttle OWNER TO docker;
  ALTER TABLE network_latency OWNER TO docker;
  ALTER TABLE volume_deletion OWNER TO docker;
  ALTER TABLE node_crash OWNER TO docker;
  ALTER TABLE packet_loss OWNER TO docker;

EOSQL
