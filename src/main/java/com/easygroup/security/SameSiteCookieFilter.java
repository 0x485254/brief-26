package com.easygroup.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class SameSiteCookieFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SameSiteCookieFilter.class);

    @Value("${samesite.cookie.enabled:true}")
    private boolean sameSiteCookieEnabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);

        if (!sameSiteCookieEnabled) {
            return;
        }

        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        if (headers.isEmpty()) return;

        // Supprime tous les headers existants pour réécriture propre
        response.setHeader(HttpHeaders.SET_COOKIE, null);

        List<String> updatedHeaders = new ArrayList<>();

        for (String header : headers) {
            String updatedHeader = header;

            if (!updatedHeader.toLowerCase().contains("samesite")) {
                updatedHeader += "; SameSite=None";
            }

            if (!updatedHeader.toLowerCase().contains("secure")) {
                updatedHeader += "; Secure";
            }

            if (!updatedHeader.toLowerCase().contains("httponly")) {
                updatedHeader += "; HttpOnly";
            }

            updatedHeaders.add(updatedHeader);
        }

        for (String updated : updatedHeaders) {
            response.addHeader(HttpHeaders.SET_COOKIE, updated);
        }

        logger.debug("Applied SameSite=None; Secure; HttpOnly to {} cookies", updatedHeaders.size());
    }
}
