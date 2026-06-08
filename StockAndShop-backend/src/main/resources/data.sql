-- Rôles
INSERT INTO role (id, name, created_at, updated_at) VALUES
    (1, 'ADMIN', NOW(), NOW()),
    (2, 'USER',  NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Utilisateurs (password = "password" encodé BCrypt)
INSERT INTO users (id, username, email, password, created_at, updated_at) VALUES
    ('11111111-1111-1111-1111-111111111111', 'hugo',  'hugo@test.com',  'test', NOW(), NOW()),
    ('22222222-2222-2222-2222-222222222222', 'alice', 'alice@test.com', 'test', NOW(), NOW()),
    ('33333333-3333-3333-3333-333333333333', 'bob',   'bob@test.com',   'test', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Rôles des utilisateurs (table de jointure @ManyToMany)
INSERT INTO users_roles (user_id, role_id) VALUES
    ('11111111-1111-1111-1111-111111111111', 1),
    ('11111111-1111-1111-1111-111111111111', 2),
    ('22222222-2222-2222-2222-222222222222', 2),
    ('33333333-3333-3333-3333-333333333333', 2)
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Catégories
INSERT INTO category (id, name, description, created_at, updated_at) VALUES
    (1, 'autres',          'Autres', NOW(), NOW()),
    (2, 'épicerie',          'Pâtes, riz, farine...', NOW(), NOW()),
    (3, 'conserves',         'Sauces, bocaux...', NOW(), NOW()),
    (4, 'boissons',          'Eau, jus, sodas...', NOW(), NOW()),
    (5, 'boulangerie',          'Pain, viennoiseries, baguette, ...', NOW(), NOW()),
    (6, 'produits laitiers', 'Lait, yaourts, fromages...', NOW(), NOW())


ON CONFLICT (id) DO NOTHING;

-- Produits
INSERT INTO product (id, name, unity, category_id, created_at, updated_at) VALUES
    (1, 'lait',          'MILLILITER',     1, NOW(), NOW()),
    (2, 'farine',        'GRAMS', 2, NOW(), NOW()),
    (3, 'yaourt nature', 'PACKET',       1, NOW(), NOW()),
    (4, 'jus d''orange', 'MILLILITER',     4, NOW(), NOW()),
    (5, 'pâtes',         'PACKET',    2, NOW(), NOW()),
    (6, 'riz',           'GRAMS', 2, NOW(), NOW()),
    (7, 'sauce tomate',  'JAR',       3, NOW(), NOW()),
    (8, 'eau minérale',  'BOTTLE',    4, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Maisons
INSERT INTO home (id, name, description, created_at, updated_at) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Maison Hugo', 'La maison principale', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Membres des maisons
INSERT INTO user_home (id, user_id, home_role, home_id, created_at, updated_at) VALUES
    (1, '11111111-1111-1111-1111-111111111111', 'OWNER',  'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW()),
    (2, '22222222-2222-2222-2222-222222222222', 'USER',   'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW()),
    (3, '33333333-3333-3333-3333-333333333333', 'VIEWER', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Stock dans les maisons
INSERT INTO product_stock_home (id, product_id, quantity, home_id, created_at, updated_at) VALUES
    (1, 1, 3, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW()),
    (2, 2, 1, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW()),
    (3, 3, 6, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW()),
    (4, 5, 2, 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Listes de courses
INSERT INTO shopping_list (id, name, description, home_id, created_at, updated_at) VALUES
    (1, 'Courses du week-end', 'Courses de samedi matin', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW()),
    (2, 'Courses urgentes',    NULL,                       'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Articles dans les listes
INSERT INTO product_list_item (id, product_id, quantity, is_checked, shopping_list_id, created_at, updated_at) VALUES
    (1, 1, 4, false, 1, NOW(), NOW()),
    (2, 4, 2, false, 1, NOW(), NOW()),
    (3, 5, 3, true,  1, NOW(), NOW()),
    (4, 2, 2, false, 2, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Réinitialisation des séquences : toujours après le max réel de chaque table
SELECT setval(pg_get_serial_sequence('role',               'id'), COALESCE(MAX(id), 0), true) FROM role;
SELECT setval(pg_get_serial_sequence('category',           'id'), COALESCE(MAX(id), 0), true) FROM category;
SELECT setval(pg_get_serial_sequence('product',            'id'), COALESCE(MAX(id), 0), true) FROM product;
SELECT setval(pg_get_serial_sequence('user_home',          'id'), COALESCE(MAX(id), 0), true) FROM user_home;
SELECT setval(pg_get_serial_sequence('product_stock_home', 'id'), COALESCE(MAX(id), 0), true) FROM product_stock_home;
SELECT setval(pg_get_serial_sequence('shopping_list',      'id'), COALESCE(MAX(id), 0), true) FROM shopping_list;
SELECT setval(pg_get_serial_sequence('product_list_item',  'id'), COALESCE(MAX(id), 0), true) FROM product_list_item;
