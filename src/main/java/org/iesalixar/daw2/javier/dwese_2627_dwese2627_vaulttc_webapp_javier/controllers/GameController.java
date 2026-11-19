package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.controllers;

import jakarta.validation.Valid;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameCreateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.dtos.GameUpdateDTO;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.DuplicateResourceException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.ResourceInUseException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.exceptions.ResourceNotFoundException;
import org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Controlador del CRUD de Game. Mismo esquema que CharacterController:
 * Bean Validation con {@code @Valid} + {@link BindingResult}, y las excepciones del servicio
 * se convierten en errores de campo ({@code rejectValue}) o en mensajes flash.
 */
@Controller
@RequestMapping("/games")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private static final String FORM_VIEW = "views/game/game-form";
    private static final String LIST_VIEW = "views/game/game-list";
    private static final String DETAIL_VIEW = "views/game/game-detail";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private GameService gameService;

    // ------------------------------------------------------------------ LISTADO

    @GetMapping
    public String listGames(@RequestParam(name = "page", defaultValue = "0") int page,
                            @RequestParam(name = "size", defaultValue = "10") int size,
                            @RequestParam(name = "sortField", defaultValue = "name") String sortField,
                            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir,
                            Model model,
                            Locale locale) {
        logger.info("Listando juegos... page={}, size={}, sortField={}, sortDir={}", page, size, sortField, sortDir);

        if (page < 0) page = 0;
        if (size <= 0) size = 10;

        try {
            Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
            Page<GameDTO> games = gameService.list(PageRequest.of(page, size, sort));

            if (games.getTotalPages() > 0 && page >= games.getTotalPages()) {
                page = games.getTotalPages() - 1;
                games = gameService.list(PageRequest.of(page, size, sort));
            }

            model.addAttribute("listGames", games.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("totalPages", games.getTotalPages());
            model.addAttribute("totalElements", games.getTotalElements());
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", "asc".equalsIgnoreCase(sortDir) ? "desc" : "asc");
        } catch (Exception e) {
            logger.error("Error al listar los juegos: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", messageSource.getMessage("msg.game-controller.list.error", null, locale));
        }
        return LIST_VIEW;
    }

    // ------------------------------------------------------------------ DETALLE

    @GetMapping("/detail")
    public String showDetail(@RequestParam("id") Long id,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Mostrando detalle del juego con ID {}", id);
        try {
            model.addAttribute("game", gameService.getDetail(id));
            return DETAIL_VIEW;
        } catch (ResourceNotFoundException ex) {
            return redirectWithError("msg.game-controller.notFound", locale, redirectAttributes);
        } catch (Exception e) {
            logger.error("Error al obtener el detalle del juego {}: {}", id, e.getMessage(), e);
            return redirectWithError("msg.game-controller.detail.error", locale, redirectAttributes);
        }
    }

    // ------------------------------------------------------------------ ALTA

    @GetMapping("/new")
    public String showNewForm(Model model) {
        logger.info("Mostrando formulario para nuevo juego.");
        model.addAttribute("game", new GameCreateDTO());
        return FORM_VIEW;
    }

    @PostMapping("/insert")
    public String insertGame(@Valid @ModelAttribute("game") GameCreateDTO gameDTO,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Insertando nuevo juego '{}'", gameDTO.getName());

        if (result.hasErrors()) {
            return FORM_VIEW;
        }

        try {
            gameService.create(gameDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.game-controller.insert.success", null, locale));
            return "redirect:/games";
        } catch (DuplicateResourceException ex) {
            result.rejectValue("name", "msg.game.name.duplicate");
        } catch (Exception e) {
            logger.error("Error al insertar el juego '{}': {}", gameDTO.getName(), e.getMessage(), e);
            model.addAttribute("errorMessage", messageSource.getMessage("msg.game-controller.insert.error", null, locale));
        }
        return FORM_VIEW;
    }

    // ------------------------------------------------------------------ EDICIÓN

    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") Long id,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               Locale locale) {
        logger.info("Mostrando formulario de edición para el juego con ID {}", id);
        try {
            model.addAttribute("game", gameService.getForEdit(id));
            return FORM_VIEW;
        } catch (ResourceNotFoundException ex) {
            return redirectWithError("msg.game-controller.notFound", locale, redirectAttributes);
        } catch (Exception e) {
            logger.error("Error al obtener el juego con ID {}: {}", id, e.getMessage(), e);
            return redirectWithError("msg.game-controller.edit.error", locale, redirectAttributes);
        }
    }

    @PostMapping("/update")
    public String updateGame(@Valid @ModelAttribute("game") GameUpdateDTO gameDTO,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Actualizando juego con ID {}", gameDTO.getId());

        if (result.hasErrors()) {
            return FORM_VIEW;
        }

        try {
            gameService.update(gameDTO);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.game-controller.update.success", null, locale));
            return "redirect:/games";
        } catch (DuplicateResourceException ex) {
            result.rejectValue("name", "msg.game.name.duplicate");
        } catch (ResourceNotFoundException ex) {
            return redirectWithError("msg.game-controller.notFound", locale, redirectAttributes);
        } catch (Exception e) {
            logger.error("Error al actualizar el juego con ID {}: {}", gameDTO.getId(), e.getMessage(), e);
            model.addAttribute("errorMessage", messageSource.getMessage("msg.game-controller.update.error", null, locale));
        }
        return FORM_VIEW;
    }

    // ------------------------------------------------------------------ BORRADO

    @PostMapping("/delete")
    public String deleteGame(@RequestParam("id") Long id,
                             RedirectAttributes redirectAttributes,
                             Locale locale) {
        logger.info("Eliminando juego con ID {}", id);
        try {
            gameService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    messageSource.getMessage("msg.game-controller.delete.success", null, locale));
            return "redirect:/games";
        } catch (ResourceNotFoundException ex) {
            return redirectWithError("msg.game-controller.notFound", locale, redirectAttributes);
        } catch (ResourceInUseException ex) {
            return redirectWithError("msg.game-controller.delete.inUse", locale, redirectAttributes);
        } catch (Exception e) {
            logger.error("Error al eliminar el juego con ID {}: {}", id, e.getMessage(), e);
            return redirectWithError("msg.game-controller.delete.error", locale, redirectAttributes);
        }
    }

    // ------------------------------------------------------------------ auxiliares

    private String redirectWithError(String messageKey, Locale locale, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage(messageKey, null, locale));
        return "redirect:/games";
    }
}
