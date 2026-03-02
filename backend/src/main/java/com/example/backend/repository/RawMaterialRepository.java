package com.example.backend.repository;

import com.example.backend.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {

    Optional<RawMaterial> findByCode(String code);

    boolean existsByCode(String code);

    /**
     * Returns the highest code matching the pattern 'MP___' (e.g. MP001, MP999).
     * Used to generate the next sequential code for new raw materials.
     */
    @Query("SELECT MAX(r.code) FROM RawMaterial r WHERE r.code LIKE 'MP%'")
    Optional<String> findMaxCodeWithPrefix();
}

