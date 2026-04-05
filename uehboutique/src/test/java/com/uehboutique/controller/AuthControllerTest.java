package com.uehboutique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uehboutique.dto.request.LoginRequest;
import com.uehboutique.entity.Staff;
import com.uehboutique.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private Staff mockStaff;

    @BeforeEach
    void setUp() {
        // Tạo dữ liệu giả lập cho Staff
        mockStaff = new Staff();
        mockStaff.setUsername("admin");
        mockStaff.setPassword("123456");
        // Thêm các field khác của Staff nếu cần (ví dụ: mockStaff.setRole("ADMIN");)
    }

    // ==========================================
    // 1. TEST ĐĂNG NHẬP THÀNH CÔNG
    // ==========================================
    @Test
    void testLogin_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "123456");

        // Giả lập AuthService trả về đối tượng Staff khi đăng nhập đúng
        when(authService.login("admin", "123456")).thenReturn(mockStaff);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"));
    }

    // ==========================================
    // 2. TEST ĐĂNG NHẬP THẤT BẠI
    // ==========================================
    @Test
    void testLogin_Failure() throws Exception {
        LoginRequest loginRequest = new LoginRequest("wrong", "pass");

        // Giả lập AuthService ném ra lỗi khi sai thông tin
        when(authService.login(anyString(), anyString()))
                .thenThrow(new RuntimeException("Wrong username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Wrong username or password"));
    }

    // ==========================================
    // 3. TEST ĐĂNG XUẤT
    // ==========================================
    @Test
    void testLogout() throws Exception {
        when(authService.logout()).thenReturn("Logout successfully");

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logout successfully"));
    }
}