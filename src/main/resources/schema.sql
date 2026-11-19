-- Entidad relacionada (lado "1" de la relación 1:N): un juego tiene muchos personajes
CREATE TABLE IF NOT EXISTS games (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    name     VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(180)
    );

-- Entidad principal del CRUD (lado "N"): cada personaje pertenece a un único juego
CREATE TABLE IF NOT EXISTS characters (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    image_url   VARCHAR(255),
    game_id     BIGINT NOT NULL,
    CONSTRAINT fk_characters_game FOREIGN KEY (game_id) REFERENCES games(id),
    CONSTRAINT uq_characters_game_name UNIQUE (game_id, name)
    );
