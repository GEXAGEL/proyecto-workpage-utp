package utp.workpagespringutp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import utp.workpagespringutp.model.Usuario;
import utp.workpagespringutp.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registrar un nuevo usuario
    public boolean registrarUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            return false;
        }
        // Encriptar la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol("CLIENTE"); // Por defecto es CLIENTE
        usuarioRepository.save(usuario);
        return true;
    }

    // Iniciar sesión
    public boolean iniciarSesion(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Si la contraseña no está encriptada (usuarios antiguos)
            if (!usuario.getPassword().startsWith("$2a$")) {
                // Comparar directamente
                if (usuario.getPassword().equals(password)) {
                    // Encriptar la contraseña para la próxima vez
                    usuario.setPassword(passwordEncoder.encode(password));
                    usuarioRepository.save(usuario);
                    return true;
                }
                return false;
            }

            // Si la contraseña ya está encriptada
            return passwordEncoder.matches(password, usuario.getPassword());
        }
        return false;
    }

    // Obtener un usuario por username
    public Optional<Usuario> obtenerUsuario(String username) {
        return usuarioRepository.findByUsername(username);
    }

    // Actualizar usuario
    public boolean actualizarUsuario(Usuario usuario) {
        if (usuarioRepository.existsById(usuario.getId())) {
            Optional<Usuario> usuarioExistente = usuarioRepository.findById(usuario.getId());
            if (usuarioExistente.isPresent()) {
                Usuario userActual = usuarioExistente.get();

                // Mantener el rol actual
                usuario.setRol(userActual.getRol());

                // Solo encriptar si la contraseña cambió
                if (!usuario.getPassword().equals(userActual.getPassword())) {
                    // Si la nueva contraseña no está encriptada
                    if (!usuario.getPassword().startsWith("$2a$")) {
                        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                    }
                } else {
                    usuario.setPassword(userActual.getPassword());
                }
            }
            usuarioRepository.save(usuario);
            return true;
        }
        return false;
    }

    // Eliminar cuenta
    public boolean eliminarCuenta(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Obtener todos los usuarios (para admin)
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Verificar si un usuario es admin
    public boolean esAdmin(String username) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        return usuario.isPresent() && "ADMIN".equals(usuario.get().getRol());
    }

    // Cambiar rol de usuario
    public void cambiarRol(Long id, String nuevoRol) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setRol(nuevoRol);
            usuarioRepository.save(usuario);
        }
    }
}