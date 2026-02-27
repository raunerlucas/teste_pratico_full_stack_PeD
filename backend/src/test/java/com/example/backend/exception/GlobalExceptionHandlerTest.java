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
    @DisplayName("handleGeneral — Deve retornar 500 com corpo padronizado")
    void shouldReturn500WithStandardBody() {
        Exception ex = new RuntimeException("Erro inesperado");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneral(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo(500);
        assertThat(response.getBody().get("error")).isEqualTo("Internal Server Error");
        assertThat(response.getBody().get("message")).isEqualTo("Erro inesperado");
        assertThat(response.getBody()).containsKey("timestamp");
    }

    @Test
    @DisplayName("handleNotFound — Deve incluir timestamp no corpo da resposta")
    void shouldIncludeTimestamp() {
        ResourceNotFoundException ex = new ResourceNotFoundException("test");

        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(ex);

        assertThat(response.getBody().get("timestamp")).isNotNull();
    }
}

