package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions;

import lombok.Getter;

/**
 * Indica que un recurso no se puede eliminar porque otros datos dependen de él
 * (por ejemplo, un juego que todavía tiene personajes asociados).
 */
@Getter
public class ResourceInUseException extends RuntimeException {

    /** Recurso que se intentaba eliminar (ej. "game"). */
    private final String resource;
    /** Campo usado para identificarlo (ej. "id"). */
    private final String field;
    /** Valor del campo (ej. 3). */
    private final Object value;

    public ResourceInUseException(String resource, String field, Object value) {
        super(resource + " is in use (" + field + "=" + value + ")");
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}
