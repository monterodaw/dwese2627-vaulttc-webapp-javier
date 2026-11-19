package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.services;

import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.InvalidFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

/**
 * Guarda y borra imágenes subidas desde formularios.
 * <p>
 * Medidas de seguridad:
 * <ul>
 *   <li>Solo se aceptan imágenes PNG, JPEG, GIF y WEBP de hasta 2 MB.</li>
 *   <li>Se comprueba el tipo declarado Y los primeros bytes del fichero ("magic bytes"),
 *       para que no valga renombrar un .exe a .png.</li>
 *   <li>El nombre en disco lo generamos nosotros (UUID + extensión): nunca se usa el nombre que envía
 *       el usuario, lo que evita sobrescrituras y ataques de "path traversal" (../../).</li>
 * </ul>
 */
@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    /** Tamaño máximo permitido: 2 MB. */
    public static final long MAX_SIZE_BYTES = 2L * 1024 * 1024;

    /** Tipos MIME permitidos -> extensión con la que se guardan. */
    private static final Map<String, String> ALLOWED_TYPES = Map.of(
            "image/png", "png",
            "image/jpeg", "jpg",
            "image/gif", "gif",
            "image/webp", "webp");

    /** Carpeta raíz de subidas (configurable con app.upload.dir). */
    private final Path root;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear la carpeta de subidas " + root, e);
        }
        logger.info("Carpeta de subidas: {}", root);
    }

    public Path getRoot() {
        return root;
    }

    /**
     * Valida y guarda la imagen en {@code <root>/<subfolder>/<uuid>.<ext>}.
     *
     * @return ruta relativa guardada en BD (ej. "characters/3f2a....png").
     * @throws InvalidFileException si el fichero no cumple las validaciones.
     */
    public String store(MultipartFile file, String subfolder, String resource, String field) {
        String contentType = file.getContentType();
        String extension = contentType == null ? null : ALLOWED_TYPES.get(contentType.toLowerCase());

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new InvalidFileException(resource, field, file.getSize(), "msg.character.imageFile.size");
        }
        if (extension == null) {
            throw new InvalidFileException(resource, field, contentType, "msg.character.imageFile.type");
        }
        if (!matchesMagicBytes(file, extension)) {
            throw new InvalidFileException(resource, field, contentType, "msg.character.imageFile.invalid");
        }

        try {
            Path folder = root.resolve(subfolder).normalize();
            Files.createDirectories(folder);
            String fileName = UUID.randomUUID() + "." + extension;
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, folder.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
            logger.info("Imagen guardada: {}/{}", subfolder, fileName);
            return subfolder + "/" + fileName;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar la imagen", e);
        }
    }

    /** Borra una imagen previamente guardada. Si no existe o la ruta es sospechosa, no hace nada. */
    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path target = root.resolve(relativePath).normalize();
            // Protección: solo se borra dentro de la carpeta de subidas
            if (!target.startsWith(root)) {
                logger.warn("Intento de borrar fuera de la carpeta de subidas: {}", relativePath);
                return;
            }
            Files.deleteIfExists(target);
        } catch (IOException e) {
            logger.warn("No se pudo borrar la imagen {}: {}", relativePath, e.getMessage());
        }
    }

    /** Comprueba que los primeros bytes corresponden realmente al formato declarado. */
    private boolean matchesMagicBytes(MultipartFile file, String extension) {
        try (InputStream in = file.getInputStream()) {
            byte[] h = in.readNBytes(12);
            return switch (extension) {
                case "png" -> h.length >= 4 && (h[0] & 0xFF) == 0x89 && h[1] == 'P' && h[2] == 'N' && h[3] == 'G';
                case "jpg" -> h.length >= 3 && (h[0] & 0xFF) == 0xFF && (h[1] & 0xFF) == 0xD8 && (h[2] & 0xFF) == 0xFF;
                case "gif" -> h.length >= 4 && h[0] == 'G' && h[1] == 'I' && h[2] == 'F' && h[3] == '8';
                case "webp" -> h.length >= 12 && h[0] == 'R' && h[1] == 'I' && h[2] == 'F' && h[3] == 'F'
                        && h[8] == 'W' && h[9] == 'E' && h[10] == 'B' && h[11] == 'P';
                default -> false;
            };
        } catch (IOException e) {
            return false;
        }
    }
}
