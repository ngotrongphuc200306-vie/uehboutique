package com.uehboutique.controller;

import com.uehboutique.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    // ==========================================
    // 1. TEST LẤY THỐNG KÊ THÀNH CÔNG
    // ==========================================
    @Test
    void testGetDashboardStats_Success() throws Exception {
        // Giả lập dữ liệu thống kê trả về từ Service (dùng Map cho tiện lợi)
        // Nếu Service của bạn trả về một Object (ví dụ DashboardDTO), bạn có thể thay thế Map bằng Object đó.
        Map<String, Object> mockStats = new HashMap<>();
        mockStats.put("totalBookings", 150);
        mockStats.put("totalRevenue", 5000000);

        when(dashboardService.getDashboardStats()).thenReturn(mockStats);

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalBookings").value(150))
                .andExpect(jsonPath("$.totalRevenue").value(5000000));
    }

    // ==========================================
    // 2. TEST LẤY THỐNG KÊ THẤT BẠI (BẮT CATCH)
    // ==========================================
    @Test
    void testGetDashboardStats_Failure() throws Exception {
        // Giả lập Service ném ra lỗi (VD: Mất kết nối database)
        when(dashboardService.getDashboardStats())
                .thenThrow(new RuntimeException("Database connection lost"));

        // Controller phải bắt được lỗi và trả về mã 400 Bad Request kèm theo câu thông báo
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error retrieving statistics data: Database connection lost"));
    }
}