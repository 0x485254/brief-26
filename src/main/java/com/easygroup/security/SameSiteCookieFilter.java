package com.easygroup.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
public class SameSiteCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean first = true;

        for (String header : headers) {
            String updatedHeader = header + "; SameSite=Lax";
            if (first) {
                response.setHeader(HttpHeaders.SET_COOKIE, updatedHeader);
                first = false;
            } else {
                response.addHeader(HttpHeaders.SET_COOKIE, updatedHeader);
            }
        }
    }
}
