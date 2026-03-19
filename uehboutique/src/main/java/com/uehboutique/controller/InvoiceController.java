package com.uehboutique.controller;

import com.uehboutique.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}