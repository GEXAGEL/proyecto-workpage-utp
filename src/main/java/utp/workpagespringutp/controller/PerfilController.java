package utp.workpagespringutp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utp.workpagespringutp.model.Usuario;
import utp.workpagespringutp.service.UsuarioService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/perfil")
public class PerfilController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    // Mostrar página de perfil (retornar datos del usuario)
    @GetMapping
    public ResponseEntity<?> mostrarPerfil(HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No has iniciado sesión");
        }
        
        return ResponseEntity.ok(usuarioLogueado);
    }
    
    // Actualizar perfil
    @PostMapping("/actualizar")
    public ResponseEntity<?> actualizarPerfil(@RequestBody Usuario usuario, HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No has iniciado sesión");
        }
        
        // Mantener el ID y actualizar solo los campos editables
        usuario.setId(usuarioLogueado.getId());
        // El rol y la contraseña no deberían cambiarse así directamente en perfil, o si no se especifican se mantienen
        if (usuario.getRol() == null) {
            usuario.setRol(usuarioLogueado.getRol());
        }
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            usuario.setPassword(usuarioLogueado.getPassword());
        }
        
        Map<String, Object> response = new HashMap<>();
        if (usuarioService.actualizarUsuario(usuario)) {
            // Actualizar la sesión con los nuevos datos
            session.setAttribute("usuarioLogueado", usuario);
            response.put("success", true);
            response.put("message", "Perfil actualizado correctamente");
            response.put("usuario", usuario);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Error al actualizar el perfil");
            return ResponseEntity.ok(response);
        }
    }
    
    // Eliminar cuenta
    @PostMapping("/eliminar")
    public ResponseEntity<?> eliminarCuenta(HttpSession session) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuarioLogueado == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No has iniciado sesión");
        }
        
        Map<String, Object> response = new HashMap<>();
        if (usuarioService.eliminarCuenta(usuarioLogueado.getId())) {
            session.invalidate();
            response.put("success", true);
            response.put("message", "Cuenta eliminada exitosamente");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Error al eliminar la cuenta");
            return ResponseEntity.ok(response);
        }
    }
}
