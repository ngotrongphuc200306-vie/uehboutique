package com.uehboutique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uehboutique.service.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceController.class)
public class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceService serviceService;

    @Autowired
    private ObjectMapper objectMapper;

    // Sử dụng đường dẫn đầy đủ để tránh trùng với @Service của Spring
    private com.uehboutique.entity.Service mockService;

    @BeforeEach
    void setUp() {
        mockService = new com.uehboutique.entity.Service();
        // LƯU Ý: Chỉnh sửa lại các hàm set này cho đúng với các field trong Entity Service của bạn
        // Mình đoán bạn sẽ có serviceId và serviceName
        mockService.setServiceId(1);
        mockService.setServiceName("Spa & Massage");
    }

    // ==========================================
    // 1. TEST LẤY DANH SÁCH DỊCH VỤ (GET /)
    // ==========================================
    @Test
    void testGetAllServices_Success() throws Exception {
        when(serviceService.getAllServices()).thenReturn(List.of(mockService));

        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].serviceName").value("Spa & Massage"));
    }

    // ==========================================
    // 2. TEST THÊM DỊCH VỤ (POST /)
    // ==========================================
    @Test
    void testAddService_Success() throws Exception {
        when(serviceService.addService(any(com.uehboutique.entity.Service.class))).thenReturn(mockService);

        mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockService)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("Spa & Massage"));
    }

    // ==========================================
    // 3. TEST CẬP NHẬT DỊCH VỤ (PUT /{id})
    // ==========================================
    @Test
    void testUpdateService_Success() throws Exception {
        when(serviceService.updateService(eq(1), any(com.uehboutique.entity.Service.class))).thenReturn(mockService);

        mockMvc.perform(put("/api/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockService)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("Spa & Massage"));
    }

    @Test
    void testUpdateService_Failure() throws Exception {
        // Test trường hợp try-catch khi cập nhật lỗi (VD: không tìm thấy dịch vụ)
        when(serviceService.updateService(eq(1), any(com.uehboutique.entity.Service.class)))
                .thenThrow(new RuntimeException("Không tìm thấy dịch vụ"));

        mockMvc.perform(put("/api/services/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockService)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Không tìm thấy dịch vụ"));
    }

    // ==========================================
    // 4. TEST XÓA DỊCH VỤ (DELETE /{id})
    // ==========================================
    @Test
    void testDeleteService_Success() throws Exception {
        // Vì hàm deleteService thường trả về void, ta dùng doNothing()
        doNothing().when(serviceService).deleteService(1);

        mockMvc.perform(delete("/api/services/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Service deleted successfully"));
    }
}