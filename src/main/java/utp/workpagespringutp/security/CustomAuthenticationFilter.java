package utp.workpagespringutp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import utp.workpagespringutp.model.Usuario;

import java.io.IOException;
import java.util.Collections;

public class CustomAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        // Si existe sesión HTTP y hay un usuario logueado
        if (session != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

            // Si hay usuario en sesión pero no hay autenticación en Spring Security
            if (usuario != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Crear la autoridad con el prefijo ROLE_
                String rol = "ROLE_" + usuario.getRol();
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rol);

                // Crear el token de autenticación
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                usuario.getUsername(),
                                null,
                                Collections.singletonList(authority)
                        );

                // Establecer la autenticación en el contexto de Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}