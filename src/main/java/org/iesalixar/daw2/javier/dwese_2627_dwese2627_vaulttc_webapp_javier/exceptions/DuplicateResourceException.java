package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions;

import lombok.Getter;

/**
 * Indica que ya existe un recurso con un valor que debería ser único
 * (por ejemplo, un personaje con el mismo nombre dentro del mismo juego).
 */
@Getter
public class DuplicateResourceException extends RuntimeException {

    /** Recurso en el que se detecta el duplicado (ej. "character"). */
    private final String resource;
    /** Campo duplicado (ej. "name"). */
    private final String field;
    /** Valor duplicado (ej. "Jinx"). */
    private final Object value;

    public DuplicateResourceException(String resource, String field, Object value) {
        super("Duplicate " + resource + " (" + field + "=" + value + ")");
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
}
