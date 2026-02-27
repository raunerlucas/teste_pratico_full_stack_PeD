package com.example.backend.controller;

import com.example.backend.dto.RawMaterialDTO;
import com.example.backend.entity.RawMaterial;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.RawMaterialService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RawMaterialController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("RawMaterialController — Testes de Integração (MockMvc)")
class RawMaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RawMaterialService service;

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private RawMaterial buildRawMaterial(Long id, String code, String name, Double stock) {
        return RawMaterial.builder()
                .id(id).code(code).name(name).stockQuantity(stock)
                .build();
    }

    // ── GET /api/raw-materials ──────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/raw-materials")
    class GetAll {

        @Test
        @DisplayName("200 OK — Deve retornar lista de matérias-primas")
        void shouldReturn200WithList() throws Exception {
            List<RawMaterial> list = List.of(
                    buildRawMaterial(1L, "MP001", "Farinha", 500.0),
                    buildRawMaterial(2L, "MP002", "Leite", 200.0)
            );
            when(service.findAll()).thenReturn(list);

            mockMvc.perform(get("/api/raw-materials"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].code").value("MP001"))
                    .andExpect(jsonPath("$[0].name").value("Farinha"))
                    .andExpect(jsonPath("$[0].stockQuantity").value(500.0))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].code").value("MP002"));

            verify(service, times(1)).findAll();
        }

        @Test
        @DisplayName("200 OK — Deve retornar lista vazia quando não há registros")
        void shouldReturn200WithEmptyList() throws Exception {
            when(service.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/raw-materials"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ── GET /api/raw-materials/{id} ─────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/raw-materials/{id}")
    class GetById {

        @Test
        @DisplayName("200 OK — Deve retornar matéria-prima quando ID existe")
        void shouldReturn200WhenIdExists() throws Exception {
            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 500.0);
            when(service.findById(1L)).thenReturn(rm);

            mockMvc.perform(get("/api/raw-materials/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.code").value("MP001"))
                    .andExpect(jsonPath("$.name").value("Farinha"))
                    .andExpect(jsonPath("$.stockQuantity").value(500.0));
        }

        @Test
        @DisplayName("404 Not Found — Deve retornar erro quando ID não existe")
        void shouldReturn404WhenIdNotFound() throws Exception {
            when(service.findById(99L))
                    .thenThrow(new ResourceNotFoundException("Raw Material not found with id: 99"));

            mockMvc.perform(get("/api/raw-materials/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("Raw Material not found with id: 99"));
        }
    }

    // ── POST /api/raw-materials ─────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/raw-materials")
    class Create {

        @Test
        @DisplayName("201 Created — Deve criar matéria-prima e retornar com ID gerado")
        void shouldReturn201WhenCreated() throws Exception {
            RawMaterialDTO dto = RawMaterialDTO.builder()
                    .code("MP001").name("Farinha").stockQuantity(500.0).build();

            RawMaterial created = buildRawMaterial(1L, "MP001", "Farinha", 500.0);
            when(service.create(any(RawMaterialDTO.class))).thenReturn(created);

            mockMvc.perform(post("/api/raw-materials")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.code").value("MP001"))
                    .andExpect(jsonPath("$.name").value("Farinha"))
                    .andExpect(jsonPath("$.stockQuantity").value(500.0));

            verify(service, times(1)).create(any(RawMaterialDTO.class));
        }
    }

    // ── PUT /api/raw-materials/{id} ─────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/raw-materials/{id}")
    class Update {

        @Test
        @DisplayName("200 OK — Deve atualizar matéria-prima existente")
        void shouldReturn200WhenUpdated() throws Exception {
            RawMaterialDTO dto = RawMaterialDTO.builder()
                    .code("MP001").name("Farinha Integral").stockQuantity(750.0).build();

            RawMaterial updated = buildRawMaterial(1L, "MP001", "Farinha Integral", 750.0);
            when(service.update(eq(1L), any(RawMaterialDTO.class))).thenReturn(updated);

            mockMvc.perform(put("/api/raw-materials/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Farinha Integral"))
                    .andExpect(jsonPath("$.stockQuantity").value(750.0));
        }

        @Test
        @DisplayName("404 Not Found — Deve retornar erro ao atualizar ID inexistente")
        void shouldReturn404WhenUpdatingNonExistent() throws Exception {
            RawMaterialDTO dto = RawMaterialDTO.builder()
                    .code("MP001").name("Farinha").stockQuantity(500.0).build();

            when(service.update(eq(99L), any(RawMaterialDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Raw Material not found with id: 99"));

            mockMvc.perform(put("/api/raw-materials/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Raw Material not found with id: 99"));
        }
    }

    // ── DELETE /api/raw-materials/{id} ──────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/raw-materials/{id}")
    class DeleteEndpoint {

        @Test
        @DisplayName("204 No Content — Deve deletar matéria-prima existente")
        void shouldReturn204WhenDeleted() throws Exception {
            doNothing().when(service).delete(1L);

            mockMvc.perform(delete("/api/raw-materials/1"))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).delete(1L);
        }

        @Test
        @DisplayName("404 Not Found — Deve retornar erro ao deletar ID inexistente")
        void shouldReturn404WhenDeletingNonExistent() throws Exception {
            doThrow(new ResourceNotFoundException("Raw Material not found with id: 99"))
                    .when(service).delete(99L);

            mockMvc.perform(delete("/api/raw-materials/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Raw Material not found with id: 99"));
        }
    }
}

