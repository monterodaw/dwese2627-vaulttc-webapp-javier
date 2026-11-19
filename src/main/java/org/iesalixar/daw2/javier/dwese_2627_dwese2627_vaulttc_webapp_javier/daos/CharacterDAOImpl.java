package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.daos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Implementación de {@link CharacterDAO} con JPA ({@link EntityManager}) y consultas HQL/JPQL.
 */
@Repository
@Transactional
public class CharacterDAOImpl implements CharacterDAO {

    private static final Logger logger = LoggerFactory.getLogger(CharacterDAOImpl.class);

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Character> listCharactersPage(int page, int size, String sortField, String sortDir) {
        logger.info("Listing characters page={}, size={}, sortField={}, sortDir={}", page, size, sortField, sortDir);

        String orderBy = switch (sortField) {
            case "id" -> "c.id";
            case "gameName" -> "g.name";
            case "name" -> "c.name";
            default -> {
                logger.warn("Unknown sortField '{}' for Character, defaulting to 'name'.", sortField);
                yield "c.name";
            }
        };
        String direction = "desc".equalsIgnoreCase(sortDir) ? "DESC" : "ASC";

        String hql = "SELECT c FROM Character c JOIN FETCH c.game g ORDER BY " + orderBy + " " + direction + ", c.id ASC";

        return entityManager.createQuery(hql, Character.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public long countCharacters() {
        Long total = entityManager.createQuery("SELECT COUNT(c) FROM Character c", Long.class).getSingleResult();
        return total != null ? total : 0L;
    }

    @Override
    public void insertCharacter(Character character) {
        logger.info("Inserting character name={}, gameId={}", character.getName(), character.getGame().getId());
        entityManager.persist(character);
    }

    @Override
    public void updateCharacter(Character character) {
        logger.info("Updating character with id: {}", character.getId());
        entityManager.merge(character);
    }

    @Override
    public void deleteCharacter(Long id) {
        logger.info("Deleting character with id: {}", id);
        Character character = entityManager.find(Character.class, id);
        if (character != null) {
            entityManager.remove(character);
        } else {
            logger.warn("Character with id: {} not found.", id);
        }
    }

    @Override
    public Character getCharacterById(Long id) {
        logger.info("Retrieving character by id: {}", id);
        List<Character> result = entityManager
                .createQuery("SELECT c FROM Character c JOIN FETCH c.game WHERE c.id = :id", Character.class)
                .setParameter("id", id)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public boolean existsCharacterByNameAndGame(String name, Long gameId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Character c WHERE UPPER(c.name) = :name AND c.game.id = :gameId", Long.class)
                .setParameter("name", name.toUpperCase())
                .setParameter("gameId", gameId)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsCharacterByNameAndGameAndNotId(String name, Long gameId, Long id) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(c) FROM Character c WHERE UPPER(c.name) = :name AND c.game.id = :gameId AND c.id <> :id", Long.class)
                .setParameter("name", name.toUpperCase())
                .setParameter("gameId", gameId)
                .setParameter("id", id)
                .getSingleResult();
        return count != null && count > 0;
    }
    }
