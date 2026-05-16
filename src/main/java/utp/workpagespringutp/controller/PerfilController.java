package utp.workpagespringutp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utp.workpagespringutp.model.Usuario;
import utp.workpagespringutp.service.UsuarioService;

@Controller
@RequestMapping("/perfil")
public class PerfilController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    // Mostrar página de perfil
    @GetMapping
    public String mostrarPerfil(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/";
        }
        
        model.addAttribute("usuario", usuarioLogueado);
        return "html/perfil";
    }
    
    // Actualizar perfil
    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute Usuario usuario, 
                                    HttpSession session, 
                                    RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/";
        }
        
        // Mantener el ID y actualizar solo los campos editables
        usuario.setId(usuarioLogueado.getId());
        
        if (usuarioService.actualizarUsuario(usuario)) {
            // Actualizar la sesión con los nuevos datos
            session.setAttribute("usuarioLogueado", usuario);
            redirectAttributes.addFlashAttribute("mensajeExito", "Perfil actualizado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al actualizar el perfil");
        }
        
        return "redirect:/perfil";
    }
    
    // Eliminar cuenta
    @PostMapping("/eliminar")
    public String eliminarCuenta(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return "redirect:/";
        }
        
        if (usuarioService.eliminarCuenta(usuarioLogueado.getId())) {
            session.invalidate();
            redirectAttributes.addFlashAttribute("mensajeExito", "Cuenta eliminada exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar la cuenta");
        }
        
        return "redirect:/";
    }
}
