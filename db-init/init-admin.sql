INSERT INTO user_account (username, password)
VALUES (
    'admin',
    '$2a$12$KJUQ5KTbYPYAgmA3uqkaNelYbKyuKL5IVSM7hqjfS4oNBhCfGep0y'
);

INSERT INTO authority (u_id, role)
VALUES (
    (SELECT u_id FROM user_account WHERE username = 'admin'),
    'ROLE_ADMIN'
);
