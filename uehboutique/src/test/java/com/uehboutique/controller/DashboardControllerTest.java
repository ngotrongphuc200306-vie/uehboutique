package com.uehboutique.controller;

import com.uehboutique.service.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    @Test
    @DisplayName("GET /api/dashboard - Lấy thống kê thành công (200 OK)")
    void testGetDashboardStats_Success() throws Exception {
        // Dùng doReturn(null) để giả lập Service chạy thành công bất chấp kiểu dữ liệu trả về là gì
        Mockito.doReturn(null).when(dashboardService).getDashboardStats();

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/dashboard - Lỗi khi lấy thống kê (400 Bad Request)")
    void testGetDashboardStats_Exception() throws Exception {
        // Giả lập Service ném ra Exception
        Mockito.doThrow(new RuntimeException("Lỗi truy xuất dữ liệu"))
                .when(dashboardService).getDashboardStats();

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isBadRequest());
    }
}