package com.venkatesh.it.ordermanagementservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cart-management-service")
public interface CartClient {

    @DeleteMapping("/cart/admin/clear/{userId}")
    void clearCart(@PathVariable Long userId);
}
