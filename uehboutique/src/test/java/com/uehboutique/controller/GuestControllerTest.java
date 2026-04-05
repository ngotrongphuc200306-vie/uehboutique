package com.uehboutique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uehboutique.entity.Guest;
import com.uehboutique.service.GuestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GuestController.class)
public class GuestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuestService guestService;

    @Autowired
    private ObjectMapper objectMapper;

    private Guest mockGuest;

    @BeforeEach
    void setUp() {
        mockGuest = new Guest();
        // SỬA LẠI TÊN HÀM Ở ĐÂY CHO KHỚP VỚI ENTITY GUEST
        mockGuest.setGuestId(1);
        mockGuest.setGuestName("Nguyen Van A");
        mockGuest.setIdCard("012345678912"); // Thêm idCard vì entity yêu cầu nullable = false
        mockGuest.setPhone("0123456789");
    }

    // ==========================================
    // 1. TEST TẠO MỚI KHÁCH HÀNG (POST)
    // ==========================================
    @Test
    void testCreateGuest() throws Exception {
        when(guestService.saveGuest(any(Guest.class))).thenReturn(mockGuest);

        mockMvc.perform(post("/api/guests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockGuest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guestId").value(1)) // Đổi thành $.guestId
                .andExpect(jsonPath("$.guestName").value("Nguyen Van A")); // Đổi thành $.guestName
    }

    // ==========================================
    // 2. TEST LẤY DANH SÁCH (GET PAGE)
    // ==========================================
    @Test
    void testGetAllGuests() throws Exception {
        Page<Guest> guestPage = new PageImpl<>(List.of(mockGuest));

        when(guestService.getAllGuests(anyInt(), anyInt())).thenReturn(guestPage);

        mockMvc.perform(get("/api/guests")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].guestName").value("Nguyen Van A")); // Đổi thành guestName
    }

    // ==========================================
    // 3. TEST LẤY THEO ID (GET /id)
    // ==========================================
    @Test
    void testGetGuestById_Found() throws Exception {
        when(guestService.getGuestById(1)).thenReturn(Optional.of(mockGuest));

        mockMvc.perform(get("/api/guests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guestName").value("Nguyen Van A")); // Đổi thành guestName
    }

    @Test
    void testGetGuestById_NotFound() throws Exception {
        when(guestService.getGuestById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/guests/99"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // 4. TEST TÌM THEO SỐ ĐIỆN THOẠI (GET /search)
    // ==========================================
    @Test
    void testGetGuestByPhone_Found() throws Exception {
        when(guestService.getGuestByPhone("0123456789")).thenReturn(Optional.of(mockGuest));

        mockMvc.perform(get("/api/guests/search")
                        .param("phone", "0123456789"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value("0123456789"));
    }

    @Test
    void testGetGuestByPhone_NotFound() throws Exception {
        when(guestService.getGuestByPhone("0000000000")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/guests/search")
                        .param("phone", "0000000000"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // 5. TEST CẬP NHẬT (PUT /id)
    // ==========================================
    @Test
    void testUpdateGuest_Success() throws Exception {
        when(guestService.updateGuest(eq(1), any(Guest.class))).thenReturn(mockGuest);

        mockMvc.perform(put("/api/guests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockGuest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.guestName").value("Nguyen Van A")); // Đổi thành guestName
    }

    @Test
    void testUpdateGuest_NotFound() throws Exception {
        when(guestService.updateGuest(eq(99), any(Guest.class))).thenThrow(new RuntimeException("Not found"));

        mockMvc.perform(put("/api/guests/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockGuest)))
                .andExpect(status().isNotFound());
    }
}