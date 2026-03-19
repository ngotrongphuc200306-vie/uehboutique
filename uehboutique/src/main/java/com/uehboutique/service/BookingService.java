package com.uehboutique.service;

import com.uehboutique.entity.Booking;
import com.uehboutique.entity.Guest;
import com.uehboutique.entity.Room;
import com.uehboutique.entity.Staff;
import com.uehboutique.repository.BookingRepository;
import com.uehboutique.repository.GuestRepository;
import com.uehboutique.repository.RoomRepository;
import com.uehboutique.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;
    private final StaffRepository staffRepository;

    // Logic Check-in(US-02)
    @Transactional
    public Booking processCheckIn(Integer guessId, Integer roomId, Integer staffId, LocalDate checkOutDate) {
        // 1. Tìm thông tin Khách, Phòng, Nhân viên trong DB
        Guest guest = guestRepository.findById(guessId)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // 2. KIỂM TRA LOGIC THỰC TẾ: Phòng có trống không?
        if(!room.getStatus().equalsIgnoreCase("Empty")){
            throw new RuntimeException("Error: Room " + room.getRoomNumber() + "not empty, can not Check-in");
        }

        // 3. Tạo phiếu Đặt phòng (Booking)
        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setRoom(room);
        booking.setStaff(staff);
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckOutDate(checkOutDate);
        booking.setStatus("Check-in");

        // Change Status
        room.setStatus("Currently");
        roomRepository.save(room);// Cập nhật phòng vào Database

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
