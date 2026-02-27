package com.example.backend.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private String code;

    private String name;

    private Double price;

    private List<ProductCompositionDTO> compositions;
}

