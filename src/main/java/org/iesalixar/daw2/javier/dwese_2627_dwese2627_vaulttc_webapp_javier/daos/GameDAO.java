package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.daos;

import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Character;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Game;

import java.util.List;

/**
 * Acceso a datos de {@link Game}. Solo necesitamos lo que usa el CRUD de Character:
 * rellenar  {@code <select>} y comprobar que el juego elegido existe.
 */
public interface GameDAO {
    List<Game> listAllGames();

    List<Game> listGamesPage(int page, int size, String sortField, String sortDir);

    long countGames();

    Game getGameById(Long id);

    void insertGame(Game game);

    void updateGame(Game game);

    void deleteGame(Long id);

    /** ¿Existe ya un juego con ese nombre? (alta) */
    boolean existsGameByName(String name);

    /** ¿Existe OTRO juego (distinto id) con ese nombre? (edición) */
    boolean existsGameByNameAndNotId(String name, Long id);

    /** Personajes que pertenecen al juego (con el juego ya cargado). */
    List<Character> listCharactersByGameId(Long gameId);

    /** Número de personajes del juego. */
    long countCharactersByGameId(Long gameId);
}
