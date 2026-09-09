package com.venkatesh.it.adminmanagementservice.service;

import java.io.IOException;

public interface ReportService {

    byte[] generateProductExcelReport() throws IOException;

    byte[] generateCategoryExcelReport() throws IOException;

    byte[] generateStockExcelReport() throws IOException;
}
