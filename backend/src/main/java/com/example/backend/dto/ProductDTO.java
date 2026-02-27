package com.example.backend.dto;

import lombok.*;

import java.util.List;

/**
 * DTO (Data Transfer Object) para criação e atualização de <strong>Produtos</strong>.
 *
 * <p>Utilizado como corpo de requisição nos endpoints {@code POST} e {@code PUT}
 * de {@code /api/products}. Inclui a lista de composições (matérias-primas
 * e quantidades necessárias para fabricar 1 unidade).</p>
 *
 * <h3>Exemplo de JSON</h3>
 * <pre>{@code
 * {
 *   "code": "PRD001",
 *   "name": "Pão Francês",
 *   "price": 12.50,
 *   "compositions": [
 *     { "rawMaterialId": 1, "requiredQuantity": 200.0 },
 *     { "rawMaterialId": 2, "requiredQuantity": 50.0 }
 *   ]
 * }
 * }</pre>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see ProductCompositionDTO
 * @see com.example.backend.entity.Product
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    /**
     * Código único do produto (ex.: {@code "PRD001"}).
     */
    private String code;

    /**
     * Nome descritivo do produto (ex.: {@code "Pão Francês"}).
     */
    private String name;

    /**
     * Preço de venda por unidade do produto (em R$).
     */
    private Double price;

    /**
     * Lista de composições do produto — cada item define uma matéria-prima
     * e a quantidade necessária para fabricar 1 unidade.
     *
     * <p>Pode ser {@code null} ou vazia para produtos sem composição definida.</p>
     *
     * @see ProductCompositionDTO
     */
    private List<ProductCompositionDTO> compositions;
}

