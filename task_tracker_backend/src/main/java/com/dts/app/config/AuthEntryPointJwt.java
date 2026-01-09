package com.dts.app.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;

/** 
 * Custom implementation of {@link AuthenticationEntryPoint}
 * Class is triggered whenever an unathenticated user tries to access a protected resources
 * Handles Unauthorized error response 
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    /**
     * triggers when {@link AuthenticationException} throwm
     * @param request - {@link HttpServletRequest} being processed 
     * @param response -  {@link HttpServletResponse} to send the error through
     * @param authException - exception that caused the invocation
     * @throws IOException - If an input or output exception occurs
     * @throws ServletException - If the request can't be handled 
     */
    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {
        //send 401 Unauthorized status and error message to client 
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorised");
    }
    
}
