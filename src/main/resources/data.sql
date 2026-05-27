INSERT IGNORE INTO roles (created_at, created_by, is_active, is_deleted, updated_at, updated_by, name) VALUES
   (now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_USER'),
   (now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_MANAGER'),
   (now(), 'SYSTEM', true, false, now(), 'SYSTEM', 'ROLE_ADMIN');

INSERT IGNORE INTO features (feature) VALUES
    ('CREATE_BOOK'),
    ('CREATE_USER'),
    ('CREAT_BORROW'),
    ('CREATE_ROLE'),
    ('UPDATE_BOOK'),
    ('UPDATE_USER'),
    ('UPDATE_BORROW'),
    ('UPDATE_ROLE'),
    ('DELETE_BOOK'),
    ('DELETE_USER'),
    ('DELETE_BORROW'),
    ('DELETE_ROLE');


