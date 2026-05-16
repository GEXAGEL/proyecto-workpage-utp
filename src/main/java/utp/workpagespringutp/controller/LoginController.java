package utp.workpagespringutp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import utp.workpagespringutp.model.Usuario;
import utp.workpagespringutp.service.UsuarioService;

import java.util.Collections;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    // Procesar registro
    @PostMapping("/login/registrar")
    public String registrar(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        if (usuarioService.registrarUsuario(usuario)) {
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Usuario registrado exitosamente! Ya puedes iniciar sesión");
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "El usuario ya existe. Intenta con otro nombre de usuario");
        }
        return "redirect:/";
    }

    // Procesar inicio de sesión
    @PostMapping("/login/iniciar")
    public String iniciarSesion(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        if (usuarioService.iniciarSesion(username, password)) {
            Usuario usuario = usuarioService.obtenerUsuario(username).orElse(null);

            if (usuario != null) {

                // 1. Guardar en sesión HTTP
                session.setAttribute("usuarioLogueado", usuario);

                // 2. Autenticar en Spring Security
                String rol = "ROLE_" + usuario.getRol();
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rol);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        usuario.getUsername(),
                        null,
                        Collections.singletonList(authority)
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

                redirectAttributes.addFlashAttribute("mensajeExito", "¡Bienvenido " + username + "!");
            }
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("mensajeError", "Usuario o contraseña incorrectos");
            return "redirect:/";
        }
    }

    // Cerrar sesión
    @GetMapping("/login/cerrar")
    public String cerrarSesion(HttpSession session, RedirectAttributes redirectAttributes) {

        // Limpiar Spring Security
        SecurityContextHolder.clearContext();

        // Invalidar sesión HTTP
        if (session != null) {
            session.invalidate();
        }

        redirectAttributes.addFlashAttribute("mensajeExito", "Sesión cerrada exitosamente");
        return "redirect:/";
    }
}