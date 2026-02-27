package com.example.backend.controller;

import com.example.backend.dto.RawMaterialDTO;
import com.example.backend.entity.RawMaterial;
import com.example.backend.service.RawMaterialService;
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
 * Controller REST responsável pelos endpoints de <strong>Matérias-Primas</strong> (Raw Materials).
 *
 * <p>Expõe operações CRUD completas para o gerenciamento de insumos industriais,
 * incluindo controle de estoque (quantidade disponível).</p>
 *
 * <h3>Base path</h3>
 * <p>{@code /api/raw-materials}</p>
 *
 * <h3>Operações disponíveis</h3>
 * <ul>
 *   <li>{@code GET    /api/raw-materials}      — Listar todas</li>
 *   <li>{@code GET    /api/raw-materials/{id}}  — Buscar por ID</li>
 *   <li>{@code POST   /api/raw-materials}      — Cadastrar nova</li>
 *   <li>{@code PUT    /api/raw-materials/{id}}  — Atualizar existente</li>
 *   <li>{@code DELETE /api/raw-materials/{id}}  — Remover</li>
 * </ul>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see RawMaterialService
 * @see RawMaterial
 * @see RawMaterialDTO
 */
@RestController
@RequestMapping("/api/raw-materials")
@RequiredArgsConstructor
@Tag(
        name = "Matérias-Primas",
        description = "Endpoints para gerenciamento de matérias-primas (insumos industriais). "
                + "Permite criar, listar, atualizar e remover insumos com controle de estoque."
)
public class RawMaterialController {

    private final RawMaterialService service;

    // ────────────────────────────────────────────────────────────────────────────
    // GET /api/raw-materials — Listar todas as matérias-primas
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Retorna a lista completa de matérias-primas cadastradas no sistema.
     *
     * <p>Não possui paginação — retorna todos os registros.
     * A lista pode ser vazia caso não haja matérias-primas cadastradas.</p>
     *
     * @return {@code 200 OK} com a lista de matérias-primas no corpo da resposta
     */
    @GetMapping
    @Operation(
            summary = "Listar todas as matérias-primas",
            description = """
                    Retorna a lista completa de matérias-primas cadastradas no sistema.
                    
                    - Não possui paginação — retorna todos os registros.
                    - A lista pode ser vazia (`[]`) caso não haja matérias-primas cadastradas.
                    - Cada item contém: `id`, `code`, `name` e `stockQuantity`.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de matérias-primas retornada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RawMaterial.class)),
                            examples = @ExampleObject(value = """
                                    [
                                      {
                                        "id": 1,
                                        "code": "MP001",
                                        "name": "Farinha de Trigo",
                                        "stockQuantity": 500.0
                                      },
                                      {
                                        "id": 2,
                                        "code": "MP002",
                                        "name": "Leite",
                                        "stockQuantity": 200.0
                                      }
                                    ]
                                    """)
                    )
            )
    })
    public ResponseEntity<List<RawMaterial>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // ────────────────────────────────────────────────────────────────────────────
    // GET /api/raw-materials/{id} — Buscar matéria-prima por ID
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Busca uma matéria-prima específica pelo seu identificador único.
     *
     * @param id identificador numérico da matéria-prima (path variable)
     * @return {@code 200 OK} com a matéria-prima encontrada, ou {@code 404 Not Found} se não existir
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar matéria-prima por ID",
            description = """
                    Retorna os dados completos de uma matéria-prima específica,
                    identificada pelo seu `id` numérico (gerado automaticamente no cadastro).
                    
                    Caso o `id` informado não corresponda a nenhum registro, retorna `404 Not Found`
                    com uma mensagem descritiva no corpo da resposta.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Matéria-prima encontrada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RawMaterial.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "code": "MP001",
                                      "name": "Farinha de Trigo",
                                      "stockQuantity": 500.0
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Matéria-prima não encontrada",
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
            )
    })
    public ResponseEntity<RawMaterial> getById(
            @Parameter(description = "ID da matéria-prima", example = "1", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // POST /api/raw-materials — Cadastrar nova matéria-prima
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Cadastra uma nova matéria-prima no sistema.
     *
     * <p>O campo {@code code} deve ser único — caso já exista uma matéria-prima
     * com o mesmo código, o banco lançará uma exceção de constraint violation.</p>
     *
     * @param dto dados da matéria-prima a ser criada
     * @return {@code 201 Created} com a entidade persistida (incluindo {@code id} gerado)
     */
    @PostMapping
    @Operation(
            summary = "Cadastrar nova matéria-prima",
            description = """
                    Cria uma nova matéria-prima no sistema com os dados informados no corpo da requisição.
                    
                    **Campos obrigatórios:**
                    - `code` — código único (ex.: `"MP001"`). Não pode ser repetido.
                    - `name` — nome descritivo (ex.: `"Farinha de Trigo"`).
                    - `stockQuantity` — quantidade em estoque (ex.: `500.0`).
                    
                    **Regras:**
                    - O `id` é gerado automaticamente pelo banco e retornado na resposta.
                    - Se o `code` já existir, retorna erro de constraint violation.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Matéria-prima criada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RawMaterial.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "code": "MP001",
                                      "name": "Farinha de Trigo",
                                      "stockQuantity": 500.0
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
            description = "Dados da matéria-prima a ser cadastrada",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RawMaterialDTO.class),
                    examples = @ExampleObject(value = """
                            {
                              "code": "MP001",
                              "name": "Farinha de Trigo",
                              "stockQuantity": 500.0
                            }
                            """)
            )
    )
    public ResponseEntity<RawMaterial> create(@RequestBody RawMaterialDTO dto) {
        RawMaterial created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // PUT /api/raw-materials/{id} — Atualizar matéria-prima
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Atualiza todos os campos de uma matéria-prima existente.
     *
     * <p>Substitui completamente os dados da matéria-prima identificada pelo {@code id}
     * com os novos valores fornecidos no corpo da requisição.</p>
     *
     * @param id  identificador da matéria-prima a ser atualizada
     * @param dto novos dados da matéria-prima
     * @return {@code 200 OK} com a entidade atualizada, ou {@code 404 Not Found} se não existir
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar matéria-prima",
            description = """
                    Atualiza **todos os campos** de uma matéria-prima existente (substituição completa).
                    
                    **Campos obrigatórios no body:**
                    - `code` — novo código (ou manter o mesmo).
                    - `name` — novo nome.
                    - `stockQuantity` — nova quantidade em estoque.
                    
                    **Regras:**
                    - Se o `id` não existir, retorna `404 Not Found`.
                    - Se o novo `code` conflitar com outra matéria-prima, retorna erro de constraint.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Matéria-prima atualizada com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RawMaterial.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "code": "MP001",
                                      "name": "Farinha de Trigo Integral",
                                      "stockQuantity": 750.0
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Matéria-prima não encontrada",
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
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Novos dados da matéria-prima",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RawMaterialDTO.class),
                    examples = @ExampleObject(value = """
                            {
                              "code": "MP001",
                              "name": "Farinha de Trigo Integral",
                              "stockQuantity": 750.0
                            }
                            """)
            )
    )
    public ResponseEntity<RawMaterial> update(
            @Parameter(description = "ID da matéria-prima a ser atualizada", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody RawMaterialDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    // ────────────────────────────────────────────────────────────────────────────
    // DELETE /api/raw-materials/{id} — Remover matéria-prima
    // ────────────────────────────────────────────────────────────────────────────

    /**
     * Remove uma matéria-prima do sistema pelo seu identificador.
     *
     * <p><strong>Atenção:</strong> se a matéria-prima estiver sendo utilizada em alguma
     * composição de produto ({@code product_composition}), a remoção pode falhar
     * por violação de integridade referencial.</p>
     *
     * @param id identificador da matéria-prima a ser removida
     * @return {@code 204 No Content} se removida com sucesso, ou {@code 404 Not Found} se não existir
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Remover matéria-prima",
            description = """
                    Remove permanentemente uma matéria-prima do sistema pelo seu `id`.
                    
                    **Regras:**
                    - Se o `id` não existir, retorna `404 Not Found`.
                    - Se a matéria-prima estiver vinculada a alguma composição de produto
                      (`product_composition`), a remoção pode falhar por violação de integridade referencial.
                    - Retorna `204 No Content` (sem corpo) em caso de sucesso.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Matéria-prima removida com sucesso (sem corpo de resposta)"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Matéria-prima não encontrada",
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
                    description = "Erro interno — possível violação de integridade referencial",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "timestamp": "2026-02-26T10:30:00",
                                      "status": 500,
                                      "error": "Internal Server Error",
                                      "message": "Referential integrity constraint violation"
                                    }
                                    """)
                    )
            )
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID da matéria-prima a ser removida", example = "1", required = true)
            @PathVariable Long id
    ) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

