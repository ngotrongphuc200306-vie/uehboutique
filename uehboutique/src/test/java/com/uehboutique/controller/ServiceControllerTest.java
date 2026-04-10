package com.uehboutique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uehboutique.entity.Service; // Import đúng Entity Service của project
import com.uehboutique.service.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceController.class)
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceService serviceService;

    private ObjectMapper objectMapper;
    private Service sampleService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Khởi tạo đối tượng giả bằng Java tiêu chuẩn (Không Lombok)
        sampleService = new Service();
        // Nhớ: Chạy Alt + Insert trong file Service.java để tạo Getter/Setter nếu cần set ID nhé!
    }

    // =========================================================================
    // TEST API: GET /api/services
    // =========================================================================
    @Test
    @DisplayName("GET /api/services - Lấy danh sách dịch vụ thành công (200 OK)")
    void testGetAllServices_Success() throws Exception {
        Mockito.doReturn(Collections.emptyList()).when(serviceService).getAllServices();

        mockMvc.perform(get("/api/services")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // TEST API: POST /api/services
    // =========================================================================
    @Test
    @DisplayName("POST /api/services - Thêm dịch vụ mới thành công (200 OK)")
    void testAddService_Success() throws Exception {
        Mockito.doReturn(sampleService).when(serviceService).addService(any(Service.class));

        mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleService)))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // TEST API: PUT /api/services/{id}
    // =========================================================================
    @Test
    @DisplayName("PUT /api/services/{id} - Cập nhật dịch vụ thành công (200 OK)")
    void testUpdateService_Success() throws Exception {
        Mockito.doReturn(sampleService).when(serviceService).updateService(eq(1), any(Service.class));

        mockMvc.perform(put("/api/services/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleService)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/services/{id} - Lỗi cập nhật (400 Bad Request)")
    void testUpdateService_BadRequest() throws Exception {
        Mockito.doThrow(new RuntimeException("Dịch vụ không tồn tại"))
                .when(serviceService).updateService(eq(99), any(Service.class));

        mockMvc.perform(put("/api/services/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleService)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // TEST API: DELETE /api/services/{id}
    // =========================================================================
    @Test
    @DisplayName("DELETE /api/services/{id} - Xóa dịch vụ thành công (200 OK)")
    void testDeleteService_Success() throws Exception {
        // Vì hàm deleteService thường trả về void, ta dùng doNothing()
        Mockito.doNothing().when(serviceService).deleteService(eq(1));

        mockMvc.perform(delete("/api/services/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}