package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO del formulario de EDICIÓN de juego. Igual que el de alta pero con {@code id} obligatorio. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameUpdateDTO {

    @NotNull(message = "{msg.game.id.notNull}")
    private Long id;

    @NotBlank(message = "{msg.game.name.notEmpty}")
    @Size(max = 100, message = "{msg.game.name.size}")
    private String name;

    @Size(max = 255, message = "{msg.game.logoUrl.size}")
    private String logoUrl;
}
