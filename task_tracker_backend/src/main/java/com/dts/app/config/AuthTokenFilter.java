package com.dts.app.config;

import com.dts.app.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filter that executes once per request to validate JWT tokens
 * Extracts  token from the Authorization header, validates it, 
 * and sets the security context for the application
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtils;
    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Core filter logic to intercept requests and verify JWT integrity.
     * * @param request - incoming request.
     * @param response - outgoing response.
     * @param filterChain - chain of other security filters.
     * @throws ServletException - If  request fails for a servlet-related reason
     * @throws IOException - If an I/O error occurs during filtering.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                String username = jwtUtils.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                //create auth token using user details (and roles when relevant)
                UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //finalise authentication by putting in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            System.out.println("cannot set authentication : " + e);
        }
        filterChain.doFilter(request, response);
    }
    /**
     * Parses the Authorization header to extract  raw JWT string
     * * @param request - current HTTP request
     * @return - extracted JWT string if valid, otherwise null
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    
}
