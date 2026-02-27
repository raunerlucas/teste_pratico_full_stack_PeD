package com.example.backend.service;

import com.example.backend.dto.RawMaterialDTO;
import com.example.backend.entity.RawMaterial;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.RawMaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável pela lógica de negócio relacionada a Matérias-Primas (Raw Materials).
 *
 * <p>Fornece operações CRUD completas para o gerenciamento de insumos industriais,
 * incluindo controle de estoque (quantidade disponível).</p>
 *
 * @author Equipe Backend
 * @version 1.0.0
 * @see RawMaterial
 * @see RawMaterialDTO
 * @see RawMaterialRepository
 */
@Service
@RequiredArgsConstructor
public class RawMaterialService {

    private final RawMaterialRepository repository;

    /**
     * Retorna todas as matérias-primas cadastradas.
     *
     * @return lista de {@link RawMaterial} — pode ser vazia caso não haja registros
     */
    public List<RawMaterial> findAll() {
        return repository.findAll();
    }

    /**
     * Busca uma matéria-prima pelo seu identificador único.
     *
     * @param id identificador da matéria-prima
     * @return a entidade {@link RawMaterial} correspondente
     * @throws ResourceNotFoundException se nenhuma matéria-prima for encontrada com o {@code id} informado
     */
    public RawMaterial findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Raw Material not found with id: " + id));
    }

    /**
     * Cadastra uma nova matéria-prima no sistema.
     *
     * <p>Constrói a entidade a partir do DTO recebido e persiste no banco de dados.</p>
     *
     * @param dto dados da matéria-prima a ser criada ({@code code}, {@code name}, {@code stockQuantity})
     * @return a entidade {@link RawMaterial} persistida, já com o {@code id} gerado
     */
    @Transactional
    public RawMaterial create(RawMaterialDTO dto) {
        RawMaterial rawMaterial = RawMaterial.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .stockQuantity(dto.getStockQuantity())
                .build();

        return repository.save(rawMaterial);
    }

    /**
     * Atualiza os dados de uma matéria-prima existente.
     *
     * <p>Busca a entidade pelo {@code id}, aplica as alterações do DTO e persiste.</p>
     *
     * @param id  identificador da matéria-prima a ser atualizada
     * @param dto novos dados ({@code code}, {@code name}, {@code stockQuantity})
     * @return a entidade {@link RawMaterial} atualizada
     * @throws ResourceNotFoundException se nenhuma matéria-prima for encontrada com o {@code id} informado
     */
    @Transactional
    public RawMaterial update(Long id, RawMaterialDTO dto) {
        RawMaterial rawMaterial = findById(id);

        rawMaterial.setCode(dto.getCode());
        rawMaterial.setName(dto.getName());
        rawMaterial.setStockQuantity(dto.getStockQuantity());

        return repository.save(rawMaterial);
    }

    /**
     * Remove uma matéria-prima pelo seu identificador.
     *
     * @param id identificador da matéria-prima a ser removida
     * @throws ResourceNotFoundException se nenhuma matéria-prima for encontrada com o {@code id} informado
     */
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Raw Material not found with id: " + id);
        }
        repository.deleteById(id);
    }
}

