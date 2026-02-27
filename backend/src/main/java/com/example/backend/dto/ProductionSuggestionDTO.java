package com.example.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductionSuggestionDTO {

    private String productCode;

    private String productName;

    private int quantity;

    private Double unitPrice;

    private Double totalValue;
}

