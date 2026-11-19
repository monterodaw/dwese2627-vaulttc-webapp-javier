package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


/**
 * La clase {@code Game} representa un videojuego. Es el lado "1" de la relación 1:N:
 * un juego tiene muchos personajes ({@link java.lang.Character}).
 * <p>
 * Es equivalente a {@code Region} en el ejemplo Province → Region.
 * La relación está definida solo en el lado N (Character → Game), es decir, es unidireccional:
 * {@code Game} no conoce a sus personajes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "games")
public class Game {

    //Identificador único
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Nombre del juego
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    // Ruta/URL del logo
    @Column(name = "logo_url", length = 255)
    private String logoUrl;
}



