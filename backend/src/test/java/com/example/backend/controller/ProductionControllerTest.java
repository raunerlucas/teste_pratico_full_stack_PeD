package com.example.backend.controller;

import com.example.backend.dto.ProductionSuggestionDTO;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.backend.service.ProductionOptimizerService;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("ProductionController — Testes de Integração (MockMvc)")
class ProductionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductionOptimizerService service;

    // ── GET /api/production/optimize ────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/production/optimize")
    class Optimize {

        @Test
        @DisplayName("200 OK — Deve retornar sugestões de produção com múltiplos produtos")
        void shouldReturn200WithSuggestions() throws Exception {
            List<ProductionSuggestionDTO> suggestions = List.of(
                    ProductionSuggestionDTO.builder()
                            .productCode("PRD002").productName("Bolo")
                            .quantity(2).unitPrice(35.00).totalValue(70.00)
                            .build(),
                    ProductionSuggestionDTO.builder()
                            .productCode("PRD001").productName("Pão")
                            .quantity(5).unitPrice(12.50).totalValue(62.50)
                            .build()
            );
            when(service.optimize()).thenReturn(suggestions);

            mockMvc.perform(get("/api/production/optimize"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].productCode").value("PRD002"))
                    .andExpect(jsonPath("$[0].productName").value("Bolo"))
                    .andExpect(jsonPath("$[0].quantity").value(2))
                    .andExpect(jsonPath("$[0].unitPrice").value(35.00))
                    .andExpect(jsonPath("$[0].totalValue").value(70.00))
                    .andExpect(jsonPath("$[1].productCode").value("PRD001"))
                    .andExpect(jsonPath("$[1].productName").value("Pão"))
                    .andExpect(jsonPath("$[1].quantity").value(5))
                    .andExpect(jsonPath("$[1].unitPrice").value(12.50))
                    .andExpect(jsonPath("$[1].totalValue").value(62.50));

            verify(service, times(1)).optimize();
        }

        @Test
        @DisplayName("200 OK — Deve retornar lista vazia quando estoque insuficiente")
        void shouldReturn200WithEmptyListWhenNoStock() throws Exception {
            when(service.optimize()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/production/optimize"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("200 OK — Deve retornar apenas 1 sugestão quando apenas 1 produto é viável")
        void shouldReturn200WithSingleSuggestion() throws Exception {
            List<ProductionSuggestionDTO> suggestions = List.of(
                    ProductionSuggestionDTO.builder()
                            .productCode("PRD001").productName("Pão")
                            .quantity(10).unitPrice(12.50).totalValue(125.00)
                            .build()
            );
            when(service.optimize()).thenReturn(suggestions);

            mockMvc.perform(get("/api/production/optimize"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].quantity").value(10))
                    .andExpect(jsonPath("$[0].totalValue").value(125.00));
        }
    }
}

