package com.uehboutique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uehboutique.entity.Room;
import com.uehboutique.service.RoomService;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    private ObjectMapper objectMapper;
    private Room sampleRoom;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        // Khởi tạo đối tượng giả bằng Java tiêu chuẩn (Không Lombok)
        sampleRoom = new Room();
        // Lưu ý: Nếu muốn set giá trị cụ thể, hãy nhớ Generate Getter/Setter cho file Room.java nhé!
        // Ví dụ: sampleRoom.setRoomId(1);
    }

    // =========================================================================
    // TEST API: GET /api/rooms
    // =========================================================================
    @Test
    @DisplayName("GET /api/rooms - Lấy tất cả phòng thành công (200 OK)")
    void testGetAllRooms_Success() throws Exception {
        // Trả về list rỗng để tránh NullPointerException khi test
        Mockito.doReturn(Collections.emptyList()).when(roomService).getAllRooms();

        mockMvc.perform(get("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // TEST API: GET /api/rooms/status
    // =========================================================================
    @Test
    @DisplayName("GET /api/rooms/status - Lấy phòng theo trạng thái thành công (200 OK)")
    void testGetAllRoomStatus_Success() throws Exception {
        Mockito.doReturn(Collections.emptyList()).when(roomService).getRoomsByStatus(anyString());

        mockMvc.perform(get("/api/rooms/status")
                        .param("status", "AVAILABLE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // TEST API: PUT /api/rooms/{roomId}/clean
    // =========================================================================
    @Test
    @DisplayName("PUT /api/rooms/{roomId}/clean - Dọn phòng thành công (200 OK)")
    void testCleanRoom_Success() throws Exception {
        Mockito.doReturn(sampleRoom).when(roomService).cleanRoom(eq(1));

        mockMvc.perform(put("/api/rooms/{roomId}/clean", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/rooms/{roomId}/clean - Lỗi khi dọn phòng (400 Bad Request)")
    void testCleanRoom_BadRequest() throws Exception {
        Mockito.doThrow(new RuntimeException("Phòng đang có khách, không thể dọn!"))
                .when(roomService).cleanRoom(anyInt());

        mockMvc.perform(put("/api/rooms/{roomId}/clean", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // TEST API: POST /api/rooms
    // =========================================================================
    @Test
    @DisplayName("POST /api/rooms - Thêm phòng mới thành công (200 OK)")
    void testAddRoom_Success() throws Exception {
        Mockito.doReturn(sampleRoom).when(roomService).addRoom(any(Room.class));

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRoom)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/rooms - Lỗi khi thêm phòng (400 Bad Request)")
    void testAddRoom_BadRequest() throws Exception {
        Mockito.doThrow(new RuntimeException("Trùng số phòng"))
                .when(roomService).addRoom(any(Room.class));

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRoom)))
                .andExpect(status().isBadRequest());
    }
}