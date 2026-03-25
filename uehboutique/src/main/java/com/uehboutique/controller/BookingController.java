package com.uehboutique.controller;

import com.uehboutique.entity.Booking;
import com.uehboutique.service.BookingService;
import com.uehboutique.dto.request.CheckInRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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
    @GetMapping
    public ResponseEntity<?> getAllBookings() {
        try {
            List<Booking> rawBookings = bookingService.getAllBookings();

            // Cách an toàn: Dùng Stream để sắp xếp và gom vào một List mới hoàn toàn
            // Không sửa trực tiếp lên list của Database trả về để tránh lỗi 500
            List<Booking> sortedBookings = rawBookings.stream().sorted((b1, b2) -> {
                try {
                    // Thêm .trim() để phòng trường hợp trong DB số phòng bị dính khoảng trắng
                    int room1 = (b1.getRoom() != null && b1.getRoom().getRoomNumber() != null)
                            ? Integer.parseInt(b1.getRoom().getRoomNumber().trim()) : 0;
                    int room2 = (b2.getRoom() != null && b2.getRoom().getRoomNumber() != null)
                            ? Integer.parseInt(b2.getRoom().getRoomNumber().trim()) : 0;
                    return Integer.compare(room1, room2);
                } catch (Exception ex) {
                    return 0; // Nếu phòng nào tên chứa chữ không parse số được thì bỏ qua, không cho sập Server
                }
            }).collect(java.util.stream.Collectors.toList());
//
//            if (sortedBookings.isEmpty()) {
//                return ResponseEntity.noContent().build();
//            }
            return ResponseEntity.ok(sortedBookings);

        } catch (Exception e) {
            e.printStackTrace(); // In log đỏ ra console của IntelliJ/Eclipse để dễ tìm
            return ResponseEntity.internalServerError().body("Lỗi ở Backend: " + e.getMessage());
        }
    }


    @PutMapping("/{bookingId}/transfer")
    public ResponseEntity<?> transferRoom(@PathVariable Integer bookingId, @RequestParam Integer newRoomId) {
        try {
            return ResponseEntity.ok(bookingService.transferRoom(bookingId, newRoomId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error transferring room: " + e.getMessage());
        }
    }

    // 1. API: Đặt phòng trước
    // Cách gọi: POST http://localhost:8080/api/bookings/reserve?guestId=8521&roomId=3&staffId=1&checkInDate=2026-03-25&checkOutDate=2026-03-28
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveRoom(
            @RequestParam Integer guestId,
            @RequestParam Integer roomId,
            @RequestParam Integer staffId,
            @RequestParam String checkInDate,
            @RequestParam String checkOutDate) {
        try {
            return ResponseEntity.ok(bookingService.reserveRoom(guestId, roomId, staffId,
                    LocalDate.parse(checkInDate), LocalDate.parse(checkOutDate)));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body("Error reserving room: " + e.getMessage());
        }
    }

    // 2. API: Khách đến nhận phòng đã đặt
    // Cách gọi: PUT http://localhost:8080/api/bookings/2/checkin-reserved
    @PutMapping("/{bookingId}/checkin-reserved")
    public ResponseEntity<?> checkInReservedRoom(@PathVariable Integer bookingId) {
        try {
            return ResponseEntity.ok(bookingService.checkInReservedRoom(bookingId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error checking in reserved room: " + e.getMessage());
        }
    }

    // 3. API: Khách hủy đặt phòng
    // Cách gọi: PUT http://localhost:8080/api/bookings/2/cancel
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Integer bookingId) {
        try {
            return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error canceling booking: " + e.getMessage());
        }
    }
}


