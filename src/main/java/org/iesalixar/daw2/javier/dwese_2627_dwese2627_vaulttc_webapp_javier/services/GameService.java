package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.services;


import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameCreateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameDetailDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;


/** Casos de uso de Game que necesita el CRUD de Character (rellenar el select). */
public interface GameService {

    Page<GameDTO> list(Pageable pageable);

    /** Todos los juegos (sin paginar): lo usa el select del formulario de Character. */
    List<GameDTO> listAll();

    GameDetailDTO getDetail(Long id);

    GameUpdateDTO getForEdit(Long id);

    void create(GameCreateDTO dto);

    void update(GameUpdateDTO dto);

    void delete(Long id);
}
