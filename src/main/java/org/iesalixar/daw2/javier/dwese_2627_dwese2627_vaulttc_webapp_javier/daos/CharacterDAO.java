package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.daos;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Character;
import java.util.List;

/**
 * Acceso a datos de {@link Character}. Es la única capa que "habla" con la base de datos.
 * No contiene lógica de negocio: solo consultas y operaciones de persistencia.
 */
public interface CharacterDAO {
    List<Character> listCharactersPage(int page, int size, String sortField, String sortDir);

    long countCharacters();

    void insertCharacter(Character character);

    void updateCharacter(Character character);

    void deleteCharacter(Long id);

    /**
     * Devuelve el personaje con su juego ya cargado, o null si no existe.
     */
    Character getCharacterById(Long id);

    /**
     * ¿Existe ya un personaje con ese nombre dentro de ese juego? (alta)
     */
    boolean existsCharacterByNameAndGame(String name, Long gameId);

    /**
     * ¿Existe OTRO personaje (distinto id) con ese nombre dentro de ese juego? (edición)
     */
    boolean existsCharacterByNameAndGameAndNotId(String name, Long gameId, Long id);
}