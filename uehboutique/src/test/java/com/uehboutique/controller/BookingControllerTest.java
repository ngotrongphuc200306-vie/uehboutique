package com.uehboutique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.uehboutique.dto.request.CheckInRequest;
import com.uehboutique.entity.Booking;
import com.uehboutique.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper objectMapper;

    private CheckInRequest checkInRequest;
    private Booking mockBooking;

    @BeforeEach
    void setUp() {
        // Cấu hình ObjectMapper hỗ trợ parse LocalDate (JDK 8+ / JDK 25)
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Khởi tạo request bằng Java chuẩn (không Lombok)
        checkInRequest = new CheckInRequest();
        checkInRequest.setGuestId(1);
        checkInRequest.setRoomId(101);
        checkInRequest.setStaffId(2);
        checkInRequest.setCheckOutDate(LocalDate.of(2026, 3, 28));

        // Mock đối tượng Booking trả về cơ bản
        mockBooking = new Booking();
    }

    // =========================================================================
    // TEST API: POST /api/bookings/checkin
    // =========================================================================
    @Test
    @DisplayName("POST /api/bookings/checkin - Thành công (200 OK)")
    void testCheckIn_Success() throws Exception {
        Mockito.doReturn(mockBooking).when(bookingService)
                .processCheckIn(any(Integer.class), any(Integer.class), any(Integer.class), any(LocalDate.class));

        mockMvc.perform(post("/api/bookings/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/bookings/checkin - Lỗi (400 Bad Request)")
    void testCheckIn_BadRequest() throws Exception {
        Mockito.doThrow(new RuntimeException("Phòng không trống")).when(bookingService)
                .processCheckIn(any(Integer.class), any(Integer.class), any(Integer.class), any(LocalDate.class));

        mockMvc.perform(post("/api/bookings/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInRequest)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    // TEST API: GET /api/bookings
    // =========================================================================
    @Test
    @DisplayName("GET /api/bookings - Lấy danh sách thành công (200 OK)")
    void testGetAllBookings_Success() throws Exception {
        // Trả về list rỗng để bỏ qua logic sort (tránh NullPointerException trong Controller khi DB rỗng)
        Mockito.doReturn(Collections.emptyList()).when(bookingService).getAllBookings();

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/bookings - Lỗi Backend (500 Internal Server Error)")
    void testGetAllBookings_Exception() throws Exception {
        Mockito.doThrow(new RuntimeException("Lỗi kết nối DB")).when(bookingService).getAllBookings();

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isInternalServerError());
    }

    // =========================================================================
    // TEST API: PUT /api/bookings/{bookingId}/transfer
    // =========================================================================
    @Test
    @DisplayName("PUT /api/bookings/{bookingId}/transfer - Thành công (200 OK)")
    void testTransferRoom_Success() throws Exception {
        Mockito.doReturn(mockBooking).when(bookingService).transferRoom(eq(1), eq(202));

        mockMvc.perform(put("/api/bookings/{bookingId}/transfer", 1)
                        .param("newRoomId", "202"))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // TEST API: POST /api/bookings/reserve
    // =========================================================================
    @Test
    @DisplayName("POST /api/bookings/reserve - Thành công (200 OK)")
    void testReserveRoom_Success() throws Exception {
        Mockito.doReturn(mockBooking).when(bookingService)
                .reserveRoom(any(Integer.class), any(Integer.class), any(Integer.class), any(LocalDate.class), any(LocalDate.class));

        mockMvc.perform(post("/api/bookings/reserve")
                        .param("guestId", "8521")
                        .param("roomId", "3")
                        .param("staffId", "1")
                        .param("checkInDate", "2026-03-25")
                        .param("checkOutDate", "2026-03-28"))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // TEST API: PUT /api/bookings/{bookingId}/checkin-reserved
    // =========================================================================
    @Test
    @DisplayName("PUT /api/bookings/{bookingId}/checkin-reserved - Thành công (200 OK)")
    void testCheckInReservedRoom_Success() throws Exception {
        Mockito.doReturn(mockBooking).when(bookingService).checkInReservedRoom(eq(2));

        mockMvc.perform(put("/api/bookings/{bookingId}/checkin-reserved", 2))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // TEST API: PUT /api/bookings/{bookingId}/cancel
    // =========================================================================
    @Test
    @DisplayName("PUT /api/bookings/{bookingId}/cancel - Thành công (200 OK)")
    void testCancelBooking_Success() throws Exception {
        Mockito.doReturn(mockBooking).when(bookingService).cancelBooking(eq(2));

        mockMvc.perform(put("/api/bookings/{bookingId}/cancel", 2))
                .andExpect(status().isOk());
    }
}