package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * La clase {@code Character} representa un personaje de un videojuego. Es la entidad principal del CRUD
 * y el lado "N" de la relación 1:N: muchos personajes pertenecen a un único {@link Game}.
 * <p>
 * Es equivalente a {@code Province} en el ejemplo Province → Region:
 * <ul>
 *   <li>{@code game} es el objeto {@link Game} al que pertenece (en BD es la clave foránea {@code game_id}).</li>
 *   <li>{@code imageUrl} guarda la ruta relativa de la imagen subida (p. ej. "characters/ab12.png").</li>
 * </ul>
 * Nota: dentro de este paquete, {@code Character} tiene prioridad sobre {@code java.lang.Character}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "characters")
public class Character {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "name",nullable = false,length = 100)
    private String name;

    @Column(name = "description",length = 500)
    private String description;

    @Column(name = "image_url",length = 255)
    private String imageUrl;



    /**
     * Parte N de la relación 1:N.
     * <ul>
     *   <li>{@code @ManyToOne}: muchos personajes -> un juego.</li>
     *   <li>{@code fetch = LAZY}: el juego no se carga de BD hasta que se necesita
     *       (en los listados lo forzamos con JOIN FETCH para evitar consultas extra).</li>
     *   <li>{@code @JoinColumn}: indica la columna FK de la tabla 'characters'.</li>
     * </ul>
     * {@code @ToString.Exclude} evita que {@code toString()} dispare la carga perezosa del juego.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "game_id",nullable = false)
    @ToString.Exclude
    private Game game;

    /** Constructor de conveniencia sin {@code id} (se autogenera en BD). */
    public Character(String name, String description, String imageURL, Game game){
        this.name = name;
        this.description = description;
        this.imageUrl = imageURL;
        this.game = game;
    }
}


