package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions;

import lombok.Getter;

/**
 * Indica que no se ha encontrado un recurso (por ejemplo, un personaje con id=5 o un juego con id=9).
 * La lanza la capa de servicio y el controlador decide cómo mostrarla al usuario.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {

    /** Recurso que no se ha encontrado (ej. "character", "game"). */
    private final String resource;
    /** Campo usado en la búsqueda (ej. "id"). */
    private final String field;
    /** Valor buscado (ej. 5). */
    private final Object value;

    public ResourceNotFoundException(String resource, String field, Object value) {
        super(resource + " not found (" + field + "=" + value + ")");
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}
