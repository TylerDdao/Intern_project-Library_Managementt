INSERT IGNORE INTO roles (id, created_at, created_by, is_active, is_deleted, updated_at, updated_by, name) VALUES
   (1, now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_USER'),
   (2, now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_MANAGER'),
   (3, now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_ADMIN');

INSERT IGNORE INTO users(id, full_name, username, address, email, phone_number, password, role_id, created_at) VALUES
    (1, 'Tyler Dao', 'tyler', '32 Noecker Street', 'tyler@gmail.com', '5483843681', '$2a$10$SWzx7vnpEzMZlj6F1tGrSOcwWclGU2lS0FKSck2eyz16V0gi3A3rm', '3', now());

INSERT IGNORE INTO features (id, name) VALUES
    (1, 'CREATE_BOOK'),
    (2, 'CREATE_USER'),
    (3, 'CREAT_BORROW'),
    (4, 'CREATE_ROLE'),
    (5, 'CREATE_POST'),
    (6, 'CREATE_COMMENT'),

    (7, 'GET_BOOK'),
    (8, 'GET_USER'),
    (9, 'GET_BORROW'),
    (10, 'GET_ROLE'),
    (11, 'GET_POST'),
    (12, 'GET_COMMENT'),

    (13, 'UPDATE_BOOK'),
    (14, 'UPDATE_USER'),
    (15, 'UPDATE_BORROW'),
    (16, 'UPDATE_ROLE'),
    (17, 'UPDATE_POST'),
    (18, 'UPDATE_COMMENT'),

    (19, 'DELETE_BOOK'),
    (20, 'DELETE_USER'),
    (21, 'DELETE_BORROW'),
    (22, 'DELETE_ROLE'),
    (23, 'DELETE_POST'),
    (24, 'DELETE_COMMENT'),
    
    (25, 'ASSIGN_FEATURE'),
    (26, 'UNASSIGN_FEATURE');


INSERT IGNORE INTO features_roles (role_id, feature_id) VALUES
    (1, 2),
    (1, 3),
    (1, 5),
    (1, 6),
    (1, 7),
    (1, 8),
    (1, 9),
    (1, 10),
    (1, 11),
    (1, 12),
    (1, 14),
    (1, 17),
    (1, 18),
    (1, 23),
    (1, 24);