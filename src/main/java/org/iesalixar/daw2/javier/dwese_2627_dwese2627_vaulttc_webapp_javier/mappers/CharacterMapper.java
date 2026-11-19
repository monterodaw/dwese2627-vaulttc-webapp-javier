package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.mappers;

import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterCreateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterDetailDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterUpdateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Character;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities.Game;

import java.util.List;

/**
 * Mapper utilitario entre la entidad {@link Character} y sus DTOs.
 * <p>
 * Regla: las entidades no salen de la capa de servicio hacia la vista, solo DTOs;
 * y el formulario nunca crea entidades directamente, el mapper las construye a partir del DTO.
 */
public class CharacterMapper {

    // ---------------- Entity -> DTO ----------------

    /** Para el listado: aplana la relación quedándose solo con el nombre del juego. */
    public static CharacterDTO toDTO(Character entity) {
        if (entity == null) return null;
        CharacterDTO dto = new CharacterDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());
        if (entity.getGame() != null) {
            dto.setGameName(entity.getGame().getName());
        }
        return dto;
    }

    public static List<CharacterDTO> toDTOList(List<Character> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(CharacterMapper::toDTO).toList();
    }

    /** Para el detalle: incluye el juego completo. */
    public static CharacterDetailDTO toDetailDTO(Character entity) {
        if (entity == null) return null;
        CharacterDetailDTO dto = new CharacterDetailDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());
        dto.setGame(GameMapper.toDTO(entity.getGame()));
        return dto;
    }

    /** Para precargar el formulario de edición (el select se marca con gameId). */
    public static CharacterUpdateDTO toUpdateDTO(Character entity) {
        if (entity == null) return null;
        CharacterUpdateDTO dto = new CharacterUpdateDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());
        dto.setGameId(entity.getGame() != null ? entity.getGame().getId() : null);
        return dto;
    }

    // ---------------- DTO -> Entity ----------------

    /**
     * Crea una entidad nueva desde el DTO de alta. El {@code Game} ya viene cargado por el servicio
     * (así sabemos que existe) y la ruta de la imagen es la que devolvió el almacenamiento de ficheros.
     */
    public static Character toEntity(CharacterCreateDTO dto, Game game, String imageUrl) {
        if (dto == null) return null;
        Character entity = new Character();
        entity.setName(dto.getName().trim());
        entity.setDescription(blankToNull(dto.getDescription()));
        entity.setImageUrl(imageUrl);
        entity.setGame(game);
        return entity;
    }

    /**
     * Copia los campos editables del DTO sobre una entidad ya existente (cargada de BD).
     * No toca el id. {@code imageUrl} es la imagen final (la nueva o la que ya tenía).
     */
    public static void copyToExistingEntity(CharacterUpdateDTO dto, Character entity, Game game, String imageUrl) {
        if (dto == null || entity == null) return;
        entity.setName(dto.getName().trim());
        entity.setDescription(blankToNull(dto.getDescription()));
        entity.setImageUrl(imageUrl);
        entity.setGame(game);
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
