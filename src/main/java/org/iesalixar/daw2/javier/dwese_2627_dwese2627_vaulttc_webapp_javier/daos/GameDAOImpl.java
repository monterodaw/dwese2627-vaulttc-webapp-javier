package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.daos;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Character;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Implementación de {@link GameDAO} con JPA ({@link EntityManager}).
 * {@code @Repository} registra la clase como bean y traduce las excepciones de BD a excepciones de Spring.
 */
@Repository
@Transactional
public class GameDAOImpl implements GameDAO {

    private static final Logger logger = LoggerFactory.getLogger(GameDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Game> listAllGames() {
        logger.info("Listing all games from the database.");
        return entityManager.createQuery("SELECT g FROM Game g ORDER BY g.name", Game.class).getResultList();
    }

    /**
     * Lista una página de juegos. El campo de ordenación se valida con una lista blanca:
     * nunca se concatena tal cual lo que llega del navegador (evita inyección HQL).
     */
    @Override
    public List<Game> listGamesPage(int page, int size, String sortField, String sortDir) {
        logger.info("Listing games page={}, size={}, sortField={}, sortDir={}", page, size, sortField, sortDir);

        String orderBy = switch (sortField) {
            case "id" -> "g.id";
            case "name" -> "g.name";
            default -> {
                logger.warn("Unknown sortField '{}' for Game, defaulting to 'name'.", sortField);
                yield "g.name";
            }
        };
        String direction = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";

        return entityManager
                .createQuery("SELECT g FROM Game g ORDER BY " + orderBy + " " + direction + ", g.id ASC", Game.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countGames() {
        Long total = entityManager.createQuery("SELECT COUNT(g) FROM Game g", Long.class).getSingleResult();
        return total != null ? total : 0L;
    }

    @Override
    public Game getGameById(Long id) {
        logger.info("Retrieving game by id: {}", id);
        return entityManager.find(Game.class, id);
    }

    @Override
    public void insertGame(Game game) {
        logger.info("Inserting game name={}", game.getName());
        entityManager.persist(game);
    }

    @Override
    public void updateGame(Game game) {
        logger.info("Updating game with id: {}", game.getId());
        entityManager.merge(game);
    }

    @Override
    public void deleteGame(Long id) {
        logger.info("Deleting game with id: {}", id);
        Game game = entityManager.find(Game.class, id);
        if (game != null) {
            entityManager.remove(game);
        } else {
            logger.warn("Game with id: {} not found.", id);
        }
    }

    @Override
    public boolean existsGameByName(String name) {
        Long count = entityManager
                .createQuery("SELECT COUNT(g) FROM Game g WHERE UPPER(g.name) = :name", Long.class)
                .setParameter("name", name.toUpperCase())
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsGameByNameAndNotId(String name, Long id) {
        Long count = entityManager
                .createQuery("SELECT COUNT(g) FROM Game g WHERE UPPER(g.name) = :name AND g.id <> :id", Long.class)
                .setParameter("name", name.toUpperCase())
                .setParameter("id", id)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public List<Character> listCharactersByGameId(Long gameId) {
        return entityManager
                .createQuery("SELECT c FROM Character c JOIN FETCH c.game WHERE c.game.id = :gameId ORDER BY c.name", Character.class)
                .setParameter("gameId", gameId)
                .getResultList();
    }

    @Override
    public long countCharactersByGameId(Long gameId) {
        Long count = entityManager
                .createQuery("SELECT COUNT(c) FROM Character c WHERE c.game.id = :gameId", Long.class)
                .setParameter("gameId", gameId)
                .getSingleResult();
        return count != null ? count : 0L;
    }
}
