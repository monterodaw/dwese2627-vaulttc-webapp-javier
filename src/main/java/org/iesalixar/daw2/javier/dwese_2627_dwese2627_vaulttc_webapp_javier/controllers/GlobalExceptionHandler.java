package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Manejo de errores común a toda la app.
 * <p>
 * Cuando el fichero supera el límite duro de Spring (spring.servlet.multipart.max-file-size),
 * la excepción salta ANTES de llegar al controlador, así que no se puede capturar con un try/catch
 * en él: hay que hacerlo aquí, con {@code @ControllerAdvice}.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(MaxUploadSizeExceededException ex,
                                      RedirectAttributes redirectAttributes,
                                      Locale locale) {
        logger.warn("Subida rechazada por tamaño: {}", ex.getMessage());
        String msg = messageSource.getMessage("msg.character.imageFile.tooBig", null, locale);
        redirectAttributes.addFlashAttribute("errorMessage", msg);
        return "redirect:/characters";
    }
}
