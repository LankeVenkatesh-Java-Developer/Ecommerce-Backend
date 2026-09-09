package com.venkatesh.it.adminmanagementservice.util;

import com.venkatesh.it.adminmanagementservice.dto.report.CategoryReportDTO;
import com.venkatesh.it.adminmanagementservice.dto.report.ProductReportDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelReportUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static byte[] generateProductReport(List<ProductReportDTO> products) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Products");

            createHeaderRow(sheet, new String[]{
                    "ID", "SKU", "Product Name", "Category", "Brand",
                    "Price", "Stock Quantity", "Status", "Created Date", "Updated Date"
            });

            fillProductData(sheet, products);

            autoSizeColumns(sheet, 10);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public static byte[] generateCategoryReport(List<CategoryReportDTO> categories) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Categories");

            createHeaderRow(sheet, new String[]{
                    "ID", "Category Name", "Description", "Status", "Created Date", "Updated Date"
            });

            fillCategoryData(sheet, categories);

            autoSizeColumns(sheet, 6);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    public static byte[] generateStockReport(List<ProductReportDTO> products) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Stock Report");

            createHeaderRow(sheet, new String[]{
                    "Product ID", "SKU", "Product Name", "Category", "Current Stock", "Status"
            });

            fillStockData(sheet, products);

            autoSizeColumns(sheet, 6);

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private static void createHeaderRow(Sheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(sheet.getWorkbook());

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private static void fillProductData(Sheet sheet, List<ProductReportDTO> products) {
        CellStyle dataStyle = createDataStyle(sheet.getWorkbook());

        int rowNum = 1;
        for (ProductReportDTO product : products) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getSku() != null ? product.getSku() : "");
            row.createCell(2).setCellValue(product.getName());
            row.createCell(3).setCellValue(product.getCategoryName() != null ? product.getCategoryName() : "");
            row.createCell(4).setCellValue(product.getBrand() != null ? product.getBrand() : "");
            row.createCell(5).setCellValue(product.getPrice() != null ? product.getPrice().doubleValue() : 0.0);
            row.createCell(6).setCellValue(product.getQuantity() != null ? product.getQuantity() : 0);
            row.createCell(7).setCellValue(product.getStatus() != null ? product.getStatus() : "");
            row.createCell(8).setCellValue(formatDate(product.getCreatedAt()));
            row.createCell(9).setCellValue(formatDate(product.getUpdatedAt()));

            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private static void fillCategoryData(Sheet sheet, List<CategoryReportDTO> categories) {
        CellStyle dataStyle = createDataStyle(sheet.getWorkbook());

        int rowNum = 1;
        for (CategoryReportDTO category : categories) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(category.getId());
            row.createCell(1).setCellValue(category.getName());
            row.createCell(2).setCellValue(category.getDescription() != null ? category.getDescription() : "");
            row.createCell(3).setCellValue(category.getStatus() != null ? category.getStatus() : "");
            row.createCell(4).setCellValue(formatDate(category.getCreatedAt()));
            row.createCell(5).setCellValue(formatDate(category.getUpdatedAt()));

            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private static void fillStockData(Sheet sheet, List<ProductReportDTO> products) {
        CellStyle dataStyle = createDataStyle(sheet.getWorkbook());

        int rowNum = 1;
        for (ProductReportDTO product : products) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getSku() != null ? product.getSku() : "");
            row.createCell(2).setCellValue(product.getName());
            row.createCell(3).setCellValue(product.getCategoryName() != null ? product.getCategoryName() : "");
            row.createCell(4).setCellValue(product.getQuantity() != null ? product.getQuantity() : 0);
            row.createCell(5).setCellValue(product.getStatus() != null ? product.getStatus() : "");

            for (Cell cell : row) {
                cell.setCellStyle(dataStyle);
            }
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static String formatDate(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "";
    }
}
