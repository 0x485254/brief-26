package com.easygroup.security.cookie;

import com.easygroup.service.CustomUserDetailsService;
import com.easygroup.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter for cookie-based JWT authentication.
 * Intercepts requests and validates JWT tokens from cookies.
 */
@Component
public class CookieAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final CookieService cookieService;

    @Autowired
    public CookieAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            CookieService cookieService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.cookieService = cookieService;
    }

    /**
     * Filter incoming requests and validate JWT tokens from cookies.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet exception occurs
     * @throws IOException if an I/O exception occurs
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Extract JWT token from cookies
            String jwt = cookieService.getTokenFromCookies(request);
            
            // If token exists, validate it
            if (jwt != null) {
                // Extract username from JWT token
                String userEmail = jwtService.extractUsername(jwt);
                
                // If username exists and no authentication exists in SecurityContext
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Load user details
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    
                    // Validate token
                    if (jwtService.validateToken(jwt, userDetails)) {
                        // Create authentication token
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        
                        // Set authentication details
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // Set authentication in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
        } catch (Exception e) {
            // Log exception but don't throw it to allow the request to continue
            logger.error("Could not set user authentication in security context", e);
        }
        
        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}