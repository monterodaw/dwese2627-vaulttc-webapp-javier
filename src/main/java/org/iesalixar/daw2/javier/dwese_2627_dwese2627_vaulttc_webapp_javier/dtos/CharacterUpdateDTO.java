package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO del formulario de EDICIÓN de personaje. Igual que el de alta, pero con {@code id} obligatorio.
 * Si no se sube una imagen nueva, se conserva la que ya tenía ({@code imageUrl}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterUpdateDTO {

    @NotNull(message = "{msg.character.id.notNull}")
    private Long id;

    @NotBlank(message = "{msg.character.name.notEmpty}")
    @Size(max = 100, message = "{msg.character.name.size}")
    private String name;

    @Size(max = 500, message = "{msg.character.description.size}")
    private String description;

    /** Imagen actual (se mantiene en un campo oculto del formulario). */
    private String imageUrl;

    @NotNull(message = "{msg.character.gameId.notNull}")
    private Long gameId;

    /** Imagen nueva (opcional): si viene, sustituye a la anterior. */
    private MultipartFile imageFile;
}
