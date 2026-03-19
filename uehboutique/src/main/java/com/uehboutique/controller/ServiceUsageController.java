package com.uehboutique.controller;

import com.uehboutique.service.ServiceUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/service-usages")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ServiceUsageController {

    private final ServiceUsageService serviceUsageService;

    // Cách gọi: POST http://localhost:8080/api/service-usages?bookingId=1&serviceId=2&quantity=2
    @PostMapping
    public ResponseEntity<?> addService(@RequestParam Integer bookingId,
                                        @RequestParam Integer serviceId,
                                        @RequestParam Integer quantity) {
        try {
            return ResponseEntity.ok(serviceUsageService.addServiceToBooking(bookingId, serviceId, quantity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}