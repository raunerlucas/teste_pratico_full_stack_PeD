package com.example.backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialDTO {

    private String code;

    private String name;

    private Double stockQuantity;
}

