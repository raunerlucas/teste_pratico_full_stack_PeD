package com.example.backend.service;

import com.example.backend.dto.ProductCompositionDTO;
import com.example.backend.dto.ProductDTO;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductComposition;
import com.example.backend.entity.RawMaterial;
import com.example.backend.exception.ResourceNotFoundException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService — Testes Unitários")
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private ProductService service;

    // ── Helpers ─────────────────────────────────────────────────────────────────

    private RawMaterial buildRawMaterial(Long id, String code, String name, Double stock) {
        return RawMaterial.builder()
                .id(id).code(code).name(name).stockQuantity(stock)
                .build();
    }

    private Product buildProduct(Long id, String code, String name, Double price) {
        return Product.builder()
                .id(id).code(code).name(name).price(price)
                .compositions(new ArrayList<>())
                .build();
    }

    private ProductDTO buildProductDTO(String code, String name, Double price, List<ProductCompositionDTO> compositions) {
        return ProductDTO.builder()
                .code(code).name(name).price(price).compositions(compositions)
                .build();
    }

    private ProductCompositionDTO buildCompositionDTO(Long rawMaterialId, Double requiredQuantity) {
        return ProductCompositionDTO.builder()
                .rawMaterialId(rawMaterialId).requiredQuantity(requiredQuantity)
                .build();
    }

    // ── findAll ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("Deve retornar lista com todos os produtos cadastrados")
        void shouldReturnAllProducts() {
            Product p1 = buildProduct(1L, "PRD001", "Pão", 12.50);
            Product p2 = buildProduct(2L, "PRD002", "Bolo", 35.00);
            when(repository.findAll()).thenReturn(List.of(p1, p2));

            List<Product> result = service.findAll();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getCode()).isEqualTo("PRD001");
            assertThat(result.get(1).getCode()).isEqualTo("PRD002");
            verify(repository, times(1)).findAll();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há produtos")
        void shouldReturnEmptyListWhenNoProducts() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            List<Product> result = service.findAll();

            assertThat(result).isEmpty();
            verify(repository, times(1)).findAll();
        }
    }

    // ── findById ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Deve retornar o produto quando o ID existe")
        void shouldReturnProductWhenIdExists() {
            Product product = buildProduct(1L, "PRD001", "Pão", 12.50);
            when(repository.findById(1L)).thenReturn(Optional.of(product));

            Product result = service.findById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCode()).isEqualTo("PRD001");
            assertThat(result.getPrice()).isEqualTo(12.50);
            verify(repository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existe")
        void shouldThrowExceptionWhenIdNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: 99");

            verify(repository, times(1)).findById(99L);
        }
    }

    // ── create ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Deve criar produto sem composições quando compositions é null")
        void shouldCreateProductWithoutCompositions() {
            ProductDTO dto = buildProductDTO("PRD001", "Pão", 12.50, null);
            when(repository.save(any(Product.class))).thenAnswer(inv -> {
                Product p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });

            Product result = service.create(dto);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCode()).isEqualTo("PRD001");
            assertThat(result.getCompositions()).isEmpty();
            verify(repository, times(1)).save(any(Product.class));
            verify(rawMaterialRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Deve criar produto com composições quando matérias-primas existem")
        void shouldCreateProductWithCompositionsWhenRawMaterialsExist() {
            RawMaterial rm1 = buildRawMaterial(1L, "MP001", "Farinha", 500.0);
            RawMaterial rm2 = buildRawMaterial(2L, "MP002", "Leite", 200.0);

            List<ProductCompositionDTO> compositions = List.of(
                    buildCompositionDTO(1L, 200.0),
                    buildCompositionDTO(2L, 50.0)
            );
            ProductDTO dto = buildProductDTO("PRD001", "Pão", 12.50, compositions);

            when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(rm1));
            when(rawMaterialRepository.findById(2L)).thenReturn(Optional.of(rm2));
            when(repository.save(any(Product.class))).thenAnswer(inv -> {
                Product p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });

            Product result = service.create(dto);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCompositions()).hasSize(2);
            assertThat(result.getCompositions().get(0).getRawMaterial().getCode()).isEqualTo("MP001");
            assertThat(result.getCompositions().get(0).getRequiredQuantity()).isEqualTo(200.0);
            assertThat(result.getCompositions().get(1).getRawMaterial().getCode()).isEqualTo("MP002");
            assertThat(result.getCompositions().get(1).getRequiredQuantity()).isEqualTo(50.0);
            verify(rawMaterialRepository, times(1)).findById(1L);
            verify(rawMaterialRepository, times(1)).findById(2L);
            verify(repository, times(1)).save(any(Product.class));
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando matéria-prima da composição não existe")
        void shouldThrowExceptionWhenRawMaterialNotFound() {
            List<ProductCompositionDTO> compositions = List.of(buildCompositionDTO(99L, 100.0));
            ProductDTO dto = buildProductDTO("PRD001", "Pão", 12.50, compositions);

            when(rawMaterialRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Raw Material not found with id: 99");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve criar produto com lista de composições vazia")
        void shouldCreateProductWithEmptyCompositions() {
            ProductDTO dto = buildProductDTO("PRD001", "Pão", 12.50, Collections.emptyList());
            when(repository.save(any(Product.class))).thenAnswer(inv -> {
                Product p = inv.getArgument(0);
                p.setId(1L);
                return p;
            });

            Product result = service.create(dto);

            assertThat(result.getCompositions()).isEmpty();
            verify(rawMaterialRepository, never()).findById(anyLong());
        }
    }

    // ── update ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("Deve atualizar todos os campos e substituir composições")
        void shouldUpdateAllFieldsAndReplaceCompositions() {
            Product existing = buildProduct(1L, "PRD001", "Pão", 12.50);
            existing.getCompositions().add(ProductComposition.builder()
                    .id(1L).product(existing)
                    .rawMaterial(buildRawMaterial(1L, "MP001", "Farinha", 500.0))
                    .requiredQuantity(200.0).build());

            RawMaterial newRm = buildRawMaterial(2L, "MP002", "Leite", 200.0);
            List<ProductCompositionDTO> newComps = List.of(buildCompositionDTO(2L, 100.0));
            ProductDTO dto = buildProductDTO("PRD001-UPD", "Pão Premium", 15.00, newComps);

            when(repository.findById(1L)).thenReturn(Optional.of(existing));
            when(rawMaterialRepository.findById(2L)).thenReturn(Optional.of(newRm));
            when(repository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            Product result = service.update(1L, dto);

            assertThat(result.getCode()).isEqualTo("PRD001-UPD");
            assertThat(result.getName()).isEqualTo("Pão Premium");
            assertThat(result.getPrice()).isEqualTo(15.00);
            assertThat(result.getCompositions()).hasSize(1);
            assertThat(result.getCompositions().get(0).getRawMaterial().getCode()).isEqualTo("MP002");
            assertThat(result.getCompositions().get(0).getRequiredQuantity()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao atualizar ID inexistente")
        void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
            ProductDTO dto = buildProductDTO("PRD001", "Pão", 12.50, null);
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(99L, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: 99");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException quando matéria-prima da nova composição não existe")
        void shouldThrowExceptionWhenNewCompositionRawMaterialNotFound() {
            Product existing = buildProduct(1L, "PRD001", "Pão", 12.50);
            List<ProductCompositionDTO> comps = List.of(buildCompositionDTO(99L, 100.0));
            ProductDTO dto = buildProductDTO("PRD001", "Pão", 12.50, comps);

            when(repository.findById(1L)).thenReturn(Optional.of(existing));
            when(rawMaterialRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(1L, dto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Raw Material not found with id: 99");

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("Deve limpar composições ao atualizar com compositions null")
        void shouldClearCompositionsWhenUpdatingWithNull() {
            Product existing = buildProduct(1L, "PRD001", "Pão", 12.50);
            existing.getCompositions().add(ProductComposition.builder()
                    .id(1L).product(existing)
                    .rawMaterial(buildRawMaterial(1L, "MP001", "Farinha", 500.0))
                    .requiredQuantity(200.0).build());

            ProductDTO dto = buildProductDTO("PRD001", "Pão Simples", 10.00, null);

            when(repository.findById(1L)).thenReturn(Optional.of(existing));
            when(repository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

            Product result = service.update(1L, dto);

            assertThat(result.getCompositions()).isEmpty();
            assertThat(result.getName()).isEqualTo("Pão Simples");
            assertThat(result.getPrice()).isEqualTo(10.00);
        }
    }

    // ── delete ──────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Deve deletar o produto quando o ID existe")
        void shouldDeleteProductWhenIdExists() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository, times(1)).existsById(1L);
            verify(repository, times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Deve lançar ResourceNotFoundException ao deletar ID inexistente")
        void shouldThrowExceptionWhenDeletingNonExistentProduct() {
            when(repository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.delete(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: 99");

            verify(repository, never()).deleteById(anyLong());
        }
    }
}

