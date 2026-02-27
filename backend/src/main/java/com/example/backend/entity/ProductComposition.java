package com.example.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidade JPA que representa a <strong>Composição de um Produto</strong>.
 *
 * <p>Mapeada para a tabela {@code product_composition} no banco de dados H2.
 * Funciona como tabela associativa (N:N com atributo) entre {@link Product}
 * e {@link RawMaterial}, armazenando a quantidade necessária de cada
 * matéria-prima para fabricar <strong>1 unidade</strong> do produto.</p>
 *
 * <h3>Relacionamentos</h3>
 * <ul>
 *   <li>{@code @ManyToOne} com {@link Product} — produto ao qual esta composição pertence.</li>
 *   <li>{@code @ManyToOne} com {@link RawMaterial} — matéria-prima utilizada na composição.</li>
 * </ul>
 *
 * <h3>Exemplo</h3>
 * <p>Se o produto "Pão" precisa de 200g de Farinha, existirá um registro com
 * {@code product = Pão}, {@code rawMaterial = Farinha} e {@code requiredQuantity = 200.0}.</p>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see Product
 * @see RawMaterial
 */
@Entity
@Table(name = "product_composition")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductComposition {

    /**
     * Identificador único gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Produto ao qual esta composição pertence.
     *
     * <p>Carregamento {@code LAZY} — a entidade {@link Product} só é carregada quando acessada.</p>
     * <p>Anotado com {@code @JsonIgnore} para evitar referência circular na serialização JSON.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    /**
     * Matéria-prima utilizada nesta composição.
     *
     * <p>Carregamento {@code LAZY} — a entidade {@link RawMaterial} só é carregada quando acessada.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    /**
     * Quantidade da matéria-prima necessária para fabricar <strong>1 unidade</strong> do produto.
     * A unidade de medida é a mesma definida no campo {@link RawMaterial#getStockQuantity()}.
     */
    @Column(name = "required_quantity", nullable = false)
    private Double requiredQuantity;
}
