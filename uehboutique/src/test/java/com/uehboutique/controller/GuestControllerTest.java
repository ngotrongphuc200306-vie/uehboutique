package com.uehboutique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uehboutique.entity.Guest;
import com.uehboutique.service.GuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GuestController.class)
class GuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuestService guestService;

    private ObjectMapper objectMapper;

    private Guest sampleGuest;

    @BeforeEach
    void setUp() {
        // Khởi tạo ObjectMapper chuẩn
        objectMapper = new ObjectMapper();

        // Khởi tạo Object bằng Java tiêu chuẩn (Không Lombok)
        sampleGuest = new Guest();
        sampleGuest.setGuestId(1);
        sampleGuest.setGuestName("Nguyen Van A");
        sampleGuest.setPhone("0901234567");
    }

    // =========================================================================
    // TEST API: POST /api/guests
    // =========================================================================
    @Test
    @DisplayName("POST /api/guests - Tạo khách hàng thành công (200 OK)")
    void testCreateGuest_Success() throws Exception {
        Mockito.doReturn(sampleGuest).when(guestService).saveGuest(any(Guest.class));

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleGuest)))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // TEST API: GET /api/guests
    // =========================================================================
    @Test
    @DisplayName("GET /api/guests - Lấy danh sách khách (200 OK)")
    void testGetAllGuests_Success() throws Exception {
        // Trả về Page.empty() để an toàn, tránh lỗi NullPointerException ở tầng Controller/View
        Mockito.doReturn(Page.empty()).when(guestService).getAllGuests(anyInt(), anyInt());

        mockMvc.perform(get("/api/guests")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // TEST API: GET /api/guests/{id}
    // =========================================================================
    @Test
    @DisplayName("GET /api/guests/{id} - Tìm thấy khách (200 OK)")
    void testGetGuestById_Found() throws Exception {
        Mockito.doReturn(Optional.of(sampleGuest)).when(guestService).getGuestById(eq(1));

        mockMvc.perform(get("/api/guests/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/guests/{id} - Không tìm thấy khách (404 Not Found)")
    void testGetGuestById_NotFound() throws Exception {
        Mockito.doReturn(Optional.empty()).when(guestService).getGuestById(eq(99));

        mockMvc.perform(get("/api/guests/{id}", 99))
                .andExpect(status().isNotFound());
    }

    // =========================================================================
    // TEST API: GET /api/guests/search
    // =========================================================================
    @Test
    @DisplayName("GET /api/guests/search - Tìm thấy qua SĐT (200 OK)")
    void testGetGuestByPhone_Found() throws Exception {
        Mockito.doReturn(Optional.of(sampleGuest)).when(guestService).getGuestByPhone(anyString());

        mockMvc.perform(get("/api/guests/search")
                        .param("phone", "0901234567"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/guests/search - Không tìm thấy qua SĐT (404 Not Found)")
    void testGetGuestByPhone_NotFound() throws Exception {
        Mockito.doReturn(Optional.empty()).when(guestService).getGuestByPhone(anyString());

        mockMvc.perform(get("/api/guests/search")
                        .param("phone", "0000000000"))
                .andExpect(status().isNotFound());
    }

    // =========================================================================
    // TEST API: PUT /api/guests/{id}
    // =========================================================================
    @Test
    @DisplayName("PUT /api/guests/{id} - Cập nhật thành công (200 OK)")
    void testUpdateGuest_Success() throws Exception {
        Mockito.doReturn(sampleGuest).when(guestService).updateGuest(eq(1), any(Guest.class));

        mockMvc.perform(put("/api/guests/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleGuest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/guests/{id} - Cập nhật thất bại, khách không tồn tại (404 Not Found)")
    void testUpdateGuest_NotFound() throws Exception {
        // Giả lập văng lỗi khi không tìm thấy khách để update
        Mockito.doThrow(new RuntimeException("Guest not found"))
                .when(guestService).updateGuest(eq(99), any(Guest.class));

        mockMvc.perform(put("/api/guests/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleGuest)))
                .andExpect(status().isNotFound());
    }
}