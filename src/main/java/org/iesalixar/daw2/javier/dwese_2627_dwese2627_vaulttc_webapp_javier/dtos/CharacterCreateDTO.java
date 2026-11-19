package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO del formulario de ALTA de personaje.
 * <p>
 * Las anotaciones de Bean Validation ({@code @NotBlank}, {@code @Size}, {@code @NotNull}) se comprueban
 * cuando el controlador recibe el DTO con {@code @Valid}; los fallos llegan en el {@code BindingResult}.
 * Los mensajes van entre llaves ({@code {clave}}) para que se traduzcan desde messages_*.properties.
 * <p>
 * {@code id} e {@code imageUrl} existen en ambos DTOs (alta y edición) para poder compartir la misma
 * plantilla de formulario; en el alta siempre van a null.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterCreateDTO {

    private Long id;

    @NotBlank(message = "{msg.character.name.notEmpty}")
    @Size(max = 100, message = "{msg.character.name.size}")
    private String name;

    @Size(max = 500, message = "{msg.character.description.size}")
    private String description;

    /** Ruta de la imagen ya guardada (solo se usa al editar). */
    private String imageUrl;

    /** El juego se elige en un select: el formulario envía solo su id. */
    @NotNull(message = "{msg.character.gameId.notNull}")
    private Long gameId;

    /** Imagen opcional subida desde el formulario (multipart/form-data). */
    private MultipartFile imageFile;
}
