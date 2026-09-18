package com.venkatesh.it.adminmanagementservice.controller;

import com.venkatesh.it.adminmanagementservice.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    private byte[] testReportBytes;

    @BeforeEach
    void setUp() {
        testReportBytes = new byte[]{1, 2, 3, 4, 5};
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGenerateProductReport_thenReturnExcelFile() throws Exception {
        when(reportService.generateProductExcelReport()).thenReturn(testReportBytes);

        mockMvc.perform(get("/reports/products/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().bytes(testReportBytes));
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void whenGenerateProductReportWithSuperAdmin_thenReturnExcelFile() throws Exception {
        when(reportService.generateProductExcelReport()).thenReturn(testReportBytes);

        mockMvc.perform(get("/reports/products/excel"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(testReportBytes));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGenerateCategoryReport_thenReturnExcelFile() throws Exception {
        when(reportService.generateCategoryExcelReport()).thenReturn(testReportBytes);

        mockMvc.perform(get("/reports/categories/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().bytes(testReportBytes));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenGenerateStockReport_thenReturnExcelFile() throws Exception {
        when(reportService.generateStockExcelReport()).thenReturn(testReportBytes);

        mockMvc.perform(get("/reports/stock/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().bytes(testReportBytes));
    }
}
