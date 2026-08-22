package com.ashok.it.adminservice.controller;

import com.ashok.it.adminservice.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    private final ReportService reportService;
    
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    @GetMapping("/products")
    public Mono<ResponseEntity<byte[]>> generateProductsReport() {
        return reportService.generateProductsReport()
                .map(data -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=products_report_" + getCurrentTimestamp() + ".xlsx")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(data));
    }
    
    @GetMapping("/categories")
    public Mono<ResponseEntity<byte[]>> generateCategoriesReport() {
        return reportService.generateCategoriesReport()
                .map(data -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=categories_report_" + getCurrentTimestamp() + ".xlsx")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(data));
    }
    
    @GetMapping("/combined")
    public Mono<ResponseEntity<byte[]>> generateCombinedReport() {
        return reportService.generateCombinedReport()
                .map(data -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=combined_report_" + getCurrentTimestamp() + ".xlsx")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(data));
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }
}
