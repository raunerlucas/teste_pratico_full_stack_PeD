package com.example.backend.dto;

import lombok.*;

/**
 * DTO (Data Transfer Object) para criação e atualização de <strong>Matérias-Primas</strong>.
 *
 * <p>Utilizado como corpo de requisição nos endpoints {@code POST} e {@code PUT}
 * de {@code /api/raw-materials}, evitando expor a entidade JPA diretamente na API.</p>
 *
 * <h3>Exemplo de JSON</h3>
 * <pre>{@code
 * {
 *   "code": "MP001",
 *   "name": "Farinha de Trigo",
 *   "stockQuantity": 500.0
 * }
 * }</pre>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see com.example.backend.entity.RawMaterial
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialDTO {

    /**
     * Código único da matéria-prima (ex.: {@code "MP001"}).
     */
    private String code;

    /**
     * Nome descritivo da matéria-prima (ex.: {@code "Farinha de Trigo"}).
     */
    private String name;

    /**
     * Quantidade disponível em estoque.
     * Unidade de medida definida pelo contexto de negócio (kg, litros, unidades, etc.).
     */
    private Double stockQuantity;
}

