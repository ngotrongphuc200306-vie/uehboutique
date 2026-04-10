package com.uehboutique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uehboutique.dto.request.LoginRequest;
import com.uehboutique.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Khởi tạo đối tượng bằng Java tiêu chuẩn (không dùng Lombok Builder)
        loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("POST /api/auth/login - Đăng nhập thành công (200 OK)")
    void testLogin_Success() throws Exception {
        // ĐÃ FIX: Dùng doReturn(null) để bỏ qua kiểm tra kiểu dữ liệu (tương thích mọi loại return type)
        Mockito.doReturn(null).when(authService).login("admin", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/login - Đăng nhập thất bại do sai thông tin (400 Bad Request)")
    void testLogin_BadRequest() throws Exception {
        // ĐÃ FIX: Dùng doThrow để đồng bộ cú pháp và đảm bảo không lỗi kiểu
        Mockito.doThrow(new RuntimeException("Sai thông tin đăng nhập"))
                .when(authService).login("admin", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Sai thông tin đăng nhập")); // Kì vọng trả về đúng message lỗi
    }

    @Test
    @DisplayName("POST /api/auth/logout - Đăng xuất thành công (200 OK)")
    void testLogout_Success() throws Exception {
        // ĐÃ FIX: Dùng doReturn(null) để bỏ qua kiểm tra kiểu dữ liệu
        Mockito.doReturn(null).when(authService).logout();

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());
    }
}