package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO del formulario de ALTA de juego. Las anotaciones de Bean Validation se comprueban cuando el
 * controlador recibe el DTO con {@code @Valid}; los mensajes {clave} se traducen desde messages_*.properties.
 * {@code id} existe también aquí para poder compartir la plantilla del formulario con la edición.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateDTO {

    private Long id;

    @NotBlank(message = "{msg.game.name.notEmpty}")
    @Size(max = 100, message = "{msg.game.name.size}")
    private String name;

    @Size(max = 255, message = "{msg.game.logoUrl.size}")
    private String logoUrl;
}
