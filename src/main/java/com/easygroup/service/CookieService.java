package com.easygroup.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for handling HTTP cookies for authentication.
 */
@Service
public class CookieService {

    @Value("${cookie.jwt.name:JWT_TOKEN}")
    private String cookieName;

    @Value("${cookie.jwt.expiration:86400}") // 24 hours in seconds
    private int cookieMaxAge;

    @Value("${cookie.jwt.secure:true}")
    private boolean secure;

    @Value("${cookie.jwt.http-only:true}")
    private boolean httpOnly;

    @Value("${cookie.jwt.path:/}")
    private String path;

    @Value("${cookie.jwt.domain:}")
    private String domain;

    @Value("${cookie.jwt.same-site:Strict}")
    private String sameSite;

    /**
     * Create a cookie with the JWT token.
     *
     * @param token the JWT token
     * @return the cookie
     */
    public Cookie createCookie(String token) {
        Cookie cookie = new Cookie(cookieName, token);
        cookie.setMaxAge(cookieMaxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath(path);
        
        if (!domain.isEmpty()) {
            cookie.setDomain(domain);
        }
        
        return cookie;
    }

    /**
     * Add a JWT token cookie to the response.
     *
     * @param response the HTTP response
     * @param token the JWT token
     */
    public void addTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = createCookie(token);
        response.addCookie(cookie);
        
        // Set SameSite attribute (not directly supported by javax.servlet.http.Cookie)
        String cookieHeader = String.format("%s=%s; Max-Age=%d; Path=%s; HttpOnly=%b; Secure=%b; SameSite=%s", 
                cookieName, token, cookieMaxAge, path, httpOnly, secure, sameSite);
        if (!domain.isEmpty()) {
            cookieHeader += "; Domain=" + domain;
        }
        response.setHeader("Set-Cookie", cookieHeader);
    }

    /**
     * Clear the JWT token cookie.
     *
     * @param response the HTTP response
     */
    public void clearTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(secure);
        cookie.setPath(path);
        
        if (!domain.isEmpty()) {
            cookie.setDomain(domain);
        }
        
        response.addCookie(cookie);
        
        // Set SameSite attribute
        String cookieHeader = String.format("%s=; Max-Age=0; Path=%s; HttpOnly=%b; Secure=%b; SameSite=%s", 
                cookieName, path, httpOnly, secure, sameSite);
        if (!domain.isEmpty()) {
            cookieHeader += "; Domain=" + domain;
        }
        response.setHeader("Set-Cookie", cookieHeader);
    }

    /**
     * Get the JWT token from the request cookies.
     *
     * @param request the HTTP request
     * @return the JWT token, or null if not found
     */
    public String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Get the cookie name.
     *
     * @return the cookie name
     */
    public String getCookieName() {
        return cookieName;
    }
}