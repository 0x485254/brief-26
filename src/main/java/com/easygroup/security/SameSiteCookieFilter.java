package com.easygroup.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
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



        Collection<String> headers = response.getHeaders(HttpHeaders.SET_COOKIE);
        boolean first = true;

        for (String header : headers) {
            String updatedHeader = header + "; SameSite=None";
            if (first) {
                response.setHeader(HttpHeaders.SET_COOKIE, updatedHeader);
                first = false;
            } else {
                response.addHeader(HttpHeaders.SET_COOKIE, updatedHeader);
            }
        }

        if (!headers.isEmpty()) {
            logger.debug("Applied SameSite=Lax attribute to {} cookies", headers.size());
        }
    }
}
