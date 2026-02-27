package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade JPA que representa uma <strong>Matéria-Prima</strong> (insumo industrial).
 *
 * <p>Mapeada para a tabela {@code raw_material} no banco de dados H2.
 * Cada matéria-prima possui um código único, nome descritivo e a quantidade
 * atualmente disponível em estoque.</p>
 *
 * <h3>Relacionamentos</h3>
 * <ul>
 *   <li>Referenciada por {@link ProductComposition} — indica quais produtos utilizam esta matéria-prima.</li>
 * </ul>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see ProductComposition
 */
@Entity
@Table(name = "raw_material")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterial {

    /**
     * Identificador único gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código único da matéria-prima (ex.: {@code "MP001"}).
     * Utilizado como identificador de negócio.
     */
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * Nome descritivo da matéria-prima (ex.: {@code "Farinha de Trigo"}).
     */
    @Column(nullable = false)
    private String name;

    /**
     * Quantidade atualmente disponível em estoque.
     * Unidade de medida definida pelo contexto de negócio (kg, litros, unidades, etc.).
     */
    @Column(name = "stock_quantity", nullable = false)
    private Double stockQuantity;
}

