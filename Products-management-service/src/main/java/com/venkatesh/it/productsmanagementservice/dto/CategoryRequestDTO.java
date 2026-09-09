package com.venkatesh.it.productsmanagementservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private Boolean active = true;

    private com.venkatesh.it.productsmanagementservice.entity.CategoryStatus status = com.venkatesh.it.productsmanagementservice.entity.CategoryStatus.ACTIVE;
}
