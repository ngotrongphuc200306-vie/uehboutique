package com.uehboutique.service;

import com.uehboutique.entity.Booking;
import com.uehboutique.entity.Invoice;
import com.uehboutique.entity.Room;
import com.uehboutique.entity.ServiceUsage;
import com.uehboutique.repository.BookingRepository;
import com.uehboutique.repository.InvoiceRepository;
import com.uehboutique.repository.RoomRepository;
import com.uehboutique.repository.ServiceUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ServiceUsageRepository serviceUsageRepository;

    @Transactional
    public Invoice checkout(Integer bookingId, String paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Booking!"));

        if ("Check-out".equals(booking.getStatus())) {
            throw new RuntimeException("Phòng này đã được check-out rồi!");
        }

        // 1. Tính tiền phòng
        LocalDate checkOutDate = LocalDate.now();
        long daysStayed = ChronoUnit.DAYS.between(booking.getCheckInDate(), checkOutDate);
        if (daysStayed <= 0) daysStayed = 1; // Ở trong ngày tính là 1 ngày

        Room room = booking.getRoom();
        BigDecimal roomPrice = room.getRoomType().getBasePrice();
        BigDecimal totalRoomFee = roomPrice.multiply(BigDecimal.valueOf(daysStayed));

        // 2. Tính tiền dịch vụ
        List<ServiceUsage> usages = serviceUsageRepository.findByBooking_BookingId(bookingId);
        BigDecimal totalServiceFee = usages.stream()
                .map(usage -> usage.getService().getUnitPrice().multiply(BigDecimal.valueOf(usage.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Cập nhật trạng thái Booking & Room (US03)
        booking.setStatus("Check-out");
        booking.setCheckOutDate(checkOutDate);
        bookingRepository.save(booking);

        room.setStatus("Dirty"); // Chuyển sang chờ dọn dẹp
        roomRepository.save(room);

        // 4. Tạo Hóa Đơn
        Invoice invoice = new Invoice();
        invoice.setBooking(booking);
        invoice.setTotalAmount(totalRoomFee.add(totalServiceFee));
        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaymentDate(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }
    public java.util.Map<String, Object> previewCheckout(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Booking!"));

        LocalDate checkOutDate = LocalDate.now();
        long daysStayed = ChronoUnit.DAYS.between(booking.getCheckInDate(), checkOutDate);
        if (daysStayed <= 0) daysStayed = 1;

        Room room = booking.getRoom();
        BigDecimal roomPrice = room.getRoomType().getBasePrice();
        BigDecimal totalRoomFee = roomPrice.multiply(BigDecimal.valueOf(daysStayed));

        List<ServiceUsage> usages = serviceUsageRepository.findByBooking_BookingId(bookingId);
        BigDecimal totalServiceFee = usages.stream()
                .map(usage -> usage.getService().getUnitPrice().multiply(BigDecimal.valueOf(usage.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        java.util.Map<String, Object> previewData = new java.util.HashMap<>();
        previewData.put("daysStayed", daysStayed);
        previewData.put("roomTotal", totalRoomFee);
        previewData.put("serviceTotal", totalServiceFee);
        previewData.put("grandTotal", totalRoomFee.add(totalServiceFee));

        return previewData;
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
}