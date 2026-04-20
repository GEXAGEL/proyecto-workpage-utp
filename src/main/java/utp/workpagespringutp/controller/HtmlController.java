package utp.workpagespringutp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HtmlController {

    @GetMapping({"/", "/index"})
    public String home() {
        return "html/index";
    }

    @GetMapping("/productos")
    public String productos() {
        return "html/productos";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "html/contacto";
    }
}

