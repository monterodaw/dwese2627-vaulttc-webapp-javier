package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.config;

import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Publica la carpeta de imágenes subidas en la URL /uploads/**.
 * Sin esto, las imágenes guardadas fuera de static/ no se verían en el navegador.
 */
@Configuration
public class UploadsConfig implements WebMvcConfigurer {

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = fileStorageService.getRoot().toUri().toString();
        if (!location.endsWith("/")) location += "/"; // Spring necesita la barra final para tratarlo como carpeta
        registry.addResourceHandler("/uploads/**").addResourceLocations(location);
    }
}
