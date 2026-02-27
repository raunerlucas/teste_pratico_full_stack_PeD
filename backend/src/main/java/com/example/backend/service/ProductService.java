package com.example.backend.service;

import com.example.backend.dto.ProductCompositionDTO;
import com.example.backend.dto.ProductDTO;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductComposition;
import com.example.backend.entity.RawMaterial;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.RawMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável pela lógica de negócio relacionada a Produtos (Products).
 *
 * <p>Fornece operações CRUD completas para o gerenciamento de produtos industriais,
 * incluindo a composição de cada produto (quais matérias-primas e quantidades
 * são necessárias para fabricar 1 unidade).</p>
 *
 * <p>Ao criar ou atualizar um produto, as composições são gerenciadas em cascata
 * ({@code CascadeType.ALL} + {@code orphanRemoval = true}), garantindo que
 * registros antigos sejam removidos automaticamente ao substituir a lista.</p>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see Product
 * @see ProductComposition
 * @see ProductDTO
 * @see ProductRepository
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final RawMaterialRepository rawMaterialRepository;

    /**
     * Retorna todos os produtos cadastrados.
     *
     * @return lista de {@link Product} — pode ser vazia caso não haja registros
     */
    public List<Product> findAll() {
        return repository.findAll();
    }

    /**
     * Busca um produto pelo seu identificador único.
     *
     * @param id identificador do produto
     * @return a entidade {@link Product} correspondente
     * @throws ResourceNotFoundException se nenhum produto for encontrado com o {@code id} informado
     */
    public Product findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    /**
     * Cadastra um novo produto no sistema, incluindo sua composição de matérias-primas.
     *
     * <p>Para cada item da composição no DTO, busca a matéria-prima correspondente
     * pelo {@code rawMaterialId} e associa ao produto com a quantidade requerida.</p>
     *
     * @param dto dados do produto a ser criado ({@code code}, {@code name}, {@code price}, {@code compositions})
     * @return a entidade {@link Product} persistida, já com o {@code id} gerado e composições associadas
     * @throws ResourceNotFoundException se alguma matéria-prima da composição não for encontrada
     */
    @Transactional
    public Product create(ProductDTO dto) {
        Product product = Product.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .price(dto.getPrice())
                .compositions(new ArrayList<>())
                .build();

        if (dto.getCompositions() != null) {
            for (ProductCompositionDTO compDTO : dto.getCompositions()) {
                RawMaterial rawMaterial = rawMaterialRepository.findById(compDTO.getRawMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Raw Material not found with id: " + compDTO.getRawMaterialId()));

                ProductComposition composition = ProductComposition.builder()
                        .product(product)
                        .rawMaterial(rawMaterial)
                        .requiredQuantity(compDTO.getRequiredQuantity())
                        .build();

                product.getCompositions().add(composition);
            }
        }

        return repository.save(product);
    }

    /**
     * Atualiza os dados de um produto existente, substituindo sua composição por completo.
     *
     * <p>Limpa todas as composições anteriores ({@code orphanRemoval} garante a exclusão no banco)
     * e reconstrói a lista com os novos dados do DTO.</p>
     *
     * @param id  identificador do produto a ser atualizado
     * @param dto novos dados ({@code code}, {@code name}, {@code price}, {@code compositions})
     * @return a entidade {@link Product} atualizada
     * @throws ResourceNotFoundException se o produto ou alguma matéria-prima da composição não for encontrada
     */
    @Transactional
    public Product update(Long id, ProductDTO dto) {
        Product product = findById(id);

        product.setCode(dto.getCode());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        product.getCompositions().clear();

        if (dto.getCompositions() != null) {
            for (ProductCompositionDTO compDTO : dto.getCompositions()) {
                RawMaterial rawMaterial = rawMaterialRepository.findById(compDTO.getRawMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Raw Material not found with id: " + compDTO.getRawMaterialId()));

                ProductComposition composition = ProductComposition.builder()
                        .product(product)
                        .rawMaterial(rawMaterial)
                        .requiredQuantity(compDTO.getRequiredQuantity())
                        .build();

                product.getCompositions().add(composition);
            }
        }

        return repository.save(product);
    }

    /**
     * Remove um produto pelo seu identificador.
     *
     * <p>As composições associadas são removidas automaticamente via {@code CascadeType.ALL}.</p>
     *
     * @param id identificador do produto a ser removido
     * @throws ResourceNotFoundException se nenhum produto for encontrado com o {@code id} informado
     */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

