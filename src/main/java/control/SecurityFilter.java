package control;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inizializzazione del filtro (se necessario)
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        
        // Aggiungi gli header per la Content Security Policy (CSP)
        response.setHeader("Content-Security-Policy", "frame-ancestors 'none'");

        // Aggiungi l'header X-Frame-Options per la retrocompatibilità con browser meno recenti
        response.setHeader("X-Frame-Options", "DENY");

        // Permetti l'esecuzione delle altre catene di filtri
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        // Pulizia delle risorse (se necessario)
    }
}