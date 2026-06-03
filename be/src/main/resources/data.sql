INSERT IGNORE INTO roles (id, created_at, created_by, is_active, is_deleted, updated_at, updated_by, name) VALUES
   (1, now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_ROOT'),
   (2, now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_ADMIN'),
   (3, now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_MANAGER'),
   (4, now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_USER');

INSERT IGNORE INTO users(id, full_name, username, address, email, phone_number, password, role_id, created_at) VALUES
    (1, 'Tyler Dao', 'tyler', '32 Noecker Street', 'tyler@gmail.com', '5483843681', '$2a$10$SWzx7vnpEzMZlj6F1tGrSOcwWclGU2lS0FKSck2eyz16V0gi3A3rm', '1', now());

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
    (4, 2),
    (4, 3),
    (4, 5),
    (4, 6),
    (4, 7),
    (4, 8),
    (4, 9),
    (4, 10),
    (4, 11),
    (4, 12),
    (4, 14),
    (4, 17),
    (4, 18),
    (4, 23),
    (4, 24);