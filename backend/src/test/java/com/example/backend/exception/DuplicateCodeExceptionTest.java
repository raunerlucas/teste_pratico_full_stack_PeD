package com.example.backend.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DuplicateCodeException — Testes Unitários")
class DuplicateCodeExceptionTest {

    @Test
    @DisplayName("Deve armazenar a mensagem passada no construtor")
    void shouldStoreMessage() {
        DuplicateCodeException ex = new DuplicateCodeException("Code already exists");

        assertThat(ex.getMessage()).isEqualTo("Code already exists");
    }

    @Test
    @DisplayName("Deve ser uma RuntimeException")
    void shouldBeRuntimeException() {
        DuplicateCodeException ex = new DuplicateCodeException("test");

        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Deve ser capturável como RuntimeException")
    void shouldBeCatchableAsRuntimeException() {
        assertThatThrownBy(() -> {
            throw new DuplicateCodeException("Código MP001 já existe");
        })
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Código MP001 já existe");
    }
}

