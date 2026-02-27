package com.example.backend.service;

import com.example.backend.dto.RawMaterialDTO;
import com.example.backend.entity.RawMaterial;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.RawMaterialRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RawMaterialService — Testes Unitários")
class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository repository;

    @InjectMocks
    private RawMaterialService service;

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private RawMaterial buildRawMaterial(Long id, String code, String name, Double stock) {
        return RawMaterial.builder()
                .id(id)
                .code(code)
                .name(name)
                .stockQuantity(stock)
                .build();
    }

    private RawMaterialDTO buildDTO(String code, String name, Double stock) {
        return RawMaterialDTO.builder()
                .code(code)
                .name(name)
                .stockQuantity(stock)
                .build();
    }

    // ── findAll ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("Deve retornar lista com todas as matérias-primas cadastradas")
        void shouldReturnAllRawMaterials() {
            RawMaterial rm1 = buildRawMaterial(1L, "MP001", "Farinha", 500.0);
            RawMaterial rm2 = buildRawMaterial(2L, "MP002", "Leite", 200.0);
            when(repository.findAll()).thenReturn(List.of(rm1, rm2));

            List<RawMaterial> result = service.findAll();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getCode()).isEqualTo("MP001");
            assertThat(result.get(1).getCode()).isEqualTo("MP002");
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há matérias-primas")
        void shouldReturnEmptyListWhenNoRawMaterials() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            List<RawMaterial> result = service.findAll();

            assertThat(result).isEmpty();
            verify(repository, times(1)).findAll();
        }
    }

    // ── findById ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Deve retornar a matéria-prima quando o ID existe")
        void shouldReturnRawMaterialWhenIdExists() {
            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 500.0);
            when(repository.findById(1L)).thenReturn(Optional.of(rm));

            RawMaterial result = service.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCode()).isEqualTo("MP001");
            assertThat(result.getName()).isEqualTo("Farinha");
            assertThat(result.getStockQuantity()).isEqualTo(500.0);
            verify(repository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existe")
        void shouldThrowExceptionWhenIdNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Raw Material not found with id: 99");

            verify(repository, times(1)).findById(99L);
        }
    }

    // ── create ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Deve criar e retornar a matéria-prima com ID gerado")
        void shouldCreateAndReturnRawMaterialWithGeneratedId() {
            RawMaterialDTO dto = buildDTO("MP001", "Farinha", 500.0);
            RawMaterial saved = buildRawMaterial(1L, "MP001", "Farinha", 500.0);
            when(repository.save(any(RawMaterial.class))).thenReturn(saved);

            RawMaterial result = service.create(dto);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCode()).isEqualTo("MP001");
            assertThat(result.getName()).isEqualTo("Farinha");
            assertThat(result.getStockQuantity()).isEqualTo(500.0);
            verify(repository, times(1)).save(any(RawMaterial.class));
        }

        @Test
        @DisplayName("Deve mapear corretamente todos os campos do DTO para a entidade")
        void shouldMapAllDtoFieldsCorrectly() {
            RawMaterialDTO dto = buildDTO("MP999", "Açúcar Cristal", 1500.0);
            when(repository.save(any(RawMaterial.class))).thenAnswer(invocation -> {
                RawMaterial arg = invocation.getArgument(0);
                arg.setId(10L);
                return arg;
            });

            RawMaterial result = service.create(dto);

            assertThat(result.getCode()).isEqualTo("MP999");
            assertThat(result.getName()).isEqualTo("Açúcar Cristal");
            assertThat(result.getStockQuantity()).isEqualTo(1500.0);
        }
    }

    // ── update ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("Deve atualizar todos os campos da matéria-prima existente")
        void shouldUpdateAllFieldsOfExistingRawMaterial() {
            RawMaterial existing = buildRawMaterial(1L, "MP001", "Farinha", 500.0);
            RawMaterialDTO dto = buildDTO("MP001-UPD", "Farinha Integral", 750.0);
            when(repository.findById(1L)).thenReturn(Optional.of(existing));
            when(repository.save(any(RawMaterial.class))).thenAnswer(inv -> inv.getArgument(0));

            RawMaterial result = service.update(1L, dto);

            assertThat(result.getCode()).isEqualTo("MP001-UPD");
            assertThat(result.getName()).isEqualTo("Farinha Integral");
            assertThat(result.getStockQuantity()).isEqualTo(750.0);
            verify(repository, times(1)).findById(1L);
            verify(repository, times(1)).save(existing);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao atualizar ID inexistente")
        void shouldThrowExceptionWhenUpdatingNonExistentId() {
            RawMaterialDTO dto = buildDTO("MP001", "Farinha", 500.0);
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(99L, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Raw Material not found with id: 99");

            verify(repository, never()).save(any());
        }
    }

    // ── delete ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Deve deletar a matéria-prima quando o ID existe")
        void shouldDeleteRawMaterialWhenIdExists() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository, times(1)).existsById(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao deletar ID inexistente")
        void shouldThrowExceptionWhenDeletingNonExistentId() {
            when(repository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.delete(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Raw Material not found with id: 99");

            verify(repository, never()).deleteById(anyLong());
        }
    }
}

