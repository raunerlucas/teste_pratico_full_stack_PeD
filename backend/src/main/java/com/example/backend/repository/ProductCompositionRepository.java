package com.example.backend.repository;

import com.example.backend.entity.ProductComposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCompositionRepository extends JpaRepository<ProductComposition, Long> {

    List<ProductComposition> findByProductId(Long productId);

    void deleteByProductId(Long productId);
}

