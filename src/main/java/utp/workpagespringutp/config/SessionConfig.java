package utp.workpagespringutp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionConfig {
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setCookiePath("/");
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        // ESTA es la línea mágica para que funcione entre Vercel y Render:
        serializer.setSameSite("None"); 
        serializer.setUseSecureCookie(true); // Requiere HTTPS (que ya tienes)
        return serializer;
    }
}