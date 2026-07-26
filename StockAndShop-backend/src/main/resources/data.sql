-- Rôles
INSERT INTO role (id, name, created_at, updated_at) VALUES
    (1, 'ADMIN', NOW(), NOW()),
    (2, 'USER',  NOW(), NOW()),
    (3, 'PREMIUM', NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- Catégories
INSERT INTO category (id, name, description, created_at, updated_at) VALUES
    (1, 'autres',          'Autres', NOW(), NOW()),
    (2, 'épicerie',          'Pâtes, riz, farine...', NOW(), NOW()),
    (3, 'conserves',         'Sauces, bocaux...', NOW(), NOW()),
    (4, 'boissons',          'Eau, jus, sodas, bières, spiritueux', NOW(), NOW()),
    (5, 'boulangerie',          'Pain, viennoiseries, baguette, ...', NOW(), NOW()),
    (6, 'produits laitiers et fromage', 'Lait, yaourts, fromages...', NOW(), NOW()),
    (7, 'surgelés', 'Glaces, légumes congelés, ...', NOW(), NOW()),
    (8, 'fruits et légumes', '', NOW(), NOW()),
    (9, 'boucherie et charcuterie', '', NOW(), NOW()),
    (10, 'hygiène', 'Parfum, savon, produits ménagers, ...', NOW(), NOW()),
    (11, 'Confiserie et snack', 'Bonbon, chocolat, chips, biscuits, ...', NOW(), NOW())


ON CONFLICT (id) DO NOTHING;

-- -- Produits
-- INSERT INTO product (id, name, unity, category_id, created_at, updated_at) VALUES
--     (1, 'lait',          'MILLILITER',     6, NOW(), NOW()),
--     (2, 'farine',        'GRAMS', 2, NOW(), NOW()),
--     (4, 'jus d''orange', 'MILLILITER',     4, NOW(), NOW()),
--     (5, 'pâtes',         'PACKET',    2, NOW(), NOW()),
--     (6, 'riz',           'GRAMS', 2, NOW(), NOW()),
--     (7, 'sauce tomate',  'JAR',       3, NOW(), NOW()),
--     (8, 'eau minérale',  'BOTTLE',    4, NOW(), NOW()),
--     -- ÉPICERIE (2)
--     (9, 'spaghetti', 'PACKET', 2, NOW(), NOW()),
--     (10, 'macaroni', 'PACKET', 2, NOW(), NOW()),
--     (11, 'tagliatelles', 'PACKET', 2, NOW(), NOW()),
--     (12, 'semoule', 'GRAMS', 2, NOW(), NOW()),
--     (13, 'quinoa', 'GRAMS', 2, NOW(), NOW()),
--     (14, 'lentilles', 'GRAMS', 2, NOW(), NOW()),
--     (15, 'haricots secs', 'GRAMS', 2, NOW(), NOW()),
--     (16, 'sucre blanc', 'GRAMS', 2, NOW(), NOW()),
--     (17, 'sucre roux', 'GRAMS', 2, NOW(), NOW()),
--     (18, 'sel', 'GRAMS', 2, NOW(), NOW()),
--     (19, 'poivre', 'GRAMS', 2, NOW(), NOW()),
--     (20, 'huile d''olive', 'MILLILITER', 2, NOW(), NOW()),
--     (21, 'huile de tournesol', 'MILLILITER', 2, NOW(), NOW()),
--     (22, 'vinaigre', 'MILLILITER', 2, NOW(), NOW()),
--     (23, 'moutarde', 'JAR', 2, NOW(), NOW()),
--     (24, 'ketchup', 'BOTTLE', 2, NOW(), NOW()),
--     (25, 'mayonnaise', 'JAR', 2, NOW(), NOW()),
--
--     -- CONSERVES (3)
--     (26, 'thon en conserve', 'CAN', 3, NOW(), NOW()),
--     (27, 'sardines', 'CAN', 3, NOW(), NOW()),
--     (28, 'maïs', 'CAN', 3, NOW(), NOW()),
--     (29, 'petits pois', 'CAN', 3, NOW(), NOW()),
--     (30, 'haricots rouges', 'CAN', 3, NOW(), NOW()),
--     (31, 'pois chiches', 'CAN', 3, NOW(), NOW()),
--     (32, 'champignons', 'CAN', 3, NOW(), NOW()),
--     (33, 'coulis de tomate', 'BOTTLE', 3, NOW(), NOW()),
--     (34, 'ratatouille', 'CAN', 3, NOW(), NOW()),
--     (35, 'compote de pommes', 'JAR', 3, NOW(), NOW()),
--
--     -- BOISSONS (4)
--     (36, 'coca cola', 'BOTTLE', 4, NOW(), NOW()),
--     (37, 'limonade', 'BOTTLE', 4, NOW(), NOW()),
--     (38, 'ice tea', 'BOTTLE', 4, NOW(), NOW()),
--     (39, 'jus de pomme', 'BOTTLE', 4, NOW(), NOW()),
--     (40, 'jus multifruits', 'BOTTLE', 4, NOW(), NOW()),
--     (41, 'eau gazeuse', 'BOTTLE', 4, NOW(), NOW()),
--     (42, 'café', 'GRAMS', 4, NOW(), NOW()),
--     (43, 'thé', 'PACKET', 4, NOW(), NOW()),
--     (44, 'chocolat chaud', 'GRAMS', 4, NOW(), NOW()),
--     (45, 'sirop de grenadine', 'BOTTLE', 4, NOW(), NOW()),
--
--     -- BOULANGERIE (5)
--     (46, 'baguette', 'PIECE', 5, NOW(), NOW()),
--     (47, 'pain complet', 'PIECE', 5, NOW(), NOW()),
--     (48, 'pain de mie', 'PACKET', 5, NOW(), NOW()),
--     (49, 'croissant', 'PIECE', 5, NOW(), NOW()),
--     (50, 'pain au chocolat', 'PIECE', 5, NOW(), NOW()),
--     (51, 'brioche', 'PIECE', 5, NOW(), NOW()),
--     (52, 'wraps', 'PACKET', 5, NOW(), NOW()),
--     (53, 'tortillas', 'PACKET', 5, NOW(), NOW()),
--
--     -- PRODUITS LAITIERS (6)
--     (54, 'lait demi-écrémé', 'MILLILITER', 6, NOW(), NOW()),
--     (55, 'lait entier', 'MILLILITER', 6, NOW(), NOW()),
--     (56, 'yaourt', 'PACKET', 6, NOW(), NOW()),
--     (58, 'beurre', 'GRAMS', 6, NOW(), NOW()),
--     (59, 'crème fraîche', 'MILLILITER', 6, NOW(), NOW()),
--     (60, 'emmental râpé', 'GRAMS', 6, NOW(), NOW()),
--     (61, 'camembert', 'PIECE', 6, NOW(), NOW()),
--     (62, 'mozzarella', 'PIECE', 6, NOW(), NOW()),
--     (63, 'parmesan', 'GRAMS', 6, NOW(), NOW()),
--     (64, 'fromage de chèvre', 'PIECE', 6, NOW(), NOW()),
--
--     -- SURGELÉS (7)
--     (65, 'frites surgelées', 'GRAMS', 7, NOW(), NOW()),
--     (66, 'pizza surgelée', 'PIECE', 7, NOW(), NOW()),
--     (67, 'épinards surgelés', 'PACKET', 7, NOW(), NOW()),
--     (68, 'petits pois surgelés', 'PACKET', 7, NOW(), NOW()),
--     (69, 'haricots verts surgelés', 'PACKET', 7, NOW(), NOW()),
--     (70, 'glace vanille', 'BOX', 7, NOW(), NOW()),
--     (71, 'glace chocolat', 'BOX', 7, NOW(), NOW()),
--     (72, 'nuggets de poulet', 'PACKET', 7, NOW(), NOW()),
--
--     -- FRUITS ET LÉGUMES (8)
--     (73, 'pommes', 'PIECE', 8, NOW(), NOW()),
--     (74, 'bananes', 'PIECE', 8, NOW(), NOW()),
--     (75, 'oranges', 'PIECE', 8, NOW(), NOW()),
--     (76, 'fraises', 'GRAMS', 8, NOW(), NOW()),
--     (77, 'raisins', 'GRAMS', 8, NOW(), NOW()),
--     (78, 'tomates', 'PIECE', 8, NOW(), NOW()),
--     (79, 'salade verte', 'PIECE', 8, NOW(), NOW()),
--     (80, 'concombre', 'PIECE', 8, NOW(), NOW()),
--     (81, 'carottes', 'GRAMS', 8, NOW(), NOW()),
--     (82, 'oignons', 'GRAMS', 8, NOW(), NOW()),
--     (83, 'ail', 'PIECE', 8, NOW(), NOW()),
--     (84, 'courgettes', 'GRAMS', 8, NOW(), NOW()),
--     (85, 'poivrons', 'GRAMS', 8, NOW(), NOW()),
--     (86, 'pommes de terre', 'GRAMS', 8, NOW(), NOW()),
--
--     -- BOUCHERIE ET CHARCUTERIE (9)
--     (87, 'steak haché', 'GRAMS', 9, NOW(), NOW()),
--     (88, 'blanc de poulet', 'GRAMS', 9, NOW(), NOW()),
--     (89, 'cuisse de poulet', 'PIECE', 9, NOW(), NOW()),
--     (90, 'escalope de dinde', 'GRAMS', 9, NOW(), NOW()),
--     (91, 'jambon blanc', 'PACKET', 9, NOW(), NOW()),
--     (92, 'jambon cru', 'PACKET', 9, NOW(), NOW()),
--     (93, 'saucisson', 'PIECE', 9, NOW(), NOW()),
--     (94, 'lardons', 'PACKET', 9, NOW(), NOW()),
--     (95, 'chipolatas', 'PACKET', 9, NOW(), NOW()),
--     (96, 'merguez', 'PACKET', 9, NOW(), NOW()),
--
--     -- HYGIÈNE (10)
--     (97, 'papier toilette', 'PACKET', 10, NOW(), NOW()),
--     (98, 'essuie-tout', 'PACKET', 10, NOW(), NOW()),
--     (99, 'dentifrice', 'PIECE', 10, NOW(), NOW()),
--     (100, 'brosse à dents', 'PIECE', 10, NOW(), NOW()),
--     (101, 'shampoing', 'BOTTLE', 10, NOW(), NOW()),
--     (102, 'gel douche', 'BOTTLE', 10, NOW(), NOW()),
--     (103, 'savon', 'PIECE', 10, NOW(), NOW()),
--     (104, 'lessive', 'BOTTLE', 10, NOW(), NOW()),
--     (105, 'liquide vaisselle', 'BOTTLE', 10, NOW(), NOW()),
--     (106, 'éponge', 'PIECE', 10, NOW(), NOW()),
--     (107, 'désodorisant', 'BOTTLE', 10, NOW(), NOW()),
--     (108, 'déodorant', 'PIECE', 10, NOW(), NOW()),
--
--     -- AUTRES (1)
--     (109, 'pile', 'PACKET', 1, NOW(), NOW()),
--     (110, 'ampoule', 'PIECE', 1, NOW(), NOW()),
--     (111, 'papier aluminium', 'PACKET', 1, NOW(), NOW()),
--     (112, 'film alimentaire', 'PACKET', 1, NOW(), NOW()),
--     (113, 'sacs poubelle', 'PACKET', 1, NOW(), NOW()),
--     (114, 'mouchoirs', 'PACKET', 1, NOW(), NOW())
-- ON CONFLICT (id) DO NOTHING;

-- Réinitialisation des séquences : toujours après le max réel de chaque table
SELECT setval(pg_get_serial_sequence('role',               'id'), COALESCE(MAX(id), 1), true) FROM role;
SELECT setval(pg_get_serial_sequence('category',           'id'), COALESCE(MAX(id), 1), true) FROM category;
SELECT setval(pg_get_serial_sequence('product',            'id'), COALESCE(MAX(id), 1), true) FROM product;

CREATE EXTENSION IF NOT EXISTS unaccent;
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE OR REPLACE FUNCTION f_unaccent(text) RETURNS text
  LANGUAGE sql IMMUTABLE PARALLEL SAFE STRICT AS
$$ SELECT unaccent('unaccent'::regdictionary, $1) $$;
CREATE INDEX IF NOT EXISTS idx_product_search
  ON product USING gin (f_unaccent(lower(name)) gin_trgm_ops);