package utp.workpagespringutp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

public class CustomAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Atrapamos el Token que envía el Interceptor de Angular
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7); // Quitamos "Bearer "
                
                // 2. Decodificamos el Token Ligero
                byte[] decodedBytes = Base64.getDecoder().decode(token);
                String decodedString = new String(decodedBytes);
                String[] parts = decodedString.split(":"); // Separamos username y rol
                
                if (parts.length == 2) {
                    String username = parts[0];
                    String rol = "ROLE_" + parts[1];

                    // 3. Le decimos a Spring Security "¡Este usuario tiene permiso!"
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rol);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, Collections.singletonList(authority));
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Si el token está mal formado, simplemente lo ignoramos y Spring Security bloqueará la ruta
            }
        }

        filterChain.doFilter(request, response);
    }
}