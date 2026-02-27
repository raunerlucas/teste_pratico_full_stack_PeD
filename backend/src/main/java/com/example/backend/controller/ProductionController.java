package com.example.backend.controller;

import com.example.backend.dto.ProductionSuggestionDTO;
import com.example.backend.service.ProductionOptimizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST responsável pelo endpoint de <strong>Otimização de Produção</strong>.
 *
 * <p>Expõe o algoritmo de otimização que analisa o estoque atual de matérias-primas
 * e sugere <strong>quais produtos fabricar e em que quantidade</strong> para obter
 * o <strong>maior valor total de venda</strong> possível.</p>
 *
 * <h3>Base path</h3>
 * <p>{@code /api/production}</p>
 *
 * <h3>Operações disponíveis</h3>
 * <ul>
 *   <li>{@code GET /api/production/optimize} — Calcular sugestão ótima de produção</li>
 * </ul>
 *
 * <h3>Algoritmo utilizado</h3>
 * <p>Algoritmo Guloso (Greedy) que ordena os produtos por preço unitário decrescente
 * e produz o máximo possível de cada um, consumindo o estoque disponível. Quando dois
 * ou mais produtos disputam a mesma matéria-prima, o de maior preço é priorizado.</p>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see ProductionOptimizerService
 * @see ProductionSuggestionDTO
 */
@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
@Tag(
        name = "Otimização de Produção",
        description = "Endpoint para cálculo de otimização de produção industrial. "
                + "Analisa o estoque atual de matérias-primas e sugere quais produtos fabricar "
                + "e em que quantidade para maximizar o valor total de venda."
)
public class ProductionController {

    private final ProductionOptimizerService service;

    // ────────────────────────────────────────────────────────────────────────────
    // GET /api/production/optimize — Calcular sugestão ótima de produção
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Executa o algoritmo de otimização e retorna a lista de sugestões de produção.
     *
     * <p>O algoritmo:</p>
     * <ol>
     *   <li>Carrega todos os produtos cadastrados e o estoque atual de matérias-primas.</li>
     *   <li>Ordena os produtos por <strong>preço unitário decrescente</strong> (maior valor primeiro).</li>
     *   <li>Para cada produto, calcula o número máximo de unidades fabricáveis com o estoque disponível.</li>
     *   <li>Consome as matérias-primas do estoque virtual e registra a sugestão.</li>
     *   <li>Repete até esgotar a lista de produtos.</li>
     * </ol>
     *
     * <p>Cada item retornado contém:</p>
     * <ul>
     *   <li>{@code productCode} — código do produto sugerido</li>
     *   <li>{@code productName} — nome do produto</li>
     *   <li>{@code quantity} — quantidade de unidades a fabricar</li>
     *   <li>{@code unitPrice} — preço de venda por unidade</li>
     *   <li>{@code totalValue} — valor total ({@code quantity × unitPrice})</li>
     * </ul>
     *
     * <p><strong>Nota:</strong> a soma de todos os {@code totalValue} é o valor máximo
     * de venda que a fábrica pode atingir com o estoque atual. O cálculo é feito
     * em memória e <strong>não altera</strong> o estoque real no banco de dados.</p>
     *
     * @return {@code 200 OK} com a lista de sugestões de produção — vazia se não houver
     *         estoque suficiente para fabricar nenhum produto
     */
    @GetMapping("/optimize")
    @Operation(
            summary = "Calcular sugestão ótima de produção",
            description = """
                    Executa o **algoritmo de otimização gulosa (Greedy)** para maximizar
                    o valor total de venda com base no estoque atual de matérias-primas.
                    
                    ### Como funciona
                    1. Carrega todos os produtos e o estoque de matérias-primas do banco.
                    2. Ordena os produtos por **preço unitário decrescente** (maior valor primeiro).
                    3. Para cada produto, calcula o **máximo de unidades fabricáveis** — o gargalo
                       é a matéria-prima mais escassa na composição.
                    4. Consome as matérias-primas do estoque virtual e registra a sugestão.
                    5. Repete para o próximo produto até esgotar a lista.
                    
                    ### Resolução de conflitos
                    Quando dois ou mais produtos disputam a mesma matéria-prima,
                    o de **maior preço** é priorizado (produzido primeiro), e o estoque
                    remanescente é utilizado para os demais.
                    
                    ### Importante
                    - O cálculo é feito **em memória** — **não altera** o estoque real no banco.
                    - Produtos sem composição definida são ignorados.
                    - Se não houver estoque suficiente para nenhum produto, retorna lista vazia `[]`.
                    - Fórmula: `totalValue = quantity × unitPrice`.
                    - A **soma** de todos os `totalValue` = **valor máximo de venda** da fábrica.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Sugestões de produção calculadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductionSuggestionDTO.class)),
                            examples = {
                                    @ExampleObject(
                                            name = "Produção otimizada com múltiplos produtos",
                                            summary = "Cenário com estoque suficiente para fabricar vários produtos",
                                            description = """
                                                    O algoritmo priorizou o Bolo (maior preço: R$ 35.00) e fabricou 2 unidades,
                                                    consumindo parte do estoque. Com o estoque restante, fabricou 5 unidades
                                                    de Pão Francês (R$ 12.50). Valor total de venda: R$ 132.50.
                                                    """,
                                            value = """
                                                    [
                                                      {
                                                        "productCode": "PRD003",
                                                        "productName": "Bolo de Chocolate",
                                                        "quantity": 2,
                                                        "unitPrice": 35.00,
                                                        "totalValue": 70.00
                                                      },
                                                      {
                                                        "productCode": "PRD001",
                                                        "productName": "Pão Francês",
                                                        "quantity": 5,
                                                        "unitPrice": 12.50,
                                                        "totalValue": 62.50
                                                      }
                                                    ]
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Apenas um produto viável",
                                            summary = "Estoque suficiente para apenas um tipo de produto",
                                            value = """
                                                    [
                                                      {
                                                        "productCode": "PRD001",
                                                        "productName": "Pão Francês",
                                                        "quantity": 10,
                                                        "unitPrice": 12.50,
                                                        "totalValue": 125.00
                                                      }
                                                    ]
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Estoque insuficiente",
                                            summary = "Sem estoque para fabricar qualquer produto",
                                            description = "Retorna lista vazia quando não há matéria-prima "
                                                    + "suficiente para fabricar sequer 1 unidade de qualquer produto.",
                                            value = "[]"
                                    )
                            }
                    )
            )
    })
    public ResponseEntity<List<ProductionSuggestionDTO>> optimize() {
        return ResponseEntity.ok(service.optimize());
    }
}

