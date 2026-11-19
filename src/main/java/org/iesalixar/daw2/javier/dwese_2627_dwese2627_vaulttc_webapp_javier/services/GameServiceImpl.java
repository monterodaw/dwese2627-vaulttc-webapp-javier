package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.services;

import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.daos.GameDAO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameCreateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameDetailDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameUpdateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Game;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.ResourceInUseException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.mappers.GameMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de Game:
 * - El nombre del juego es único.
 * - No se puede borrar un juego que todavía tiene personajes.
 */
@Service
@Transactional
public class GameServiceImpl implements GameService {

    private static final String RESOURCE = "game";

    @Autowired
    private GameDAO gameDAO;

    @Override
    @Transactional(readOnly = true)
    public Page<GameDTO> list(Pageable pageable) {
        String sortField = "name";
        String sortDir = "asc";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortField = order.getProperty();
            sortDir = order.getDirection().name().toLowerCase();
        }
        List<Game> games = gameDAO.listGamesPage(pageable.getPageNumber(), pageable.getPageSize(), sortField, sortDir);
        return new PageImpl<>(GameMapper.toDTOList(games), pageable, gameDAO.countGames());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDTO> listAll() {
        return GameMapper.toDTOList(gameDAO.listAllGames());
    }

    @Override
    @Transactional(readOnly = true)
    public GameDetailDTO getDetail(Long id) {
        Game game = findOrThrow(id);
        return GameMapper.toDetailDTO(game, gameDAO.listCharactersByGameId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public GameUpdateDTO getForEdit(Long id) {
        return GameMapper.toUpdateDTO(findOrThrow(id));
    }

    @Override
    public void create(GameCreateDTO dto) {
        if (gameDAO.existsGameByName(dto.getName().trim())) {
            throw new DuplicateResourceException(RESOURCE, "name", dto.getName());
        }
        gameDAO.insertGame(GameMapper.toEntity(dto));
    }

    @Override
    public void update(GameUpdateDTO dto) {
        Game game = findOrThrow(dto.getId());
        if (gameDAO.existsGameByNameAndNotId(dto.getName().trim(), dto.getId())) {
            throw new DuplicateResourceException(RESOURCE, "name", dto.getName());
        }
        GameMapper.copyToExistingEntity(dto, game);
        gameDAO.updateGame(game);
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        if (gameDAO.countCharactersByGameId(id) > 0) {
            throw new ResourceInUseException(RESOURCE, "id", id);
        }
        gameDAO.deleteGame(id);
    }

    private Game findOrThrow(Long id) {
        Game game = gameDAO.getGameById(id);
        if (game == null) {
            throw new ResourceNotFoundException(RESOURCE, "id", id);
        }
        return game;
    }
}