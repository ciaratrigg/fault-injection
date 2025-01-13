#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
  CREATE USER docker WITH PASSWORD 'pswd-docker;
  CREATE DATABASE docker;
  ALTER DATABASE docker owner to docker;
  \c docker

  CREATE TABLE IF NOT EXISTS user-account (
    u_id SERIAL UNIQUE,
    username UNIQUE,
    PRIMARY KEY (u_id, username)
  );

  CREATE TABLE IF NOT EXISTS authority (
    u_id SERIAL REFERENCES user-account(u_id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL,
    PRIMARY KEY (u_id, role)
  );




EOSQL