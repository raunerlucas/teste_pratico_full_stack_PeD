package com.example.backend.controller;

import com.example.backend.dto.ProductCompositionDTO;
import com.example.backend.dto.ProductDTO;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductComposition;
import com.example.backend.entity.RawMaterial;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.service.ProductService;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("ProductController — Testes de Integração (MockMvc)")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService service;

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private RawMaterial buildRawMaterial(Long id, String code, String name, Double stock) {
        return RawMaterial.builder().id(id).code(code).name(name).stockQuantity(stock).build();
    }

    private Product buildProductWithComposition() {
        RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 500.0);

        Product product = Product.builder()
                .id(1L).code("PRD001").name("Pão").price(12.50)
                .compositions(new ArrayList<>())
                .build();

        ProductComposition comp = ProductComposition.builder()
                .id(1L).product(product).rawMaterial(rm).requiredQuantity(200.0)
                .build();

        product.getCompositions().add(comp);
        return product;
    }

    // ── GET /api/products ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/products")
    class GetAll {

        @Test
        @DisplayName("200 OK — Deve retornar lista de produtos")
        void shouldReturn200WithList() throws Exception {
            Product p = buildProductWithComposition();
            when(service.findAll()).thenReturn(List.of(p));

            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].code").value("PRD001"))
                    .andExpect(jsonPath("$[0].name").value("Pão"))
                    .andExpect(jsonPath("$[0].price").value(12.50))
                    .andExpect(jsonPath("$[0].compositions", hasSize(1)))
                    .andExpect(jsonPath("$[0].compositions[0].requiredQuantity").value(200.0));

            verify(service, times(1)).findAll();
        }

        @Test
        @DisplayName("200 OK — Deve retornar lista vazia quando não há produtos")
        void shouldReturn200WithEmptyList() throws Exception {
            when(service.findAll()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ── GET /api/products/{id} ──────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/products/{id}")
    class GetById {

        @Test
        @DisplayName("200 OK — Deve retornar produto com composições quando ID existe")
        void shouldReturn200WhenIdExists() throws Exception {
            Product p = buildProductWithComposition();
            when(service.findById(1L)).thenReturn(p);

            mockMvc.perform(get("/api/products/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.code").value("PRD001"))
                    .andExpect(jsonPath("$.name").value("Pão"))
                    .andExpect(jsonPath("$.price").value(12.50))
                    .andExpect(jsonPath("$.compositions", hasSize(1)))
                    .andExpect(jsonPath("$.compositions[0].rawMaterial.code").value("MP001"))
                    .andExpect(jsonPath("$.compositions[0].requiredQuantity").value(200.0));
        }

        @Test
        @DisplayName("404 Not Found — Deve retornar erro quando ID não existe")
        void shouldReturn404WhenIdNotFound() throws Exception {
            when(service.findById(99L))
                    .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

            mockMvc.perform(get("/api/products/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"))
                    .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
        }
    }

    // ── POST /api/products ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/products")
    class Create {

        @Test
        @DisplayName("201 Created — Deve criar produto com composições")
        void shouldReturn201WhenCreatedWithCompositions() throws Exception {
            ProductDTO dto = ProductDTO.builder()
                    .code("PRD001").name("Pão").price(12.50)
                    .compositions(List.of(
                            ProductCompositionDTO.builder().rawMaterialId(1L).requiredQuantity(200.0).build()
                    ))
                    .build();

            Product created = buildProductWithComposition();
            when(service.create(any(ProductDTO.class))).thenReturn(created);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.code").value("PRD001"))
                    .andExpect(jsonPath("$.compositions", hasSize(1)));

            verify(service, times(1)).create(any(ProductDTO.class));
        }

        @Test
        @DisplayName("404 Not Found — Deve retornar erro quando matéria-prima não existe")
        void shouldReturn404WhenRawMaterialNotFound() throws Exception {
            ProductDTO dto = ProductDTO.builder()
                    .code("PRD001").name("Pão").price(12.50)
                    .compositions(List.of(
                            ProductCompositionDTO.builder().rawMaterialId(99L).requiredQuantity(200.0).build()
                    ))
                    .build();

            when(service.create(any(ProductDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Raw Material not found with id: 99"));

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Raw Material not found with id: 99"));
        }

        @Test
        @DisplayName("201 Created — Deve criar produto sem composições")
        void shouldReturn201WhenCreatedWithoutCompositions() throws Exception {
            ProductDTO dto = ProductDTO.builder()
                    .code("PRD001").name("Pão").price(12.50)
                    .compositions(null)
                    .build();

            Product created = Product.builder()
                    .id(1L).code("PRD001").name("Pão").price(12.50)
                    .compositions(new ArrayList<>())
                    .build();
            when(service.create(any(ProductDTO.class))).thenReturn(created);

            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.compositions", hasSize(0)));
        }
    }

    // ── PUT /api/products/{id} ──────────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class Update {

        @Test
        @DisplayName("200 OK — Deve atualizar produto e suas composições")
        void shouldReturn200WhenUpdated() throws Exception {
            ProductDTO dto = ProductDTO.builder()
                    .code("PRD001").name("Pão Premium").price(15.00)
                    .compositions(List.of(
                            ProductCompositionDTO.builder().rawMaterialId(1L).requiredQuantity(250.0).build()
                    ))
                    .build();

            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 500.0);
            Product updated = Product.builder()
                    .id(1L).code("PRD001").name("Pão Premium").price(15.00)
                    .compositions(new ArrayList<>())
                    .build();
            updated.getCompositions().add(ProductComposition.builder()
                    .id(2L).product(updated).rawMaterial(rm).requiredQuantity(250.0).build());

            when(service.update(eq(1L), any(ProductDTO.class))).thenReturn(updated);

            mockMvc.perform(put("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Pão Premium"))
                    .andExpect(jsonPath("$.price").value(15.00))
                    .andExpect(jsonPath("$.compositions[0].requiredQuantity").value(250.0));
        }

        @Test
        @DisplayName("404 Not Found — Deve retornar erro ao atualizar ID inexistente")
        void shouldReturn404WhenUpdatingNonExistent() throws Exception {
            ProductDTO dto = ProductDTO.builder()
                    .code("PRD001").name("Pão").price(12.50).build();

            when(service.update(eq(99L), any(ProductDTO.class)))
                    .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

            mockMvc.perform(put("/api/products/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
        }
    }

    // ── DELETE /api/products/{id} ───────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class DeleteEndpoint {

        @Test
        @DisplayName("204 No Content — Deve deletar produto existente")
        void shouldReturn204WhenDeleted() throws Exception {
            doNothing().when(service).delete(1L);

            mockMvc.perform(delete("/api/products/1"))
                    .andExpect(status().isNoContent());

            verify(service, times(1)).delete(1L);
        }

        @Test
        @DisplayName("404 Not Found — Deve retornar erro ao deletar ID inexistente")
        void shouldReturn404WhenDeletingNonExistent() throws Exception {
            doThrow(new ResourceNotFoundException("Product not found with id: 99"))
                    .when(service).delete(99L);

            mockMvc.perform(delete("/api/products/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
        }
    }
}

