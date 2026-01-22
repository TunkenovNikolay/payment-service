package org.example.payment_service_app.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.payment_service_app.exception.ExceptionMessageModel;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

@Component
public class PaymentAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response,
        @NonNull AuthenticationException authException) throws IOException {
        final ExceptionMessageModel re = new ExceptionMessageModel("Authentication failed");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        final OutputStream responseStream = response.getOutputStream();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(responseStream, re);
        responseStream.flush();
    }
}
