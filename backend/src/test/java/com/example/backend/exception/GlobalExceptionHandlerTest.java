package com.example.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GlobalExceptionHandler — Testes Unitários")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleNotFound — Deve retornar 404 com corpo padronizado")
    void shouldReturn404WithStandardBody() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso não encontrado");

        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(404);
        assertThat(response.getBody().get("error")).isEqualTo("Not Found");
        assertThat(response.getBody().get("message")).isEqualTo("Recurso não encontrado");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    @DisplayName("handleDuplicateCode — Deve retornar 409 com mensagem de código duplicado")
    void shouldReturn409ForDuplicateCode() {
        DuplicateCodeException ex = new DuplicateCodeException("Raw material with code 'MP001' already exists.");

        ResponseEntity<Map<String, Object>> response = handler.handleDuplicateCode(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(409);
        assertThat(response.getBody().get("error")).isEqualTo("Conflict");
        assertThat(response.getBody().get("message")).isEqualTo("Raw material with code 'MP001' already exists.");
    }

    @Test
    @DisplayName("handleBadRequest — Deve retornar 400 com mensagem descritiva")
    void shouldReturn400ForBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid value");

        ResponseEntity<Map<String, Object>> response = handler.handleBadRequest(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(400);
        assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
        assertThat(response.getBody().get("message")).isEqualTo("Invalid value");
    }

    @Test
    @DisplayName("handleGeneral — Deve retornar 500 com mensagem genérica segura")
    void shouldReturn500WithSafeMessage() {
        Exception ex = new RuntimeException("Erro inesperado");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneral(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(500);
        assertThat(response.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(response.getBody().get("message")).isEqualTo("An unexpected error occurred. Please try again later.");
    }

    @Test
    @DisplayName("handleGeneral — Deve desembrulhar DuplicateCodeException aninhada e retornar 409")
    void shouldUnwrapNestedDuplicateCodeException() {
        DuplicateCodeException cause = new DuplicateCodeException("Raw material with code 'MP001' already exists.");
        Exception wrapper = new RuntimeException("Transaction failed", cause);

        ResponseEntity<Map<String, Object>> response = handler.handleGeneral(wrapper);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(409);
        assertThat(response.getBody().get("message")).isEqualTo("Raw material with code 'MP001' already exists.");
    }

    @Test
    @DisplayName("handleNotFound — Deve incluir timestamp como String no corpo da resposta")
    void shouldIncludeTimestamp() {
        ResourceNotFoundException ex = new ResourceNotFoundException("test");

        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertThat(response.getBody().get("timestamp")).isNotNull();
        assertThat(response.getBody().get("timestamp")).isInstanceOf(String.class);
    }
}

