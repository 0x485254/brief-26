package com.easygroup.config;

import com.easygroup.util.JwtUtils;
import com.easygroup.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

@Override
protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {

    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        filterChain.doFilter(request, response);
        return;
    }

    String token = authHeader.substring(7);
    String email;

    try {
        email = jwtUtils.getEmailFromToken(token);
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"JWT expired\"}");
        return;
    } catch (io.jsonwebtoken.JwtException e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Invalid token\"}");
        return;
    }

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        userRepository.findByEmail(email).ifPresent(user -> {
            var authToken = new UsernamePasswordAuthenticationToken(
                    user, null, null
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        });
    }

    filterChain.doFilter(request, response);
}

}
