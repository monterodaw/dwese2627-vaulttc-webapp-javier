-- INSERT IGNORE: si al reiniciar la app ya existen (mismo id), no falla ni duplica.
INSERT IGNORE INTO games (id, name, logo_url) VALUES
(1, 'Genshin Impact', NULL),
(2, 'League of Legends', NULL),
(3, 'Fortnite', NULL),
(4, 'Valorant', NULL);

INSERT IGNORE INTO characters (id, name, description, image_url, game_id) VALUES
(1, 'Hu Tao',  'Directora de la funeraria Wangsheng del puerto de Liyue.', NULL, 1),
(2, 'Zhongli', 'Consultor de la funeraria Wangsheng y antiguo arconte Geo.', NULL, 1),
(3, 'Jinx',    'Criminal impulsiva y caótica de Zaun.', NULL, 2),
(4, 'Jax',     'Maestro de armas que usa una farola como arma.', NULL, 2),
(5, 'Jonesy',  'Personaje principal del modo Battle Royale.', NULL, 3),
(6, 'Jett',    'Duelista coreana que controla el viento.', NULL, 4);
