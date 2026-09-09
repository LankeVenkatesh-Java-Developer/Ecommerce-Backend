package com.venkatesh.it.productsmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean active;
    private com.venkatesh.it.productsmanagementservice.entity.CategoryStatus status;
}
