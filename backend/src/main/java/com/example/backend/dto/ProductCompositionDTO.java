package com.example.backend.dto;

import lombok.*;

/**
 * DTO (Data Transfer Object) para a <strong>Composição de um Produto</strong>.
 *
 * <p>Representa um item da lista de composições enviada nos endpoints de
 * criação e atualização de produtos ({@code POST} / {@code PUT} em {@code /api/products}).
 * Cada item indica qual matéria-prima é necessária e em que quantidade
 * para fabricar 1 unidade do produto.</p>
 *
 * <h3>Exemplo de JSON (dentro do array {@code compositions})</h3>
 * <pre>{@code
 * {
 *   "rawMaterialId": 1,
 *   "requiredQuantity": 200.0
 * }
 * }</pre>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see ProductDTO
 * @see com.example.backend.entity.ProductComposition
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCompositionDTO {

    /**
     * Identificador ({@code id}) da matéria-prima utilizada na composição.
     * Deve corresponder a um registro existente na tabela {@code raw_material}.
     */
    private Long rawMaterialId;

    /**
     * Quantidade da matéria-prima necessária para fabricar <strong>1 unidade</strong> do produto.
     * A unidade de medida é a mesma do campo {@code stockQuantity} da matéria-prima.
     */
    private Double requiredQuantity;
}

