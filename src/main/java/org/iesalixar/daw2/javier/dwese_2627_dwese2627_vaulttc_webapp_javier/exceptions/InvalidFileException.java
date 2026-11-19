package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions;

import lombok.Getter;

/**
 * Indica que un fichero subido no es válido (tipo no permitido, demasiado grande, contenido corrupto...).
 * Incluye la clave i18n del mensaje de error para que el controlador pueda mostrarlo
 * en el campo del formulario, en el idioma del usuario.
 */
@Getter
public class InvalidFileException extends RuntimeException {

    /** Recurso afectado (ej. "character"). */
    private final String resource;
    /** Campo del formulario asociado al fichero (ej. "imageFile"). */
    private final String field;
    /** Valor o detalle relevante (ej. "application/pdf", 3145728). */
    private final Object value;
    /** Clave del fichero messages*.properties con el texto de error para el usuario. */
    private final String messageKey;

    public InvalidFileException(String resource, String field, Object value, String messageKey) {
        super("Invalid file for " + resource + " (" + field + "=" + value + "): " + messageKey);
        this.resource = resource;
        this.field = field;
        this.value = value;
        this.messageKey = messageKey;
    }
}
