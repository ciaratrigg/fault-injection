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

INSERT INTO fault (username, name, duration, scheduled_for, fault_type)
VALUES ('admin', 'test-node-restart', 60, 1620000000, 'node-restart');

INSERT INTO node_restart (f_id, num_nodes, frequency)
VALUES ((SELECT f_id FROM fault WHERE name = 'test-node-restart'), 3, 10);

INSERT INTO fault (username, name, duration, scheduled_for, fault_type)
VALUES ('admin', 'test-network-delay', 120, 1620001000, 'network-delay');

INSERT INTO network_delay (f_id, delay)
VALUES ((SELECT f_id FROM fault WHERE name = 'test-network-delay'), 500);

INSERT INTO fault (username, name, duration, scheduled_for, fault_type)
VALUES ('admin', 'test-node-crash', 180, 1620002000, 'node-crash');

INSERT INTO node_crash (f_id, num_nodes)
VALUES ((SELECT f_id FROM fault WHERE name = 'test-node-crash'), 2);