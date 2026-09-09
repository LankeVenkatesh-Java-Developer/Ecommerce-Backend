package com.venkatesh.it.adminmanagementservice.service.impl;

import com.venkatesh.it.adminmanagementservice.dto.report.CategoryReportDTO;
import com.venkatesh.it.adminmanagementservice.dto.report.ProductReportDTO;
import com.venkatesh.it.adminmanagementservice.service.ReportService;
import com.venkatesh.it.adminmanagementservice.util.ExcelReportUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final RestTemplate restTemplate;

    @Value("${products.service.base-url:http://localhost:8083}")
    private String productsServiceBaseUrl;

    @Override
    public byte[] generateProductExcelReport() throws IOException {
        log.info("Generating product Excel report from Products Service");

        String url = productsServiceBaseUrl + "/api/products/admin/all?page=0&size=10000";
        ProductReportDTO[] productsArray = restTemplate.getForObject(url, ProductReportDTO[].class);
        List<ProductReportDTO> products = Arrays.asList(productsArray != null ? productsArray : new ProductReportDTO[0]);

        byte[] report = ExcelReportUtil.generateProductReport(products);
        log.info("Product Excel report generated successfully with {} records", products.size());

        return report;
    }

    @Override
    public byte[] generateCategoryExcelReport() throws IOException {
        log.info("Generating category Excel report from Products Service");

        String url = productsServiceBaseUrl + "/api/categories/admin/all";
        CategoryReportDTO[] categoriesArray = restTemplate.getForObject(url, CategoryReportDTO[].class);
        List<CategoryReportDTO> categories = Arrays.asList(categoriesArray != null ? categoriesArray : new CategoryReportDTO[0]);

        byte[] report = ExcelReportUtil.generateCategoryReport(categories);
        log.info("Category Excel report generated successfully with {} records", categories.size());

        return report;
    }

    @Override
    public byte[] generateStockExcelReport() throws IOException {
        log.info("Generating stock Excel report from Products Service");

        String url = productsServiceBaseUrl + "/api/products/admin/all?page=0&size=10000";
        ProductReportDTO[] productsArray = restTemplate.getForObject(url, ProductReportDTO[].class);
        List<ProductReportDTO> products = Arrays.asList(productsArray != null ? productsArray : new ProductReportDTO[0]);

        byte[] report = ExcelReportUtil.generateStockReport(products);
        log.info("Stock Excel report generated successfully with {} records", products.size());

        return report;
    }
}
