package com.venkatesh.it.adminmanagementservice.service.impl;

import com.venkatesh.it.adminmanagementservice.dto.report.CategoryReportDTO;
import com.venkatesh.it.adminmanagementservice.dto.report.ProductReportDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReportServiceImpl reportService;

    private ProductReportDTO[] testProducts;
    private CategoryReportDTO[] testCategories;

    @BeforeEach
    void setUp() {
        testProducts = new ProductReportDTO[]{
                ProductReportDTO.builder()
                        .id(1L)
                        .name("Laptop")
                        .price(new java.math.BigDecimal("999.99"))
                        .quantity(50)
                        .categoryName("Electronics")
                        .build(),
                ProductReportDTO.builder()
                        .id(2L)
                        .name("Mouse")
                        .price(new java.math.BigDecimal("29.99"))
                        .quantity(100)
                        .categoryName("Electronics")
                        .build()
        };

        testCategories = new CategoryReportDTO[]{
                CategoryReportDTO.builder()
                        .id(1L)
                        .name("Electronics")
                        .description("Electronic devices")
                        .status("ACTIVE")
                        .build(),
                CategoryReportDTO.builder()
                        .id(2L)
                        .name("Clothing")
                        .description("Clothing items")
                        .status("ACTIVE")
                        .build()
        };
    }

    @Test
    void whenGenerateProductExcelReport_thenReturnByteArray() throws IOException {
        when(restTemplate.getForObject(any(String.class), eq(ProductReportDTO[].class)))
                .thenReturn(testProducts);

        byte[] report = reportService.generateProductExcelReport();

        assertNotNull(report);
        assertTrue(report.length > 0);
    }

    @Test
    void whenGenerateProductExcelReportWithEmptyData_thenReturnByteArray() throws IOException {
        when(restTemplate.getForObject(any(String.class), eq(ProductReportDTO[].class)))
                .thenReturn(null);

        byte[] report = reportService.generateProductExcelReport();

        assertNotNull(report);
    }

    @Test
    void whenGenerateCategoryExcelReport_thenReturnByteArray() throws IOException {
        when(restTemplate.getForObject(any(String.class), eq(CategoryReportDTO[].class)))
                .thenReturn(testCategories);

        byte[] report = reportService.generateCategoryExcelReport();

        assertNotNull(report);
        assertTrue(report.length > 0);
    }

    @Test
    void whenGenerateCategoryExcelReportWithEmptyData_thenReturnByteArray() throws IOException {
        when(restTemplate.getForObject(any(String.class), eq(CategoryReportDTO[].class)))
                .thenReturn(null);

        byte[] report = reportService.generateCategoryExcelReport();

        assertNotNull(report);
    }

    @Test
    void whenGenerateStockExcelReport_thenReturnByteArray() throws IOException {
        when(restTemplate.getForObject(any(String.class), eq(ProductReportDTO[].class)))
                .thenReturn(testProducts);

        byte[] report = reportService.generateStockExcelReport();

        assertNotNull(report);
        assertTrue(report.length > 0);
    }

    @Test
    void whenGenerateStockExcelReportWithEmptyData_thenReturnByteArray() throws IOException {
        when(restTemplate.getForObject(any(String.class), eq(ProductReportDTO[].class)))
                .thenReturn(new ProductReportDTO[0]);

        byte[] report = reportService.generateStockExcelReport();

        assertNotNull(report);
    }
}
