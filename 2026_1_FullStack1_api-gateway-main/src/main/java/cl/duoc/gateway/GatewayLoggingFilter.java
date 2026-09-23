package cl.duoc.gateway;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GatewayLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        System.out.println("====================================");
        System.out.println("Petición recibida en API Gateway");
        System.out.println("Método: " + request.getMethod());
        System.out.println("Ruta: " + request.getRequestURI());
        System.out.println("====================================");

        filterChain.doFilter(request, response);
    }
}
