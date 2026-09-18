package com.venkatesh.it.cartmanagementservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    
    private Long id;
    private Long userId;
    
    @JsonProperty("total")
    private Double total;
    
    private Integer totalItems;
    private List<CartItemResponse> items;
}
