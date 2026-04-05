package com.uehboutique.controller;

import com.uehboutique.service.ServiceUsageService;
// Import Entity ServiceUsage của bạn vào (Sửa lại package nếu cần)
import com.uehboutique.entity.ServiceUsage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceUsageController.class)
public class ServiceUsageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceUsageService serviceUsageService;

    // ==========================================
    // 1. TEST THÊM DỊCH VỤ THÀNH CÔNG
    // ==========================================
    @Test
    void testAddService_Success() throws Exception {
        // Rút kinh nghiệm vụ gạch đỏ lúc nãy, ở đây mình khởi tạo thẳng Object ServiceUsage
        // Nếu Entity của bạn tên khác thì đổi lại cho đúng nhé
        ServiceUsage mockUsage = new ServiceUsage();

        // Giả lập Service chạy thành công và trả về Object
        when(serviceUsageService.addServiceToBooking(anyInt(), anyInt(), anyInt()))
                .thenReturn(mockUsage);

        mockMvc.perform(post("/api/service-usages")
                        .param("bookingId", "1")
                        .param("serviceId", "2")
                        .param("quantity", "2"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // 2. TEST THÊM DỊCH VỤ THẤT BẠI (BẮT CATCH)
    // ==========================================
    @Test
    void testAddService_Failure() throws Exception {
        // Giả lập lỗi khi số lượng < 1 hoặc không tìm thấy dịch vụ
        when(serviceUsageService.addServiceToBooking(anyInt(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Số lượng dịch vụ phải lớn hơn 0"));

        mockMvc.perform(post("/api/service-usages")
                        .param("bookingId", "1")
                        .param("serviceId", "2")
                        .param("quantity", "-5"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Số lượng dịch vụ phải lớn hơn 0"));
    }
}