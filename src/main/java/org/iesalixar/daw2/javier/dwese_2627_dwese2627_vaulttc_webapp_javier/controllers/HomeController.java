package org.iesalixar.daw2.javier.dwese_2627_dwese2627_vaulttc_webapp_javier.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }
}
