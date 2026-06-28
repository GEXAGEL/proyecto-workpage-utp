package utp.workpagespringutp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import utp.workpagespringutp.model.Usuario;
import utp.workpagespringutp.service.UsuarioService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    // DTO para el inicio de sesión
    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    // Obtener estado de la sesión actual
    @GetMapping("/login/status")
    public ResponseEntity<?> obtenerUsuarioLogueado(HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        Map<String, Object> response = new HashMap<>();
        if (usuarioLogueado != null) {
            response.put("autenticado", true);
            response.put("usuario", usuarioLogueado);
            return ResponseEntity.ok(response);
        }
        response.put("autenticado", false);
        return ResponseEntity.ok(response);
    }

    // Procesar registro
    @PostMapping("/login/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        Map<String, Object> response = new HashMap<>();
        if (usuarioService.registrarUsuario(usuario)) {
            response.put("success", true);
            response.put("message", "¡Usuario registrado exitosamente! Ya puedes iniciar sesión");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "El usuario ya existe. Intenta con otro nombre de usuario");
            return ResponseEntity.ok(response);
        }
    }

    // Procesar inicio de sesión
    @PostMapping("/login/iniciar")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequest loginRequest, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

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

                response.put("success", true);
                response.put("message", "¡Bienvenido " + username + "!");
                response.put("usuario", usuario);
                return ResponseEntity.ok(response);
            }
        }

        response.put("success", false);
        response.put("message", "Usuario o contraseña incorrectos");
        return ResponseEntity.ok(response);
    }

    // Cerrar sesión
    @PostMapping("/login/cerrar")
    public ResponseEntity<?> cerrarSesion(HttpSession session) {
        // Limpiar Spring Security
        SecurityContextHolder.clearContext();

        // Invalidar sesión HTTP
        if (session != null) {
            session.invalidate();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sesión cerrada exitosamente");
        return ResponseEntity.ok(response);
    }
}