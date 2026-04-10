package com.uehboutique.controller;

import com.uehboutique.service.ServiceUsageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceUsageController.class)
class ServiceUsageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceUsageService serviceUsageService;

    // =========================================================================
    // TEST API: POST /api/service-usages
    // =========================================================================
    @Test
    @DisplayName("POST /api/service-usages - Thêm dịch vụ cho booking thành công (200 OK)")
    void testAddService_Success() throws Exception {
        // Dùng doReturn(null) để bao phủ mọi kiểu dữ liệu trả về từ service
        Mockito.doReturn(null).when(serviceUsageService)
                .addServiceToBooking(eq(1), eq(2), eq(2));

        mockMvc.perform(post("/api/service-usages")
                        .param("bookingId", "1")
                        .param("serviceId", "2")
                        .param("quantity", "2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/service-usages - Lỗi thêm dịch vụ (400 Bad Request)")
    void testAddService_BadRequest() throws Exception {
        // Giả lập văng lỗi khi thêm dịch vụ (ví dụ: Booking không tồn tại)
        Mockito.doThrow(new RuntimeException("Booking không tồn tại"))
                .when(serviceUsageService).addServiceToBooking(anyInt(), anyInt(), anyInt());

        mockMvc.perform(post("/api/service-usages")
                        .param("bookingId", "99")
                        .param("serviceId", "99")
                        .param("quantity", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}