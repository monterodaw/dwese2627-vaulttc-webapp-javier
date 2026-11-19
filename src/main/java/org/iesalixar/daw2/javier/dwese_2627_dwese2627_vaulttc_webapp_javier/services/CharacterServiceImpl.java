package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.services;

import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.daos.CharacterDAO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.daos.GameDAO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterCreateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterDetailDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterUpdateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Character;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Game;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.mappers.CharacterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Lógica de negocio del CRUD de {@link Character}:
 * <ul>
 *   <li>Reglas: el juego elegido debe existir y no puede haber dos personajes con el mismo nombre en el mismo juego.</li>
 *   <li>Gestión de la imagen subida (guardar la nueva, borrar la antigua).</li>
 *   <li>Conversión Entity &lt;-&gt; DTO con {@link CharacterMapper}.</li>
 *   <li>Lanza excepciones semánticas; el controlador decide cómo mostrarlas.</li>
 * </ul>
 * {@code @Transactional}: cada método es una transacción; si lanza una RuntimeException se hace rollback.
 */
@Service
@Transactional
public class CharacterServiceImpl implements CharacterService {

    private static final String RESOURCE = "character";
    private static final String IMAGE_FOLDER = "characters";

    @Autowired
    private CharacterDAO characterDAO;

    @Autowired
    private GameDAO gameDAO;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public Page<CharacterDTO> list(Pageable pageable) {
        String sortField = "name";
        String sortDir = "asc";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortField = order.getProperty();
            sortDir = order.getDirection().name().toLowerCase();
        }
        List<Character> characters = characterDAO.listCharactersPage(
                pageable.getPageNumber(), pageable.getPageSize(), sortField, sortDir);
        long total = characterDAO.countCharacters();
        return new PageImpl<>(CharacterMapper.toDTOList(characters), pageable, total);
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterDetailDTO getDetail(Long id) {
        return CharacterMapper.toDetailDTO(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterUpdateDTO getForEdit(Long id) {
        return CharacterMapper.toUpdateDTO(findOrThrow(id));
    }

    @Override
    public void create(CharacterCreateDTO dto) {
        // 1) El juego debe existir (el id viene del navegador y no nos fiamos)
        Game game = findGameOrThrow(dto.getGameId());

        // 2) Regla de negocio: nombre único dentro del mismo juego
        if (characterDAO.existsCharacterByNameAndGame(dto.getName().trim(), game.getId())) {
            throw new DuplicateResourceException(RESOURCE, "name", dto.getName());
        }

        // 3) Imagen opcional: se valida y se guarda en disco
        String imageUrl = null;
        MultipartFile file = dto.getImageFile();
        if (file != null && !file.isEmpty()) {
            imageUrl = fileStorageService.store(file, IMAGE_FOLDER, RESOURCE, "imageFile");
        }

        // 4) Persistir. Si falla, se borra la imagen que acabamos de guardar para no dejar basura.
        try {
            characterDAO.insertCharacter(CharacterMapper.toEntity(dto, game, imageUrl));
        } catch (RuntimeException e) {
            fileStorageService.delete(imageUrl);
            throw e;
        }
    }

    @Override
    public void update(CharacterUpdateDTO dto) {
        Character character = findOrThrow(dto.getId());
        Game game = findGameOrThrow(dto.getGameId());

        if (characterDAO.existsCharacterByNameAndGameAndNotId(dto.getName().trim(), game.getId(), dto.getId())) {
            throw new DuplicateResourceException(RESOURCE, "name", dto.getName());
        }

        // La imagen "oficial" es la que está en BD (no la del campo oculto del formulario, que se podría manipular)
        String oldImage = character.getImageUrl();
        String newImage = oldImage;
        MultipartFile file = dto.getImageFile();
        boolean replaced = file != null && !file.isEmpty();
        if (replaced) {
            newImage = fileStorageService.store(file, IMAGE_FOLDER, RESOURCE, "imageFile");
        }

        try {
            CharacterMapper.copyToExistingEntity(dto, character, game, newImage);
            characterDAO.updateCharacter(character);
        } catch (RuntimeException e) {
            if (replaced) fileStorageService.delete(newImage);
            throw e;
        }

        // Solo cuando todo ha ido bien se elimina la imagen antigua
        if (replaced) {
            fileStorageService.delete(oldImage);
        }
    }

    @Override
    public void delete(Long id) {
        Character character = findOrThrow(id);
        String imageUrl = character.getImageUrl();
        characterDAO.deleteCharacter(id);
        fileStorageService.delete(imageUrl);
    }

    // ---------------- auxiliares ----------------

    private Character findOrThrow(Long id) {
        Character character = characterDAO.getCharacterById(id);
        if (character == null) {
            throw new ResourceNotFoundException(RESOURCE, "id", id);
        }
        return character;
    }

    private Game findGameOrThrow(Long gameId) {
        Game game = gameDAO.getGameById(gameId);
        if (game == null) {
            throw new ResourceNotFoundException("game", "id", gameId);
        }
        return game;
    }
}
