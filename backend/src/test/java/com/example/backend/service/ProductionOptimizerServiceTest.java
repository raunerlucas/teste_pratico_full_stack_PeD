package com.example.backend.service;

import com.example.backend.dto.ProductionSuggestionDTO;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductComposition;
import com.example.backend.entity.RawMaterial;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.RawMaterialRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductionOptimizerService — Testes Unitários")
class ProductionOptimizerServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private ProductionOptimizerService service;

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private RawMaterial buildRawMaterial(Long id, String code, String name, Double stock) {
        return RawMaterial.builder()
                .id(id).code(code).name(name).stockQuantity(stock)
                .build();
    }

    private Product buildProductWithCompositions(Long id, String code, String name, Double price,
                                                  List<CompositionSpec> specs) {
        Product product = Product.builder()
                .id(id).code(code).name(name).price(price)
                .compositions(new ArrayList<>())
                .build();

        for (CompositionSpec spec : specs) {
            ProductComposition comp = ProductComposition.builder()
                    .id(spec.compId)
                    .product(product)
                    .rawMaterial(spec.rawMaterial)
                    .requiredQuantity(spec.requiredQuantity)
                    .build();
            product.getCompositions().add(comp);
        }

        return product;
    }

    /** Registro auxiliar para construir composições de teste */
    private record CompositionSpec(Long compId, RawMaterial rawMaterial, Double requiredQuantity) {}

    // ── Cenários de otimização ──────────────────────────────────────────────────

    @Nested
    @DisplayName("optimize() — Cenários básicos")
    class BasicScenarios {

        @Test
        @DisplayName("Deve retornar lista vazia quando não há produtos cadastrados")
        void shouldReturnEmptyWhenNoProducts() {
            when(productRepository.findAll()).thenReturn(Collections.emptyList());
            when(rawMaterialRepository.findAll()).thenReturn(Collections.emptyList());

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há matérias-primas em estoque")
        void shouldReturnEmptyWhenNoRawMaterials() {
            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 0.0);
            Product product = buildProductWithCompositions(1L, "PRD001", "Pão", 12.50,
                    List.of(new CompositionSpec(1L, rm, 200.0)));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(rm));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando produtos não possuem composição")
        void shouldReturnEmptyWhenProductsHaveNoCompositions() {
            Product product = Product.builder()
                    .id(1L).code("PRD001").name("Pão").price(12.50)
                    .compositions(new ArrayList<>())
                    .build();

            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 500.0);

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(rm));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando composição tem null em compositions")
        void shouldReturnEmptyWhenCompositionsIsNull() {
            Product product = Product.builder()
                    .id(1L).code("PRD001").name("Pão").price(12.50)
                    .compositions(null)
                    .build();

            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 500.0);

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(rm));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("optimize() — Produto único")
    class SingleProduct {

        @Test
        @DisplayName("Deve produzir o máximo de unidades possível com estoque suficiente")
        void shouldProduceMaxUnitsWithSufficientStock() {
            // Farinha: 1000g em estoque, produto precisa de 200g → máximo 5 unidades
            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 1000.0);
            Product product = buildProductWithCompositions(1L, "PRD001", "Pão", 12.50,
                    List.of(new CompositionSpec(1L, rm, 200.0)));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(rm));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductCode()).isEqualTo("PRD001");
            assertThat(result.get(0).getQuantity()).isEqualTo(5);
            assertThat(result.get(0).getUnitPrice()).isEqualTo(12.50);
            assertThat(result.get(0).getTotalValue()).isEqualTo(62.50);
        }

        @Test
        @DisplayName("Deve respeitar o gargalo da matéria-prima mais escassa")
        void shouldRespectBottleneckRawMaterial() {
            // Farinha: 1000g (→ 5 unidades) | Leite: 100ml (→ 2 unidades) → gargalo = 2
            RawMaterial farinha = buildRawMaterial(1L, "MP001", "Farinha", 1000.0);
            RawMaterial leite = buildRawMaterial(2L, "MP002", "Leite", 100.0);

            Product product = buildProductWithCompositions(1L, "PRD001", "Pão", 12.50,
                    List.of(
                            new CompositionSpec(1L, farinha, 200.0),
                            new CompositionSpec(2L, leite, 50.0)
                    ));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(farinha, leite));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getQuantity()).isEqualTo(2); // gargalo pelo leite
            assertThat(result.get(0).getTotalValue()).isEqualTo(25.0);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando estoque insuficiente para 1 unidade")
        void shouldReturnEmptyWhenStockInsufficientForOneUnit() {
            // Precisa de 200g, tem apenas 100g → 0 unidades
            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 100.0);
            Product product = buildProductWithCompositions(1L, "PRD001", "Pão", 12.50,
                    List.of(new CompositionSpec(1L, rm, 200.0)));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(rm));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("optimize() — Múltiplos produtos disputando matéria-prima")
    class MultipleProductsConflict {

        @Test
        @DisplayName("Deve priorizar produto de maior preço quando disputam mesma matéria-prima")
        void shouldPrioritizeHigherPricedProduct() {
            // Farinha: 500g
            // Pão: R$10, precisa 100g → max 5 unidades
            // Bolo: R$35, precisa 200g → max 2 unidades (priorizado por ser mais caro)
            // Após Bolo (2x200g=400g), sobra 100g → Pão pode fazer 1 unidade
            RawMaterial farinha = buildRawMaterial(1L, "MP001", "Farinha", 500.0);

            Product pao = buildProductWithCompositions(1L, "PRD001", "Pão", 10.0,
                    List.of(new CompositionSpec(1L, farinha, 100.0)));

            Product bolo = buildProductWithCompositions(2L, "PRD002", "Bolo", 35.0,
                    List.of(new CompositionSpec(2L, farinha, 200.0)));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(pao, bolo)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(farinha));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).hasSize(2);

            // Bolo primeiro (maior preço)
            assertThat(result.get(0).getProductCode()).isEqualTo("PRD002");
            assertThat(result.get(0).getQuantity()).isEqualTo(2);
            assertThat(result.get(0).getTotalValue()).isEqualTo(70.0);

            // Pão com o restante
            assertThat(result.get(1).getProductCode()).isEqualTo("PRD001");
            assertThat(result.get(1).getQuantity()).isEqualTo(1);
            assertThat(result.get(1).getTotalValue()).isEqualTo(10.0);
        }

        @Test
        @DisplayName("Deve excluir segundo produto quando estoque esgota após o primeiro")
        void shouldExcludeSecondProductWhenStockExhausted() {
            // Farinha: 400g
            // Bolo: R$35, precisa 200g → 2 unidades (consome tudo)
            // Pão: R$10, precisa 100g → 0 unidades (sem farinha)
            RawMaterial farinha = buildRawMaterial(1L, "MP001", "Farinha", 400.0);

            Product pao = buildProductWithCompositions(1L, "PRD001", "Pão", 10.0,
                    List.of(new CompositionSpec(1L, farinha, 100.0)));

            Product bolo = buildProductWithCompositions(2L, "PRD002", "Bolo", 35.0,
                    List.of(new CompositionSpec(2L, farinha, 200.0)));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(pao, bolo)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(farinha));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductCode()).isEqualTo("PRD002");
            assertThat(result.get(0).getQuantity()).isEqualTo(2);
        }

        @Test
        @DisplayName("Deve fabricar ambos os produtos quando usam matérias-primas diferentes")
        void shouldProduceBothProductsWhenDifferentRawMaterials() {
            RawMaterial farinha = buildRawMaterial(1L, "MP001", "Farinha", 1000.0);
            RawMaterial chocolate = buildRawMaterial(2L, "MP002", "Chocolate", 600.0);

            Product pao = buildProductWithCompositions(1L, "PRD001", "Pão", 10.0,
                    List.of(new CompositionSpec(1L, farinha, 200.0)));

            Product trufa = buildProductWithCompositions(2L, "PRD002", "Trufa", 25.0,
                    List.of(new CompositionSpec(2L, chocolate, 100.0)));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(pao, trufa)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(farinha, chocolate));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).hasSize(2);

            // Trufa primeiro (maior preço)
            assertThat(result.get(0).getProductCode()).isEqualTo("PRD002");
            assertThat(result.get(0).getQuantity()).isEqualTo(6);
            assertThat(result.get(0).getTotalValue()).isEqualTo(150.0);

            // Pão com estoque independente
            assertThat(result.get(1).getProductCode()).isEqualTo("PRD001");
            assertThat(result.get(1).getQuantity()).isEqualTo(5);
            assertThat(result.get(1).getTotalValue()).isEqualTo(50.0);
        }
    }

    @Nested
    @DisplayName("optimize() — Cenários de borda")
    class EdgeCases {

        @Test
        @DisplayName("Deve ignorar composição com requiredQuantity zero")
        void shouldIgnoreCompositionWithZeroRequiredQuantity() {
            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 500.0);

            // Composição com qty 0 deve ser ignorada; resultado baseado apenas na outra composição
            RawMaterial rm2 = buildRawMaterial(2L, "MP002", "Sal", 100.0);
            Product product = buildProductWithCompositions(1L, "PRD001", "Pão", 10.0,
                    List.of(
                            new CompositionSpec(1L, rm, 100.0),
                            new CompositionSpec(2L, rm2, 0.0) // qty zero
                    ));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(rm, rm2));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getQuantity()).isEqualTo(5);
        }

        @Test
        @DisplayName("Deve calcular corretamente com estoque fracionário")
        void shouldHandleFractionalStock() {
            // Estoque: 550g, precisa 200g → floor(550/200) = 2 unidades
            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 550.0);
            Product product = buildProductWithCompositions(1L, "PRD001", "Pão", 12.50,
                    List.of(new CompositionSpec(1L, rm, 200.0)));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(rm));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getQuantity()).isEqualTo(2);
            assertThat(result.get(0).getTotalValue()).isEqualTo(25.0);
        }

        @Test
        @DisplayName("Deve calcular totalValue como quantity × unitPrice")
        void shouldCalculateTotalValueCorrectly() {
            RawMaterial rm = buildRawMaterial(1L, "MP001", "Farinha", 900.0);
            Product product = buildProductWithCompositions(1L, "PRD001", "Pão", 7.50,
                    List.of(new CompositionSpec(1L, rm, 100.0)));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(rm));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getQuantity()).isEqualTo(9);
            assertThat(result.get(0).getUnitPrice()).isEqualTo(7.50);
            assertThat(result.get(0).getTotalValue()).isEqualTo(67.50);
        }

        @Test
        @DisplayName("Deve lidar com matéria-prima sem estoque mapeado (rawMaterial não existe no stockMap)")
        void shouldReturnZeroWhenRawMaterialNotInStockMap() {
            // Produto precisa de matéria-prima ID=99, que não está no estoque
            RawMaterial rmGhost = buildRawMaterial(99L, "MP099", "Fantasma", 0.0);
            Product product = buildProductWithCompositions(1L, "PRD001", "Pão", 12.50,
                    List.of(new CompositionSpec(1L, rmGhost, 200.0)));

            // Estoque tem apenas ID=1
            RawMaterial rmEstoque = buildRawMaterial(1L, "MP001", "Farinha", 500.0);

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(product)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(rmEstoque));

            List<ProductionSuggestionDTO> result = service.optimize();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Deve produzir 3 produtos priorizando por preço decrescente")
        void shouldProduceThreeProductsSortedByPriceDesc() {
            RawMaterial farinha = buildRawMaterial(1L, "MP001", "Farinha", 3000.0);

            Product pao = buildProductWithCompositions(1L, "PRD001", "Pão", 5.0,
                    List.of(new CompositionSpec(1L, farinha, 100.0)));
            Product bolo = buildProductWithCompositions(2L, "PRD002", "Bolo", 30.0,
                    List.of(new CompositionSpec(2L, farinha, 500.0)));
            Product torta = buildProductWithCompositions(3L, "PRD003", "Torta", 50.0,
                    List.of(new CompositionSpec(3L, farinha, 1000.0)));

            when(productRepository.findAll()).thenReturn(new ArrayList<>(List.of(pao, bolo, torta)));
            when(rawMaterialRepository.findAll()).thenReturn(List.of(farinha));

            List<ProductionSuggestionDTO> result = service.optimize();

            // Torta: 3000/1000 = 3 → consome 3000g... 0 sobra
            // Mas 3*1000 = 3000, sobra 0
            // Esperado: Torta=3, total = 150
            // Mas na verdade: Torta: floor(3000/1000)=3, consome 3000 → 0 restante
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductCode()).isEqualTo("PRD003"); // mais caro
            assertThat(result.get(0).getQuantity()).isEqualTo(3);
            assertThat(result.get(0).getTotalValue()).isEqualTo(150.0);
        }
    }
}

