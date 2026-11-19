package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la pantalla de DETALLE de un juego. Incluye la lista de sus personajes
 * (el lado "N" de la relación 1:N), igual que el detalle de Region mostraba sus provincias.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDetailDTO {
    private Long id;
    private String name;
    private String logoUrl;
    private List<CharacterDTO> characters;
}
