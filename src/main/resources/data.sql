-- ==================== PRODUCTS ====================
INSERT INTO product (name, created_at, updated_at) VALUES
    ('Lait', NOW(), NOW()),
    ('Pain', NOW(), NOW()),
    ('Beurre', NOW(), NOW()),
    ('Oeufs', NOW(), NOW()),
    ('Fromage', NOW(), NOW()),
    ('Jambon', NOW(), NOW()),
    ('Poulet', NOW(), NOW()),
    ('Pâtes', NOW(), NOW()),
    ('Riz', NOW(), NOW()),
    ('Pommes de terre', NOW(), NOW()),
    ('Tomates', NOW(), NOW()),
    ('Salade', NOW(), NOW()),
    ('Carottes', NOW(), NOW()),
    ('Pommes', NOW(), NOW()),
    ('Bananes', NOW(), NOW()),
    ('Yaourts', NOW(), NOW()),
    ('Céréales', NOW(), NOW()),
    ('Jus d''orange', NOW(), NOW()),
    ('Eau minérale', NOW(), NOW()),
    ('Café', NOW(), NOW());

-- ==================== USERS ====================
-- Mot de passe pour tous : "password"
INSERT INTO users (id, email, username, password, role, created_at, updated_at) VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'admin@test.be',  'Admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', NOW(), NOW()),
    ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'alice@test.be',  'Alice', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER',  NOW(), NOW()),
    ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'bob@test.be',    'Bob',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER',  NOW(), NOW());

-- ==================== HOMES ====================
INSERT INTO home (name, description, created_at, updated_at) VALUES
    ('Maison Dupont',      'Résidence principale famille Dupont', NOW(), NOW()),
    ('Appartement Alice',  'Studio centre-ville',                 NOW(), NOW());

-- ==================== USER_HOME ====================
-- Admin et Alice dans Maison Dupont, Alice et Bob dans Appartement Alice
INSERT INTO user_home (user_id, home_id, role, created_at, updated_at)
SELECT u.id, h.id, 'OWNER', NOW(), NOW() FROM users u, home h WHERE u.email = 'admin@test.be' AND h.name = 'Maison Dupont';

INSERT INTO user_home (user_id, home_id, role, created_at, updated_at)
SELECT u.id, h.id, 'USER', NOW(), NOW() FROM users u, home h WHERE u.email = 'alice@test.be' AND h.name = 'Maison Dupont';

INSERT INTO user_home (user_id, home_id, role, created_at, updated_at)
SELECT u.id, h.id, 'OWNER', NOW(), NOW() FROM users u, home h WHERE u.email = 'alice@test.be' AND h.name = 'Appartement Alice';

INSERT INTO user_home (user_id, home_id, role, created_at, updated_at)
SELECT u.id, h.id, 'USER', NOW(), NOW() FROM users u, home h WHERE u.email = 'bob@test.be' AND h.name = 'Appartement Alice';

-- ==================== PRODUCT STOCK LINES ====================
-- Stock Maison Dupont
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 3,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Maison Dupont'     AND p.name = 'Lait';
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 2,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Maison Dupont'     AND p.name = 'Pain';
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 12, NOW(), NOW() FROM home h, product p WHERE h.name = 'Maison Dupont'     AND p.name = 'Oeufs';
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 1,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Maison Dupont'     AND p.name = 'Fromage';
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 5,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Maison Dupont'     AND p.name = 'Riz';
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 1,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Maison Dupont'     AND p.name = 'Céréales';
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 2,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Maison Dupont'     AND p.name = 'Café';

-- Stock Appartement Alice
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 1,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Appartement Alice' AND p.name = 'Lait';
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 2,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Appartement Alice' AND p.name = 'Poulet';
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 3,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Appartement Alice' AND p.name = 'Pâtes';
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 6,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Appartement Alice' AND p.name = 'Eau minérale';
INSERT INTO product_stock_line (home_id, product_id, quantity, created_at, updated_at)
SELECT h.id, p.id, 1,  NOW(), NOW() FROM home h, product p WHERE h.name = 'Appartement Alice' AND p.name = 'Jus d''orange';

-- ==================== SHOPPING LISTS ====================
INSERT INTO shopping_list (name, description, home_id, created_at, updated_at)
SELECT 'Courses hebdo',       'Courses de la semaine',   h.id, NOW(), NOW() FROM home h WHERE h.name = 'Maison Dupont';
INSERT INTO shopping_list (name, description, home_id, created_at, updated_at)
SELECT 'Réapprovisionnement', 'Stock à refaire',         h.id, NOW(), NOW() FROM home h WHERE h.name = 'Appartement Alice';

-- ==================== PRODUCT LIST LINES ====================
-- Courses hebdo (Maison Dupont)
INSERT INTO product_list_line (shopping_list_id, product_id, quantity, created_at, updated_at)
SELECT s.id, p.id, 2, NOW(), NOW() FROM shopping_list s, product p WHERE s.name = 'Courses hebdo' AND p.name = 'Beurre';
INSERT INTO product_list_line (shopping_list_id, product_id, quantity, created_at, updated_at)
SELECT s.id, p.id, 1, NOW(), NOW() FROM shopping_list s, product p WHERE s.name = 'Courses hebdo' AND p.name = 'Jambon';
INSERT INTO product_list_line (shopping_list_id, product_id, quantity, created_at, updated_at)
SELECT s.id, p.id, 5, NOW(), NOW() FROM shopping_list s, product p WHERE s.name = 'Courses hebdo' AND p.name = 'Pommes de terre';
INSERT INTO product_list_line (shopping_list_id, product_id, quantity, created_at, updated_at)
SELECT s.id, p.id, 4, NOW(), NOW() FROM shopping_list s, product p WHERE s.name = 'Courses hebdo' AND p.name = 'Tomates';
INSERT INTO product_list_line (shopping_list_id, product_id, quantity, created_at, updated_at)
SELECT s.id, p.id, 6, NOW(), NOW() FROM shopping_list s, product p WHERE s.name = 'Courses hebdo' AND p.name = 'Pommes';
INSERT INTO product_list_line (shopping_list_id, product_id, quantity, created_at, updated_at)
SELECT s.id, p.id, 4, NOW(), NOW() FROM shopping_list s, product p WHERE s.name = 'Courses hebdo' AND p.name = 'Bananes';

-- Réapprovisionnement (Appartement Alice)
INSERT INTO product_list_line (shopping_list_id, product_id, quantity, created_at, updated_at)
SELECT s.id, p.id, 2, NOW(), NOW() FROM shopping_list s, product p WHERE s.name = 'Réapprovisionnement' AND p.name = 'Lait';
INSERT INTO product_list_line (shopping_list_id, product_id, quantity, created_at, updated_at)
SELECT s.id, p.id, 1, NOW(), NOW() FROM shopping_list s, product p WHERE s.name = 'Réapprovisionnement' AND p.name = 'Pain';
INSERT INTO product_list_line (shopping_list_id, product_id, quantity, created_at, updated_at)
SELECT s.id, p.id, 3, NOW(), NOW() FROM shopping_list s, product p WHERE s.name = 'Réapprovisionnement' AND p.name = 'Carottes';
INSERT INTO product_list_line (shopping_list_id, product_id, quantity, created_at, updated_at)
SELECT s.id, p.id, 4, NOW(), NOW() FROM shopping_list s, product p WHERE s.name = 'Réapprovisionnement' AND p.name = 'Yaourts';
