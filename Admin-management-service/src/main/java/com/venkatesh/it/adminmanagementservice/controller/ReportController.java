package com.venkatesh.it.adminmanagementservice.controller;

import com.venkatesh.it.adminmanagementservice.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Report Management", description = "APIs for generating Excel reports")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;
    private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @GetMapping("/products/excel")
    @Operation(summary = "Generate product report", description = "Generates an Excel report of all products")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<byte[]> generateProductReport() throws IOException {
        byte[] report = reportService.generateProductExcelReport();

        String filename = "products_report_" + LocalDateTime.now().format(FILE_NAME_FORMATTER) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(report);
    }

    @GetMapping("/categories/excel")
    @Operation(summary = "Generate category report", description = "Generates an Excel report of all categories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<byte[]> generateCategoryReport() throws IOException {
        byte[] report = reportService.generateCategoryExcelReport();

        String filename = "categories_report_" + LocalDateTime.now().format(FILE_NAME_FORMATTER) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(report);
    }

    @GetMapping("/stock/excel")
    @Operation(summary = "Generate stock report", description = "Generates an Excel report of stock/inventory")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<byte[]> generateStockReport() throws IOException {
        byte[] report = reportService.generateStockExcelReport();

        String filename = "stock_report_" + LocalDateTime.now().format(FILE_NAME_FORMATTER) + ".xlsx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.status(HttpStatus.OK)
                .headers(headers)
                .body(report);
    }
}
