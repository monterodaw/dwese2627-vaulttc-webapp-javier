package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de lectura de Game. Se usa para rellenar el {@code <select>} del formulario
 * y para mostrar los datos del juego en el detalle del personaje.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameDTO {
    private Long id;
    private String name;
    private String logoUrl;
}
