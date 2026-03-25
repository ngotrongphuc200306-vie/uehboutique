package com.uehboutique.controller;

import com.uehboutique.entity.Invoice;
import com.uehboutique.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    // Cách gọi: POST http://localhost:8080/api/invoices/checkout/1?paymentMethod=Card
    @PostMapping("/checkout/{bookingId}")
    public ResponseEntity<?> checkout(@PathVariable Integer bookingId, @RequestParam String paymentMethod) {
        try {
            return ResponseEntity.ok(invoiceService.checkout(bookingId, paymentMethod));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi Check-out: " + e.getMessage());
        }
    }

    // Nút Preview trên Frontend sẽ gọi API này
    @GetMapping("/preview/{bookingId}")
    public ResponseEntity<?> previewCheckout(@PathVariable Integer bookingId) {
        try {
            return ResponseEntity.ok(invoiceService.previewCheckout(bookingId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi Preview: " + e.getMessage());
        }
    }

    // Frontend sẽ gọi API này để đổ dữ liệu vào bảng Quản lý hóa đơn
    @GetMapping
    public ResponseEntity<?> getAllInvoices() {
        try {
            return ResponseEntity.ok(invoiceService.getAllInvoices());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi tải danh sách hóa đơn: " + e.getMessage());
        }
    }

}