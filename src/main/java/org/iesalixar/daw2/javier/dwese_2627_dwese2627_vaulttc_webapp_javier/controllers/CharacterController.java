package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterCreateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterDetailDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.CharacterUpdateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.InvalidFileException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.services.CharacterService;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Controlador del CRUD de Character. Solo se ocupa de lo "web":
 * recibir la petición, validar el formulario, llamar al servicio, capturar excepciones,
 * preparar el modelo y elegir la vista o el redirect.
 * <p>
 * Hay dos tipos de validación:
 * <ol>
 *   <li>Bean Validation en el DTO ({@code @Valid} + {@link BindingResult}): campos vacíos, longitudes...</li>
 *   <li>Validaciones propias que lanza el servicio (nombre duplicado en el juego, juego inexistente,
 *       imagen inválida). Aquí se capturan y se convierten en errores del campo concreto con
 *       {@code result.rejectValue(...)}, de modo que el formulario se vuelve a mostrar con los datos escritos.</li>
 * </ol>
 */
@Controller
@RequestMapping("/characters")
public class CharacterController {

    private static final Logger logger = LoggerFactory.getLogger(CharacterController.class);

    private static final String FORM_VIEW = "views/character/character-form";
    private static final String LIST_VIEW = "views/character/character-list";
    private static final String DETAIL_VIEW = "views/character/character-detail";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CharacterService characterService;

    // Solo para rellenar el <select> de juegos
    @Autowired
    private GameService gameService;

    // ------------------------------------------------------------------ LISTADO

    @GetMapping
    public String listCharacters(@RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                 @RequestParam(name = "sortField", defaultValue = "name") String sortField,
                                 @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
                                 Model model,
                                 Locale locale) {
        logger.info("Listando personajes... page={}, size={}, sortField={}, sortDir={}", page, size, sortField, sortDir);

        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        try {
            Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
            Page<CharacterDTO> characters = characterService.list(PageRequest.of(page, size, sort));

            // Si se pide una página fuera de rango, se vuelve a pedir la última
            if (characters.getTotalPages() > 0 && page >= characters.getTotalPages()) {
                page = characters.getTotalPages() - 1;
                characters = characterService.list(PageRequest.of(page, size, sort));
            }

            model.addAttribute("listCharacters", characters.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", characters.getTotalPages());
            model.addAttribute("totalElements", characters.getTotalElements());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", "asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");
        } catch (Exception e) {
            logger.error("Error al listar los personajes: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", messageSource.getMessage("msg.character-controller.list.error", null, locale));
        }
        return LIST_VIEW;
    }

    // ------------------------------------------------------------------ DETALLE

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Mostrando detalle del personaje con ID {}", id);
        try {
            CharacterDetailDTO character = characterService.getDetail(id);
            model.addAttribute("character", character);
            return DETAIL_VIEW;
        } catch (ResourceNotFoundException ex) {
            return redirectWithError("msg.character-controller.notFound", locale, redirectAttributes);
        } catch (Exception e) {
            logger.error("Error al obtener el detalle del personaje {}: {}", id, e.getMessage(), e);
            return redirectWithError("msg.character-controller.detail.error", locale, redirectAttributes);
        }
    }

    // ------------------------------------------------------------------ ALTA

    @GetMapping("/new")
    public String showNewForm(Model model, Locale locale) {
        logger.info("Mostrando formulario para nuevo personaje.");
        model.addAttribute("character", new CharacterCreateDTO());
        loadGames(model, locale);
        return FORM_VIEW;
    }

    @PostMapping("/insert")
    public String insertCharacter(@Valid @ModelAttribute("character") CharacterCreateDTO characterDTO,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes,
                                  Locale locale) {
        logger.info("Insertando nuevo personaje '{}'", characterDTO.getName());

        // 1) Errores de Bean Validation: se vuelve al formulario sin perder lo escrito
        if (result.hasErrors()) {
            loadGames(model, locale);
            return FORM_VIEW;
        }

        try {
            // 2) El servicio aplica las reglas de negocio y guarda
            characterService.create(characterDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.character-controller.insert.success", null, locale));
            return "redirect:/characters";
        } catch (DuplicateResourceException ex) {
            result.rejectValue("name", "msg.character.name.duplicate");
        } catch (ResourceNotFoundException ex) {
            result.rejectValue("gameId", "msg.character.gameId.notFound");
        } catch (InvalidFileException ex) {
            result.rejectValue("imageFile", ex.getMessageKey());
        } catch (Exception e) {
            logger.error("Error al insertar el personaje '{}': {}", characterDTO.getName(), e.getMessage(), e);
            model.addAttribute("errorMessage", messageSource.getMessage("msg.character-controller.insert.error", null, locale));
        }
        loadGames(model, locale);
        return FORM_VIEW;
    }

    // ------------------------------------------------------------------ EDICIÓN

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {
        logger.info("Mostrando formulario de edición para el personaje con ID {}", id);
        try {
            model.addAttribute("character", characterService.getForEdit(id));
            loadGames(model, locale);
            return FORM_VIEW;
        } catch (ResourceNotFoundException ex) {
            return redirectWithError("msg.character-controller.notFound", locale, redirectAttributes);
        } catch (Exception e) {
            logger.error("Error al obtener el personaje con ID {}: {}", id, e.getMessage(), e);
            return redirectWithError("msg.character-controller.edit.error", locale, redirectAttributes);
        }
    }

    @PostMapping("/update")
    public String updateCharacter(@Valid @ModelAttribute("character") CharacterUpdateDTO characterDTO,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes,
                                  Locale locale) {
        logger.info("Actualizando personaje con ID {}", characterDTO.getId());

        if (result.hasErrors()) {
            loadGames(model, locale);
            return FORM_VIEW;
        }

        try {
            characterService.update(characterDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.character-controller.update.success", null, locale));
            return "redirect:/characters";
        } catch (DuplicateResourceException ex) {
            result.rejectValue("name", "msg.character.name.duplicate");
        } catch (InvalidFileException ex) {
            result.rejectValue("imageFile", ex.getMessageKey());
        } catch (ResourceNotFoundException ex) {
            // Puede ser que el personaje ya no exista, o que el juego elegido no exista
            if ("game".equals(ex.getResource())) {
                result.rejectValue("gameId", "msg.character.gameId.notFound");
            } else {
                return redirectWithError("msg.character-controller.notFound", locale, redirectAttributes);
            }
        } catch (Exception e) {
            logger.error("Error al actualizar el personaje con ID {}: {}", characterDTO.getId(), e.getMessage(), e);
            model.addAttribute("errorMessage", messageSource.getMessage("msg.character-controller.update.error", null, locale));
        }
        loadGames(model, locale);
        return FORM_VIEW;
    }

    // ------------------------------------------------------------------ BORRADO

    @PostMapping("/delete")
    public String deleteCharacter(@RequestParam("id") Long id,
                                  RedirectAttributes redirectAttributes,
                                  Locale locale) {
        logger.info("Eliminando personaje con ID {}", id);
        try {
            characterService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.character-controller.delete.success", null, locale));
            return "redirect:/characters";
        } catch (ResourceNotFoundException ex) {
            return redirectWithError("msg.character-controller.notFound", locale, redirectAttributes);
        } catch (Exception e) {
            logger.error("Error al eliminar el personaje con ID {}: {}", id, e.getMessage(), e);
            return redirectWithError("msg.character-controller.delete.error", locale, redirectAttributes);
        }
    }

    // ------------------------------------------------------------------ auxiliares

    /** Carga en el modelo la lista de juegos para el {@code <select>} del formulario. */
    private void loadGames(Model model, Locale locale) {
        try {
            model.addAttribute("listGames", gameService.listAll());
        } catch (Exception e) {
            logger.error("Error al cargar los juegos para el formulario: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", messageSource.getMessage("msg.character-controller.games.error", null, locale));
        }
    }

    private String redirectWithError(String messageKey, Locale locale, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage(messageKey, null, locale));
        return "redirect:/characters";
    }
}
