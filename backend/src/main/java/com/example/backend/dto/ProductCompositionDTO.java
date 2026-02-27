package com.example.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCompositionDTO {

    private Long rawMaterialId;

    private Double requiredQuantity;
}

