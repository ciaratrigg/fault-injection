\connect docker

INSERT INTO user_account (username, password, approved)
VALUES (
    'admin',
    '$2a$10$OuLyJj/ix79Oqkf9dwBfmu/PL0JeiwZvzp9M6geFNDcTawi4MsXku',
    true
);

INSERT INTO authority (u_id, role)
SELECT u_id, 'ROLE_ADMIN'
FROM user_account
WHERE username = 'admin';
