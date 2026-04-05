package com.uehboutique.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.uehboutique.dto.request.CheckInRequest;
import com.uehboutique.entity.Booking;
import com.uehboutique.entity.Room;
import com.uehboutique.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private Booking mockBooking;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());

        Room mockRoom = new Room();
        mockRoom.setRoomNumber("101");

        mockBooking = new Booking();
        // Đã sửa thành setBookingId thay vì setId
        mockBooking.setBookingId(1);
        mockBooking.setRoom(mockRoom);
    }

    // ==========================================
    // 1. TEST XỬ LÝ CHECK-IN (POST /checkin)
    // ==========================================
    @Test
    void testCheckin_Success() throws Exception {
        CheckInRequest request = new CheckInRequest();
        request.setGuestId(1);
        request.setRoomId(101);
        request.setStaffId(1);
        request.setCheckOutDate(LocalDate.now().plusDays(2));

        when(bookingService.processCheckIn(anyInt(), anyInt(), anyInt(), any(LocalDate.class)))
                .thenReturn(mockBooking);

        mockMvc.perform(post("/api/bookings/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1)); // Đã sửa thành bookingId
    }

    @Test
    void testCheckin_Failure() throws Exception {
        CheckInRequest request = new CheckInRequest();

        when(bookingService.processCheckIn(anyInt(), anyInt(), anyInt(), any()))
                .thenThrow(new RuntimeException("Phòng không trống"));

        mockMvc.perform(post("/api/bookings/checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Phòng không trống"));
    }

    // ==========================================
    // 2. TEST LẤY DANH SÁCH & SORT (GET /)
    // ==========================================
    @Test
    void testGetAllBookings_Success() throws Exception {
        Room room2 = new Room();
        room2.setRoomNumber("105");
        Booking booking2 = new Booking();
        // Đã sửa thành setBookingId thay vì setId
        booking2.setBookingId(2);
        booking2.setRoom(room2);

        List<Booking> mockList = Arrays.asList(booking2, mockBooking);
        when(bookingService.getAllBookings()).thenReturn(mockList);

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                // Đã sửa thành bookingId
                .andExpect(jsonPath("$.[0].bookingId").value(1))
                .andExpect(jsonPath("$.[1].bookingId").value(2));
    }

    // ==========================================
    // 3. TEST CHUYỂN PHÒNG (PUT /transfer)
    // ==========================================
    @Test
    void testTransferRoom_Success() throws Exception {
        when(bookingService.transferRoom(eq(1), eq(102))).thenReturn(mockBooking);

        mockMvc.perform(put("/api/bookings/1/transfer")
                        .param("newRoomId", "102"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // 4. TEST ĐẶT PHÒNG TRƯỚC (POST /reserve)
    // ==========================================
    @Test
    void testReserveRoom_Success() throws Exception {
        when(bookingService.reserveRoom(anyInt(), anyInt(), anyInt(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(mockBooking);

        mockMvc.perform(post("/api/bookings/reserve")
                        .param("guestId", "8521")
                        .param("roomId", "3")
                        .param("staffId", "1")
                        .param("checkInDate", "2026-03-25")
                        .param("checkOutDate", "2026-03-28"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // 5. TEST NHẬN PHÒNG ĐÃ ĐẶT (PUT /checkin-reserved)
    // ==========================================
    @Test
    void testCheckInReservedRoom_Success() throws Exception {
        when(bookingService.checkInReservedRoom(1)).thenReturn(mockBooking);

        mockMvc.perform(put("/api/bookings/1/checkin-reserved"))
                .andExpect(status().isOk());
    }

    // ==========================================
    // 6. TEST HỦY ĐẶT PHÒNG (PUT /cancel)
    // ==========================================
    @Test
    void testCancelBooking_Success() throws Exception {
        when(bookingService.cancelBooking(1)).thenReturn(mockBooking);

        mockMvc.perform(put("/api/bookings/1/cancel"))
                .andExpect(status().isOk());
    }
}