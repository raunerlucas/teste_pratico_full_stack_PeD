package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade JPA que representa um <strong>Produto</strong> industrial.
 *
 * <p>Mapeada para a tabela {@code product} no banco de dados H2.
 * Cada produto possui um código único, nome, preço de venda e uma lista
 * de composições que define quais matérias-primas (e em que quantidade)
 * são necessárias para fabricar 1 unidade.</p>
 *
 * <h3>Relacionamentos</h3>
 * <ul>
 *   <li>{@code @OneToMany} com {@link ProductComposition} — composição do produto
 *       (cascade ALL + orphanRemoval, garantindo remoção automática de composições órfãs).</li>
 * </ul>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see ProductComposition
 * @see RawMaterial
 */
@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /**
     * Identificador único gerado automaticamente pelo banco de dados.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código único do produto (ex.: {@code "PRD001"}).
     * Utilizado como identificador de negócio.
     */
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * Nome descritivo do produto (ex.: {@code "Pão Francês"}).
     */
    @Column(nullable = false)
    private String name;

    /**
     * Preço de venda por unidade do produto (em R$).
     */
    @Column(nullable = false)
    private Double price;

    /**
     * Lista de composições do produto — define quais matérias-primas e quantidades
     * são necessárias para fabricar 1 unidade.
     *
     * <p>Gerenciada em cascata: ao salvar/deletar o produto, as composições
     * são persistidas/removidas automaticamente ({@code CascadeType.ALL} + {@code orphanRemoval = true}).</p>
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ProductComposition> compositions = new ArrayList<>();
}
