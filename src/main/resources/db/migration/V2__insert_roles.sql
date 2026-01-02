-- Insert roles
INSERT INTO role (id, name)
VALUES (uuid_generate_v4(), 'ROLE_ADMIN'),
       (uuid_generate_v4(), 'ROLE_USER');

-- Insert admin user (password = admin123)
INSERT INTO users (id, username, password_hash, role_id)
VALUES (uuid_generate_v4(),
        'admin',
        '$2a$10$4BSmBGElTYbIDlGdtznwL.jZmLIETeA1aB7ubkRWYkhOvdgRFHGge',
        (SELECT id FROM role WHERE name = 'ROLE_ADMIN'));