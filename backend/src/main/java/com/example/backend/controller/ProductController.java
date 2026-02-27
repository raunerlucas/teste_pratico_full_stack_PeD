package com.example.backend.controller;

import com.example.backend.dto.ProductDTO;
import com.example.backend.entity.Product;
import com.example.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST responsável pelos endpoints de <strong>Produtos</strong> (Products).
 *
 * <p>Expõe operações CRUD completas para o gerenciamento de produtos industriais,
 * incluindo a composição de cada produto (quais matérias-primas e quantidades
 * são necessárias para fabricar 1 unidade).</p>
 *
 * <h3>Base path</h3>
 * <p>{@code /api/products}</p>
 *
 * <h3>Operações disponíveis</h3>
 * <ul>
 *   <li>{@code GET    /api/products}      — Listar todos</li>
 *   <li>{@code GET    /api/products/{id}}  — Buscar por ID</li>
 *   <li>{@code POST   /api/products}      — Cadastrar novo (com composição)</li>
 *   <li>{@code PUT    /api/products/{id}}  — Atualizar existente (redefine composição)</li>
 *   <li>{@code DELETE /api/products/{id}}  — Remover (composições removidas em cascata)</li>
 * </ul>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see ProductService
 * @see Product
 * @see ProductDTO
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(
        name = "Produtos",
        description = "Endpoints para gerenciamento de produtos industriais. "
                + "Permite criar, listar, atualizar e remover produtos, incluindo "
                + "a composição de matérias-primas necessárias para fabricar cada unidade."
)
public class ProductController {

    private final ProductService service;

    // ────────────────────────────────────────────────────────────────────────────
    // GET /api/products — Listar todos os produtos
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Retorna a lista completa de produtos cadastrados no sistema.
     *
     * <p>Cada produto inclui suas composições (matérias-primas e quantidades).
     * A lista pode ser vazia caso não haja produtos cadastrados.</p>
     *
     * @return {@code 200 OK} com a lista de produtos no corpo da resposta
     */
    @GetMapping
    @Operation(
            summary = "Listar todos os produtos",
            description = """
                    Retorna a lista completa de produtos cadastrados no sistema.
                    
                    - Não possui paginação — retorna todos os registros.
                    - Cada produto inclui a lista de **composições** (`compositions`),
                      detalhando as matérias-primas e quantidades necessárias.
                    - A lista pode ser vazia (`[]`) caso não haja produtos cadastrados.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de produtos retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Product.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": 1,
                                        "code": "PRD001",
                                        "name": "Pão Francês",
                                        "price": 12.50,
                                        "compositions": [
                                          {
                                            "id": 1,
                                            "rawMaterial": {
                                              "id": 1,
                                              "code": "MP001",
                                              "name": "Farinha de Trigo",
                                              "stockQuantity": 500.0
                                            },
                                            "requiredQuantity": 200.0
                                          }
                                        ]
                                      }
                                    ]
                                    """)
                    )
            )
    })
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // ────────────────────────────────────────────────────────────────────────────
    // GET /api/products/{id} — Buscar produto por ID
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Busca um produto específico pelo seu identificador único.
     *
     * <p>Retorna o produto com todas as suas composições carregadas.</p>
     *
     * @param id identificador numérico do produto (path variable)
     * @return {@code 200 OK} com o produto encontrado, ou {@code 404 Not Found} se não existir
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar produto por ID",
            description = """
                    Retorna os dados completos de um produto específico, identificado pelo seu `id`
                    numérico (gerado automaticamente no cadastro).
                    
                    A resposta inclui a lista de **composições** (`compositions`), com os dados
                    completos de cada matéria-prima vinculada e a quantidade necessária.
                    
                    Caso o `id` informado não corresponda a nenhum registro, retorna `404 Not Found`.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "code": "PRD001",
                                      "name": "Pão Francês",
                                      "price": 12.50,
                                      "compositions": [
                                        {
                                          "id": 1,
                                          "rawMaterial": {
                                            "id": 1,
                                            "code": "MP001",
                                            "name": "Farinha de Trigo",
                                            "stockQuantity": 500.0
                                          },
                                          "requiredQuantity": 200.0
                                        },
                                        {
                                          "id": 2,
                                          "rawMaterial": {
                                            "id": 2,
                                            "code": "MP002",
                                            "name": "Leite",
                                            "stockQuantity": 200.0
                                          },
                                          "requiredQuantity": 50.0
                                        }
                                      ]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-02-26T10:30:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Product not found with id: 99"
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<Product> getById(
            @Parameter(description = "ID do produto", example = "1", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // POST /api/products — Cadastrar novo produto
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Cadastra um novo produto no sistema, incluindo sua composição de matérias-primas.
     *
     * <p>Para cada item na lista {@code compositions}, o sistema busca a matéria-prima
     * pelo {@code rawMaterialId} e associa ao produto com a {@code requiredQuantity} informada.</p>
     *
     * @param dto dados do produto a ser criado (inclui composições)
     * @return {@code 201 Created} com a entidade persistida (incluindo {@code id} e composições)
     */
    @PostMapping
    @Operation(
            summary = "Cadastrar novo produto",
            description = """
                    Cria um novo produto no sistema com os dados e composições informados.
                    
                    **Campos obrigatórios:**
                    - `code` — código único do produto (ex.: `"PRD001"`). Não pode ser repetido.
                    - `name` — nome descritivo (ex.: `"Pão Francês"`).
                    - `price` — preço de venda por unidade (ex.: `12.50`).
                    
                    **Campo opcional:**
                    - `compositions` — lista de matérias-primas necessárias para fabricar 1 unidade.
                      Cada item contém:
                      - `rawMaterialId` — ID da matéria-prima (deve existir no sistema).
                      - `requiredQuantity` — quantidade necessária por unidade fabricada.
                    
                    **Regras:**
                    - O `id` do produto é gerado automaticamente pelo banco.
                    - Se algum `rawMaterialId` não existir, retorna `404 Not Found`.
                    - Se o `code` já existir, retorna erro de constraint violation.
                    - As composições são persistidas em cascata junto com o produto.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto criado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "code": "PRD001",
                                      "name": "Pão Francês",
                                      "price": 12.50,
                                      "compositions": [
                                        {
                                          "id": 1,
                                          "rawMaterial": {
                                            "id": 1,
                                            "code": "MP001",
                                            "name": "Farinha de Trigo",
                                            "stockQuantity": 500.0
                                          },
                                          "requiredQuantity": 200.0
                                        }
                                      ]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Matéria-prima da composição não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-02-26T10:30:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Raw Material not found with id: 99"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno — possível violação de constraint (código duplicado)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-02-26T10:30:00",
                                      "status": 500,
                                      "error": "Internal Server Error",
                                      "message": "Unique index or primary key violation"
                                    }
                                    """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do produto a ser cadastrado, incluindo composições",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class),
                    examples = @ExampleObject(value = """
                            {
                              "code": "PRD001",
                              "name": "Pão Francês",
                              "price": 12.50,
                              "compositions": [
                                { "rawMaterialId": 1, "requiredQuantity": 200.0 },
                                { "rawMaterialId": 2, "requiredQuantity": 50.0 }
                              ]
                            }
                            """)
            )
    )
    public ResponseEntity<Product> create(@RequestBody ProductDTO dto) {
        Product created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // PUT /api/products/{id} — Atualizar produto
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Atualiza todos os campos de um produto existente, substituindo a composição por completo.
     *
     * <p>As composições anteriores são removidas automaticamente ({@code orphanRemoval = true})
     * e substituídas pelas novas composições informadas no corpo da requisição.</p>
     *
     * @param id  identificador do produto a ser atualizado
     * @param dto novos dados do produto (inclui novas composições)
     * @return {@code 200 OK} com a entidade atualizada, ou {@code 404 Not Found} se não existir
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar produto",
            description = """
                    Atualiza **todos os campos** de um produto existente, incluindo a
                    **substituição completa** da lista de composições.
                    
                    **Comportamento das composições:**
                    - Todas as composições anteriores são **removidas** automaticamente.
                    - As novas composições do body são **criadas** e associadas ao produto.
                    - Isso garante que a composição reflita exatamente o que foi enviado.
                    
                    **Campos obrigatórios no body:**
                    - `code`, `name`, `price` — dados básicos do produto.
                    - `compositions` — nova lista de composições (pode ser vazia `[]`).
                    
                    **Regras:**
                    - Se o `id` do produto não existir, retorna `404 Not Found`.
                    - Se algum `rawMaterialId` da composição não existir, retorna `404 Not Found`.
                    - Se o novo `code` conflitar com outro produto, retorna erro de constraint.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto atualizado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Product.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "code": "PRD001",
                                      "name": "Pão Francês Premium",
                                      "price": 15.00,
                                      "compositions": [
                                        {
                                          "id": 3,
                                          "rawMaterial": {
                                            "id": 1,
                                            "code": "MP001",
                                            "name": "Farinha de Trigo",
                                            "stockQuantity": 500.0
                                          },
                                          "requiredQuantity": 250.0
                                        }
                                      ]
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto ou matéria-prima da composição não encontrada",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-02-26T10:30:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Product not found with id: 99"
                                    }
                                    """)
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Novos dados do produto, incluindo composições atualizadas",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProductDTO.class),
                    examples = @ExampleObject(value = """
                            {
                              "code": "PRD001",
                              "name": "Pão Francês Premium",
                              "price": 15.00,
                              "compositions": [
                                { "rawMaterialId": 1, "requiredQuantity": 250.0 },
                                { "rawMaterialId": 2, "requiredQuantity": 75.0 }
                              ]
                            }
                            """)
            )
    )
    public ResponseEntity<Product> update(
            @Parameter(description = "ID do produto a ser atualizado", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody ProductDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // DELETE /api/products/{id} — Remover produto
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Remove um produto do sistema pelo seu identificador.
     *
     * <p>As composições associadas ao produto são removidas automaticamente
     * via {@code CascadeType.ALL}, sem necessidade de exclusão manual.</p>
     *
     * @param id identificador do produto a ser removido
     * @return {@code 204 No Content} se removido com sucesso, ou {@code 404 Not Found} se não existir
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Remover produto",
            description = """
                    Remove permanentemente um produto do sistema pelo seu `id`.
                    
                    **Comportamento em cascata:**
                    - Todas as composições (`product_composition`) vinculadas ao produto
                      são **removidas automaticamente** (`CascadeType.ALL`).
                    - Não é necessário excluir as composições manualmente antes.
                    
                    **Regras:**
                    - Se o `id` não existir, retorna `404 Not Found`.
                    - Retorna `204 No Content` (sem corpo) em caso de sucesso.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Produto removido com sucesso (sem corpo de resposta)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-02-26T10:30:00",
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "Product not found with id: 99"
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do produto a ser removido", example = "1", required = true)
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

