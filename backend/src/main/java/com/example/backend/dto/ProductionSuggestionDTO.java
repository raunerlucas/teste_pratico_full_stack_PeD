package com.example.backend.dto;

import lombok.*;

/**
 * DTO (Data Transfer Object) de <strong>Sugestão de Produção</strong>.
 *
 * <p>Retornado pelo endpoint {@code GET /api/production/optimize} como resultado
 * do algoritmo de otimização. Cada instância representa um produto que pode
 * ser fabricado com o estoque atual, incluindo a quantidade sugerida e o
 * valor total de venda correspondente.</p>
 *
 * <h3>Cálculo</h3>
 * <p>{@code totalValue = quantity × unitPrice}</p>
 * <p>A soma de todos os {@code totalValue} da lista é o <strong>valor máximo de venda</strong>
 * que a fábrica pode atingir com o estoque atual de matérias-primas.</p>
 *
 * <h3>Exemplo de JSON</h3>
 * <pre>{@code
 * {
 *   "productCode": "PRD001",
 *   "productName": "Pão Francês",
 *   "quantity": 5,
 *   "unitPrice": 12.50,
 *   "totalValue": 62.50
 * }
 * }</pre>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see com.example.backend.service.ProductionOptimizerService
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionSuggestionDTO {

    /**
     * Código único do produto sugerido (ex.: {@code "PRD001"}).
     */
    private String productCode;

    /**
     * Nome descritivo do produto sugerido (ex.: {@code "Pão Francês"}).
     */
    private String productName;

    /**
     * Quantidade de unidades sugeridas para fabricação.
     * Representa o máximo possível com o estoque disponível no momento do cálculo.
     */
    private int quantity;

    /**
     * Preço de venda por unidade do produto (em R$).
     */
    private Double unitPrice;

    /**
     * Valor total de venda para a quantidade sugerida.
     * Calculado como {@code quantity × unitPrice}.
     */
    private Double totalValue;
}

