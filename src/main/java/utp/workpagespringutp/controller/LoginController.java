package utp.workpagespringutp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import utp.workpagespringutp.model.Usuario;
import utp.workpagespringutp.service.UsuarioService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

@RestController
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    public static class LoginRequest {
        private String username;
        private String password;
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @GetMapping("/login/status")
    public ResponseEntity<?> obtenerUsuarioLogueado() {
        Map<String, Object> response = new HashMap<>();
        // Leemos la autenticación que el Filtro acaba de armar gracias al Token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            String username = (String) auth.getPrincipal();
            Usuario usuarioLogueado = usuarioService.obtenerUsuario(username).orElse(null);
            
            if (usuarioLogueado != null) {
                response.put("autenticado", true);
                response.put("usuario", usuarioLogueado);
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("autenticado", false);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        Map<String, Object> response = new HashMap<>();
        if (usuarioService.registrarUsuario(usuario)) {
            response.put("success", true);
            response.put("message", "¡Usuario registrado exitosamente! Ya puedes iniciar sesión");
        } else {
            response.put("success", false);
            response.put("message", "El usuario ya existe. Intenta con otro nombre de usuario");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/iniciar")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginRequest loginRequest) {
        Map<String, Object> response = new HashMap<>();
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        if (usuarioService.iniciarSesion(username, password)) {
            Usuario usuario = usuarioService.obtenerUsuario(username).orElse(null);

            if (usuario != null) {
                // 1. Generamos el Token Ligero (username:rol en Base64)
                String plainToken = usuario.getUsername() + ":" + usuario.getRol();
                String token = Base64.getEncoder().encodeToString(plainToken.getBytes());

                // 2. Autenticamos en Spring Security
                String rol = "ROLE_" + usuario.getRol();
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rol);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        usuario.getUsername(), null, Collections.singletonList(authority));
                SecurityContextHolder.getContext().setAuthentication(auth);

                response.put("success", true);
                response.put("message", "¡Bienvenido " + username + "!");
                response.put("usuario", usuario);
                response.put("token", token); // <-- AQUÍ ENVIAMOS EL TOKEN AL FRONTEND
                return ResponseEntity.ok(response);
            }
        }

        response.put("success", false);
        response.put("message", "Usuario o contraseña incorrectos");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/cerrar")
    public ResponseEntity<?> cerrarSesion() {
        SecurityContextHolder.clearContext();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sesión cerrada exitosamente");
        return ResponseEntity.ok(response);
    }
}