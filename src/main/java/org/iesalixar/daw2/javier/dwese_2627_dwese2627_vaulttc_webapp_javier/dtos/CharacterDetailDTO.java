package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la pantalla de DETALLE. Incluye el juego completo (objeto embebido)
 * para poder mostrar todos sus datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDetailDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private GameDTO game;
}
