package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.services;

import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterCreateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterDetailDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Casos de uso del CRUD de Character. El controlador solo habla con esta interfaz.
 */
public interface CharacterService {

    Page<CharacterDTO> list(Pageable pageable);

    CharacterDetailDTO getDetail(Long id);

    CharacterUpdateDTO getForEdit(Long id);

    void create(CharacterCreateDTO dto);

    void update(CharacterUpdateDTO dto);

    void delete(Long id);
}
