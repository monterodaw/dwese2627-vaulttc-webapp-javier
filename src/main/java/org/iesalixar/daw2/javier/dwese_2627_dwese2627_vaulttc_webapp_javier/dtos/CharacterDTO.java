package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de lectura para el LISTADO de personajes.
 * Aplana la relación: en vez del objeto Game completo solo lleva {@code gameName},
 * que es lo único que muestra la tabla.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String gameName;
}
