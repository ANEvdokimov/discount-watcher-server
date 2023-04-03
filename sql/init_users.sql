CREATE SCHEMA IF NOT EXISTS discount_watcher_schema;

DROP OWNED BY discount_watcher_users;
DROP ROLE IF EXISTS discount_watcher_users;
CREATE ROLE discount_watcher_users;

DROP OWNED BY discount_watcher_server;
DROP USER IF EXISTS discount_watcher_server;
CREATE USER discount_watcher_server PASSWORD 'secret-password' IN ROLE discount_watcher_users;

GRANT USAGE
    ON SCHEMA discount_watcher_schema
    TO discount_watcher_users;

GRANT SELECT, UPDATE, INSERT, DELETE
    ON ALL TABLES IN SCHEMA discount_watcher_schema
    TO discount_watcher_users;

GRANT USAGE
    ON ALL SEQUENCES IN SCHEMA discount_watcher_schema
    TO discount_watcher_users;



CREATE SCHEMA IF NOT EXISTS discount_watcher_schema_test;

DROP OWNED BY discount_watcher_users_test;
DROP ROLE IF EXISTS discount_watcher_users_test;
CREATE ROLE discount_watcher_users_test;

DROP OWNED BY discount_watcher_server_test;
DROP USER IF EXISTS discount_watcher_server_test;
CREATE USER discount_watcher_server_test PASSWORD 'password-for-tests' IN ROLE discount_watcher_users_test;

GRANT ALL
    ON SCHEMA discount_watcher_schema_test
    TO discount_watcher_users_test;

GRANT ALL
    ON ALL TABLES IN SCHEMA discount_watcher_schema_test
    TO discount_watcher_users_test;

GRANT ALL
    ON ALL SEQUENCES IN SCHEMA discount_watcher_schema_test
    TO discount_watcher_users_test;
