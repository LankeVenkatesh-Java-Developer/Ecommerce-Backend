package com.venkatesh.it.cartmanagementservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;
    private Long productId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("imageUrl")
    private String imageUrl;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("brand")
    private String brand;

    private Integer quantity;

    @JsonProperty("stockQuantity")
    private Integer stockQuantity;

    private Double price;
    private Double total;
}
