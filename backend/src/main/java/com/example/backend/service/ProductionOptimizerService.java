package com.example.backend.service;

import com.example.backend.dto.ProductionSuggestionDTO;
import com.example.backend.entity.Product;
import com.example.backend.entity.ProductComposition;
import com.example.backend.entity.RawMaterial;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.RawMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço responsável pelo algoritmo de otimização de produção industrial.
 *
 * <p>Analisa o estoque atual de matérias-primas e sugere <strong>quais produtos fabricar
 * e em que quantidade</strong> para obter o <strong>maior valor total de venda</strong>,
 * resolvendo conflitos quando dois ou mais produtos disputam a mesma matéria-prima.</p>
 *
 * <h3>Estratégia utilizada — Algoritmo Guloso (Greedy)</h3>
 * <ol>
 *   <li>Carrega todos os produtos e o estoque atual de matérias-primas.</li>
 *   <li>Ordena os produtos por <strong>preço unitário decrescente</strong> (maior valor primeiro).</li>
 *   <li>Para cada produto, calcula o número máximo de unidades fabricáveis com o estoque disponível.</li>
 *   <li>Consome as matérias-primas do estoque virtual e registra a sugestão.</li>
 *   <li>Repete até esgotar a lista de produtos.</li>
 * </ol>
 *
 * <p><strong>Complexidade:</strong> O(P × C), onde P = número de produtos e C = composições por produto.</p>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see Product
 * @see ProductComposition
 * @see RawMaterial
 * @see ProductionSuggestionDTO
 */
@Service
@RequiredArgsConstructor
public class ProductionOptimizerService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    /**
     * Executa o algoritmo de otimização e retorna as sugestões de produção.
     *
     * <p>Cada item da lista representa um produto que pode ser fabricado,
     * com a quantidade sugerida, preço unitário e valor total
     * ({@code totalValue = quantity × unitPrice}).</p>
     *
     * <p>A soma de todos os {@code totalValue} é o <strong>valor máximo de venda</strong>
     * que a fábrica pode atingir com o estoque atual.</p>
     *
     * @return lista de {@link ProductionSuggestionDTO} ordenada por prioridade de produção
     *         — vazia se não houver estoque suficiente para fabricar nenhum produto
     */
    public List<ProductionSuggestionDTO> optimize() {
        List<Product> products = productRepository.findAll();
        List<RawMaterial> rawMaterials = rawMaterialRepository.findAll();

        // Mapa de estoque: rawMaterialId -> quantidade disponível
        Map<Long, Double> stockMap = rawMaterials.stream()
                .collect(Collectors.toMap(RawMaterial::getId, RawMaterial::getStockQuantity));

        // Ordena produtos por preço decrescente (prioriza os de maior valor)
        products.sort(Comparator.comparingDouble(Product::getPrice).reversed());

        List<ProductionSuggestionDTO> suggestions = new ArrayList<>();

        for (Product product : products) {
            if (product.getCompositions() == null || product.getCompositions().isEmpty()) {
                continue;
            }

            int maxUnits = calculateMaxUnits(product, stockMap);

            if (maxUnits > 0) {
                consumeStock(product, maxUnits, stockMap);

                ProductionSuggestionDTO suggestion = ProductionSuggestionDTO.builder()
                        .productCode(product.getCode())
                        .productName(product.getName())
                        .quantity(maxUnits)
                        .unitPrice(product.getPrice())
                        .totalValue(product.getPrice() * maxUnits)
                        .build();

                suggestions.add(suggestion);
            }
        }

        return suggestions;
    }

    /**
     * Calcula o número máximo de unidades fabricáveis de um produto
     * com base no estoque disponível de matérias-primas.
     *
     * <p>O gargalo é determinado pela matéria-prima que permite fabricar
     * o <strong>menor número de unidades</strong> (recurso mais escasso).</p>
     *
     * <p>Exemplo: se um produto precisa de 100g de Farinha (estoque: 500g → 5 unidades)
     * e 50ml de Leite (estoque: 100ml → 2 unidades), o máximo fabricável é <strong>2</strong>.</p>
     *
     * @param product  produto cuja capacidade de fabricação será calculada
     * @param stockMap mapa de estoque atual (rawMaterialId → quantidade disponível)
     * @return número máximo de unidades fabricáveis — {@code 0} se não houver estoque suficiente
     */
    private int calculateMaxUnits(Product product, Map<Long, Double> stockMap) {
        int maxUnits = Integer.MAX_VALUE;

        for (ProductComposition composition : product.getCompositions()) {
            Long rawMaterialId = composition.getRawMaterial().getId();
            Double available = stockMap.getOrDefault(rawMaterialId, 0.0);
            Double required = composition.getRequiredQuantity();

            if (required <= 0) {
                continue;
            }

            int possibleUnits = (int) Math.floor(available / required);
            maxUnits = Math.min(maxUnits, possibleUnits);
        }

        return maxUnits == Integer.MAX_VALUE ? 0 : maxUnits;
    }

    /**
     * Consome o estoque de matérias-primas após decidir produzir N unidades de um produto.
     *
     * <p>Deduz do mapa de estoque virtual a quantidade total consumida por cada
     * matéria-prima: {@code consumido = requiredQuantity × units}.</p>
     *
     * <p><strong>Nota:</strong> esta operação altera apenas o mapa em memória,
     * sem persistir as alterações no banco de dados.</p>
     *
     * @param product  produto que será fabricado
     * @param units    quantidade de unidades a produzir
     * @param stockMap mapa de estoque atual (rawMaterialId → quantidade disponível) — será modificado in-place
     */
    private void consumeStock(Product product, int units, Map<Long, Double> stockMap) {
        for (ProductComposition composition : product.getCompositions()) {
            Long rawMaterialId = composition.getRawMaterial().getId();
            Double currentStock = stockMap.getOrDefault(rawMaterialId, 0.0);
            Double consumed = composition.getRequiredQuantity() * units;

            stockMap.put(rawMaterialId, currentStock - consumed);
        }
    }
}

