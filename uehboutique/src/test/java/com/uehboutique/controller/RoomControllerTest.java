package com.uehboutique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uehboutique.entity.Room;
import com.uehboutique.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    private Room mockRoom;

    @BeforeEach
    void setUp() {
        mockRoom = new Room();
        // LƯU Ý: Nếu Entity Room của bạn dùng 'id' thì đổi thành setId nhé
        mockRoom.setRoomId(1);
        mockRoom.setRoomNumber("101");
        mockRoom.setStatus("Available");
    }

    // ==========================================
    // 1. TEST LẤY TẤT CẢ PHÒNG (GET /)
    // ==========================================
    @Test
    void testGetAllRooms_Success() throws Exception {
        List<Room> roomList = List.of(mockRoom);
        when(roomService.getAllRooms()).thenReturn(roomList);

        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].roomNumber").value("101"));
    }

    // ==========================================
    // 2. TEST LẤY PHÒNG THEO TRẠNG THÁI (GET /status)
    // ==========================================
    @Test
    void testGetAllRoomStatus_Success() throws Exception {
        List<Room> roomList = List.of(mockRoom);
        when(roomService.getRoomsByStatus(anyString())).thenReturn(roomList);

        mockMvc.perform(get("/api/rooms/status")
                        .param("status", "Available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].status").value("Available"));
    }

    // ==========================================
    // 3. TEST DỌN PHÒNG (PUT /{roomId}/clean)
    // ==========================================
    @Test
    void testCleanRoom_Success() throws Exception {
        when(roomService.cleanRoom(anyInt())).thenReturn(mockRoom);

        mockMvc.perform(put("/api/rooms/1/clean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId").value(1)); // Đổi thành id nếu Entity dùng id
    }

    @Test
    void testCleanRoom_Failure() throws Exception {
        when(roomService.cleanRoom(anyInt()))
                .thenThrow(new RuntimeException("Phòng đang có khách, không thể dọn"));

        mockMvc.perform(put("/api/rooms/1/clean"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Mistakes made while cleaning the room: Phòng đang có khách, không thể dọn"));
    }

    // ==========================================
    // 4. TEST THÊM PHÒNG (POST /)
    // ==========================================
    @Test
    void testAddRoom_Success() throws Exception {
        when(roomService.addRoom(any(Room.class))).thenReturn(mockRoom);

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRoom)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomNumber").value("101"));
    }

    @Test
    void testAddRoom_Failure() throws Exception {
        when(roomService.addRoom(any(Room.class)))
                .thenThrow(new RuntimeException("Trùng số phòng"));

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRoom)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error add room: Trùng số phòng"));
    }
}