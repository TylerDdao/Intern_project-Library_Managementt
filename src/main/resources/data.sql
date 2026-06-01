INSERT IGNORE INTO roles (id, created_at, created_by, is_active, is_deleted, updated_at, updated_by, name) VALUES
   (1, now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_USER'),
   (2, now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_MANAGER'),
   (3, now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_ADMIN');

INSERT IGNORE INTO features (id, name) VALUES
    (1, 'CREATE_BOOK'),
    (2, 'CREATE_USER'),
    (3, 'CREAT_BORROW'),
    (4, 'CREATE_ROLE'),
    (5, 'UPDATE_BOOK'),
    (6, 'UPDATE_USER'),
    (7, 'UPDATE_BORROW'),
    (8, 'UPDATE_ROLE'),
    (9, 'DELETE_BOOK'),
    (10, 'DELETE_USER'),
    (11, 'DELETE_BORROW'),
    (12, 'DELETE_ROLE');