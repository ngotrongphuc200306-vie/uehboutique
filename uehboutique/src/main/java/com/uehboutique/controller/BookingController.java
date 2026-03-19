package com.uehboutique.controller;

import com.uehboutique.entity.Booking;
import com.uehboutique.service.BookingService;
import com.uehboutique.dto.request.CheckInRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    // API 3: Xử lý Check-in
    @PostMapping("/checkin")
    public ResponseEntity<?> checkin(@RequestBody CheckInRequest request) {
        try {
            Booking newBooking = bookingService.processCheckIn(
                    request.getGuestId(),
                    request.getRoomId(),
                    request.getStaffId(),
                    request.getCheckOutDate()
            );
            return ResponseEntity.ok(newBooking);
        } catch (Exception e) {
            // Nếu có lỗi (VD: Phòng không trống), trả về thông báo lỗi cho Frontend hiển thị popup
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}


