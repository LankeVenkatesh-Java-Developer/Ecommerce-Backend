package com.venkatesh.it.productsmanagementservice.specification;

import com.venkatesh.it.productsmanagementservice.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

    public static Specification<Product> withFilters(Long categoryId, String searchKeyword, Product.ProductStatus status) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (categoryId != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }

            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String keyword = "%" + searchKeyword.toLowerCase().trim() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), keyword);
                Predicate descriptionPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("description")), keyword);
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            if (status != null) {
                predicate = criteriaBuilder.and(predicate, 
                    criteriaBuilder.equal(root.get("status"), status));
            }

            return predicate;
        };
    }
}
