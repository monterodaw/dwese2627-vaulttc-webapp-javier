package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.mappers;

import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameCreateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameDetailDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameUpdateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Game;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Character;
import java.util.List;


/**
 * Mapper utilitario Game (entity) -> GameDTO. Implementación simple sin frameworks de mapeo.
 */
public class GameMapper {

    public static GameDTO toDTO(Game entity) {
        if (entity == null) return null;
        return new GameDTO(entity.getId(), entity.getName(), entity.getLogoUrl());
    }

    public static List<GameDTO> toDTOList(List<Game> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(GameMapper::toDTO).toList();
    }

    /** Para el detalle: el juego más la lista de sus personajes. */
    public static GameDetailDTO toDetailDTO(Game entity, List<Character> characters) {
        if (entity == null) return null;
        GameDetailDTO dto = new GameDetailDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLogoUrl(entity.getLogoUrl());
        dto.setCharacters(CharacterMapper.toDTOList(characters));
        return dto;
    }

    /** Para precargar el formulario de edición. */
    public static GameUpdateDTO toUpdateDTO(Game entity) {
        if (entity == null) return null;
        GameUpdateDTO dto = new GameUpdateDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setLogoUrl(entity.getLogoUrl());
        return dto;
    }

    // ---------------- DTO -> Entity ----------------

    public static Game toEntity(GameCreateDTO dto) {
        if (dto == null) return null;
        Game entity = new Game();
        entity.setName(dto.getName().trim());
        entity.setLogoUrl(blankToNull(dto.getLogoUrl()));
        return entity;
    }

    /** Copia los campos editables del DTO sobre una entidad ya existente. No toca el id. */
    public static void copyToExistingEntity(GameUpdateDTO dto, Game entity) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName().trim());
        entity.setLogoUrl(blankToNull(dto.getLogoUrl()));
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
