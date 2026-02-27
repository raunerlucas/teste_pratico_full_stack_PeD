package com.example.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ResourceNotFoundException — Testes Unitários")
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("Deve armazenar a mensagem passada no construtor")
    void shouldStoreMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Item not found");

        assertThat(ex.getMessage()).isEqualTo("Item not found");
    }

    @Test
    @DisplayName("Deve ser uma RuntimeException")
    void shouldBeRuntimeException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("test");

        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Deve ser capturável como RuntimeException")
    void shouldBeCatchableAsRuntimeException() {
        assertThatThrownBy(() -> {
            throw new ResourceNotFoundException("Recurso X não encontrado");
        })
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Recurso X não encontrado");
    }
}

