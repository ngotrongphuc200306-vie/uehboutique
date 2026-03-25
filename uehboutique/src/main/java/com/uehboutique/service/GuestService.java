package com.uehboutique.service;

import com.uehboutique.entity.Guest;
import com.uehboutique.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;

    // 1. Thêm khách hàng mới
    public Guest saveGuest(Guest guest) {
        // Sau này có thể thêm logic kiểm tra SĐT trùng ở đây
        return guestRepository.save(guest);
    }

    // 2. Lấy danh sách có phân trang
    public Page<Guest> getAllGuests(int page, int size) {
        return guestRepository.findAll(PageRequest.of(page, size));
    }

    // 3. Tìm khách theo ID
    public Optional<Guest> getGuestById(Integer id) {
        return guestRepository.findById(id);
    }

    // 4. Tìm khách theo Số điện thoại
    public Optional<Guest> getGuestByPhone(String phone) {
        return guestRepository.findByPhone(phone);
    }

    // 5. Cập nhật thông tin khách hàng
    public Guest updateGuest(Integer id, Guest guestDetails) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + id));

        guest.setGuestName(guestDetails.getGuestName());
        guest.setPhone(guestDetails.getPhone());
        // Bác có thêm trường nào (CCCD, Email...) thì set thêm ở đây

        return guestRepository.save(guest);
    }
}