package com.surest.member_management.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidation_returnsBadRequestWithFieldErrors() {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "object");

        bindingResult.addError(new FieldError("object", "name", "Name is required"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<?> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();

        assertThat(body).containsEntry("name", "Name is required");
    }

    @Test
    void handleAccessDenied_returnsForbidden() {
        AccessDeniedException ex = new AccessDeniedException("Access denied");

        ResponseEntity<?> response = handler.handleAccessDenied(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();

        assertThat(body)
                .containsEntry("error", "Forbidden")
                .containsEntry("message", "You do not have permission to access this resource");
    }

    @Test
    void handleTypeMismatch_returnsBadRequest() {
        // Spring Boot 3 / Spring 6 constructor
        MethodArgumentTypeMismatchException ex =
                new MethodArgumentTypeMismatchException(
                        "abc",          // value
                        Integer.class,  // required type
                        "id",           // parameter name
                        null,
                        null// MethodParameter (can be null)
                );

        ResponseEntity<?> response = handler.handleTypeMismatch(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();

        assertThat(body)
                .containsEntry("error", "Invalid parameter")
                .containsEntry("message", "Parameter 'id' has invalid value 'abc'");
    }

    @Test
    void handleIllegalArgument_returnsConflict() {
        IllegalArgumentException ex = new IllegalArgumentException("Duplicate entry");

        ResponseEntity<?> response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();

        assertThat(body).containsEntry("error", "Duplicate entry");
    }

    @Test
    void handleNotFound_returnsNotFound() {
        RuntimeException ex = new RuntimeException("Resource not found");

        ResponseEntity<?> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();

        assertThat(body).containsEntry("error", "Resource not found");
    }

    @Test
    void handleAuthErrors_returnsUnauthorized() {
        BadCredentialsException ex = new BadCredentialsException("Invalid credentials");

        ResponseEntity<?> response = handler.handleAuthErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();

        assertThat(body)
                .containsEntry("error", "Unauthorized")
                .containsEntry("message", "Invalid credentials");
    }

    @Test
    void handleInternalServerError_returns500() {
        Exception ex = new Exception("Boom");

        ResponseEntity<?> response = handler.handleInternalServerError(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();

        assertThat(body)
                .containsEntry("error", "Internal Server Error")
                .containsEntry("message", "Something went wrong. Please try again later.");
    }
}
