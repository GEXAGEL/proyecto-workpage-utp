package utp.workpagespringutp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == 403) {
                model.addAttribute("errorTitle", "Acceso Denegado");
                model.addAttribute("errorMessage", "No tienes permisos para acceder a esta página.");
                model.addAttribute("errorCode", "403");
                return "error/403";
            } else if (statusCode == 404) {
                model.addAttribute("errorTitle", "Página no encontrada");
                model.addAttribute("errorMessage", "La página que buscas no existe.");
                model.addAttribute("errorCode", "404");
                return "error/404";
            }
        }

        model.addAttribute("errorTitle", "Error");
        model.addAttribute("errorMessage", "Ha ocurrido un error inesperado.");
        model.addAttribute("errorCode", "500");
        return "error/error";
    }
}