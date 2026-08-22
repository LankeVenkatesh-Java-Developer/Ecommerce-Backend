package com.ashok.it.adminservice.service;

import com.ashok.it.adminservice.entity.Product;
import com.ashok.it.adminservice.entity.ProductCategory;
import com.ashok.it.adminservice.repository.ProductCategoryRepository;
import com.ashok.it.adminservice.repository.ProductRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@Service
public class ReportService {
    
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public ReportService(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }
    
    public Mono<byte[]> generateProductsReport() {
        return productRepository.findAll()
                .collectList()
                .map(products -> {
                    try (Workbook workbook = new XSSFWorkbook();
                         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        
                        Sheet sheet = workbook.createSheet("Products Report");
                        
                        // Create header style
                        CellStyle headerStyle = createHeaderStyle(workbook);
                        
                        // Create headers
                        Row headerRow = sheet.createRow(0);
                        String[] headers = {"ID", "Name", "Description", "SKU", "Price", "Quantity", 
                                           "Category ID", "Image URL", "Active", "Created At", 
                                           "Updated At", "Created By", "Updated By"};
                        
                        for (int i = 0; i < headers.length; i++) {
                            Cell cell = headerRow.createCell(i);
                            cell.setCellValue(headers[i]);
                            cell.setCellStyle(headerStyle);
                        }
                        
                        // Fill data
                        int rowNum = 1;
                        for (Product product : products) {
                            Row row = sheet.createRow(rowNum++);
                            
                            row.createCell(0).setCellValue(product.getId() != null ? product.getId() : 0);
                            row.createCell(1).setCellValue(product.getName() != null ? product.getName() : "");
                            row.createCell(2).setCellValue(product.getDescription() != null ? product.getDescription() : "");
                            row.createCell(3).setCellValue(product.getSku() != null ? product.getSku() : "");
                            row.createCell(4).setCellValue(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);
                            row.createCell(5).setCellValue(product.getQuantity() != null ? product.getQuantity() : 0);
                            row.createCell(6).setCellValue(product.getCategoryId() != null ? product.getCategoryId() : 0);
                            row.createCell(7).setCellValue(product.getImageUrl() != null ? product.getImageUrl() : "");
                            row.createCell(8).setCellValue(product.getActive() != null ? product.getActive() : false);
                            row.createCell(9).setCellValue(formatDate(product.getCreatedAt()));
                            row.createCell(10).setCellValue(formatDate(product.getUpdatedAt()));
                            row.createCell(11).setCellValue(product.getCreatedBy() != null ? product.getCreatedBy() : "");
                            row.createCell(12).setCellValue(product.getUpdatedBy() != null ? product.getUpdatedBy() : "");
                        }
                        
                        // Auto-size columns
                        for (int i = 0; i < headers.length; i++) {
                            sheet.autoSizeColumn(i);
                        }
                        
                        workbook.write(outputStream);
                        return outputStream.toByteArray();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to generate products report", e);
                    }
                });
    }
    
    public Mono<byte[]> generateCategoriesReport() {
        return productCategoryRepository.findAll()
                .collectList()
                .map(categories -> {
                    try (Workbook workbook = new XSSFWorkbook();
                         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        
                        Sheet sheet = workbook.createSheet("Categories Report");
                        
                        // Create header style
                        CellStyle headerStyle = createHeaderStyle(workbook);
                        
                        // Create headers
                        Row headerRow = sheet.createRow(0);
                        String[] headers = {"ID", "Name", "Description", "Active", "Created At", 
                                           "Updated At", "Created By", "Updated By"};
                        
                        for (int i = 0; i < headers.length; i++) {
                            Cell cell = headerRow.createCell(i);
                            cell.setCellValue(headers[i]);
                            cell.setCellStyle(headerStyle);
                        }
                        
                        // Fill data
                        int rowNum = 1;
                        for (ProductCategory category : categories) {
                            Row row = sheet.createRow(rowNum++);
                            
                            row.createCell(0).setCellValue(category.getId() != null ? category.getId() : 0);
                            row.createCell(1).setCellValue(category.getName() != null ? category.getName() : "");
                            row.createCell(2).setCellValue(category.getDescription() != null ? category.getDescription() : "");
                            row.createCell(3).setCellValue(category.getActive() != null ? category.getActive() : false);
                            row.createCell(4).setCellValue(formatDate(category.getCreatedAt()));
                            row.createCell(5).setCellValue(formatDate(category.getUpdatedAt()));
                            row.createCell(6).setCellValue(category.getCreatedBy() != null ? category.getCreatedBy() : "");
                            row.createCell(7).setCellValue(category.getUpdatedBy() != null ? category.getUpdatedBy() : "");
                        }
                        
                        // Auto-size columns
                        for (int i = 0; i < headers.length; i++) {
                            sheet.autoSizeColumn(i);
                        }
                        
                        workbook.write(outputStream);
                        return outputStream.toByteArray();
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to generate categories report", e);
                    }
                });
    }
    
    public Mono<byte[]> generateCombinedReport() {
        return Mono.zip(
                productRepository.findAll().collectList(),
                productCategoryRepository.findAll().collectList()
        ).map(tuple -> {
            try (Workbook workbook = new XSSFWorkbook();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                
                // Products sheet
                createProductsSheet(workbook, tuple.getT1());
                
                // Categories sheet
                createCategoriesSheet(workbook, tuple.getT2());
                
                // Summary sheet
                createSummarySheet(workbook, tuple.getT1(), tuple.getT2());
                
                workbook.write(outputStream);
                return outputStream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("Failed to generate combined report", e);
            }
        });
    }
    
    private void createProductsSheet(Workbook workbook, java.util.List<Product> products) {
        Sheet sheet = workbook.createSheet("Products");
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Description", "SKU", "Price", "Quantity", 
                           "Category ID", "Image URL", "Active", "Created At", 
                           "Updated At", "Created By", "Updated By"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getId() != null ? product.getId() : 0);
            row.createCell(1).setCellValue(product.getName() != null ? product.getName() : "");
            row.createCell(2).setCellValue(product.getDescription() != null ? product.getDescription() : "");
            row.createCell(3).setCellValue(product.getSku() != null ? product.getSku() : "");
            row.createCell(4).setCellValue(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);
            row.createCell(5).setCellValue(product.getQuantity() != null ? product.getQuantity() : 0);
            row.createCell(6).setCellValue(product.getCategoryId() != null ? product.getCategoryId() : 0);
            row.createCell(7).setCellValue(product.getImageUrl() != null ? product.getImageUrl() : "");
            row.createCell(8).setCellValue(product.getActive() != null ? product.getActive() : false);
            row.createCell(9).setCellValue(formatDate(product.getCreatedAt()));
            row.createCell(10).setCellValue(formatDate(product.getUpdatedAt()));
            row.createCell(11).setCellValue(product.getCreatedBy() != null ? product.getCreatedBy() : "");
            row.createCell(12).setCellValue(product.getUpdatedBy() != null ? product.getUpdatedBy() : "");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createCategoriesSheet(Workbook workbook, java.util.List<ProductCategory> categories) {
        Sheet sheet = workbook.createSheet("Categories");
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Description", "Active", "Created At", 
                           "Updated At", "Created By", "Updated By"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        int rowNum = 1;
        for (ProductCategory category : categories) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(category.getId() != null ? category.getId() : 0);
            row.createCell(1).setCellValue(category.getName() != null ? category.getName() : "");
            row.createCell(2).setCellValue(category.getDescription() != null ? category.getDescription() : "");
            row.createCell(3).setCellValue(category.getActive() != null ? category.getActive() : false);
            row.createCell(4).setCellValue(formatDate(category.getCreatedAt()));
            row.createCell(5).setCellValue(formatDate(category.getUpdatedAt()));
            row.createCell(6).setCellValue(category.getCreatedBy() != null ? category.getCreatedBy() : "");
            row.createCell(7).setCellValue(category.getUpdatedBy() != null ? category.getUpdatedBy() : "");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createSummarySheet(Workbook workbook, java.util.List<Product> products, 
                                    java.util.List<ProductCategory> categories) {
        Sheet sheet = workbook.createSheet("Summary");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Report Summary");
        headerCell.setCellStyle(headerStyle);
        
        int rowNum = 2;
        
        // Products summary
        createSummaryRow(sheet, rowNum++, "Total Products", String.valueOf(products.size()), dataStyle);
        
        long activeProducts = products.stream().filter(p -> p.getActive() != null && p.getActive()).count();
        createSummaryRow(sheet, rowNum++, "Active Products", String.valueOf(activeProducts), dataStyle);
        
        BigDecimal totalValue = products.stream()
                .filter(p -> p.getPrice() != null && p.getQuantity() != null)
                .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        createSummaryRow(sheet, rowNum++, "Total Inventory Value", "$" + totalValue.toString(), dataStyle);
        
        int totalQuantity = products.stream()
                .filter(p -> p.getQuantity() != null)
                .mapToInt(Product::getQuantity)
                .sum();
        createSummaryRow(sheet, rowNum++, "Total Quantity", String.valueOf(totalQuantity), dataStyle);
        
        rowNum++;
        
        // Categories summary
        createSummaryRow(sheet, rowNum++, "Total Categories", String.valueOf(categories.size()), dataStyle);
        
        long activeCategories = categories.stream().filter(c -> c.getActive() != null && c.getActive()).count();
        createSummaryRow(sheet, rowNum++, "Active Categories", String.valueOf(activeCategories), dataStyle);
        
        rowNum++;
        
        // Report metadata
        createSummaryRow(sheet, rowNum++, "Report Generated At", formatDate(LocalDateTime.now()), dataStyle);
        
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
    
    private void createSummaryRow(Sheet sheet, int rowNum, String label, String value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(style);
        
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value);
        valueCell.setCellStyle(style);
    }
    
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_FORMATTER);
    }
}
