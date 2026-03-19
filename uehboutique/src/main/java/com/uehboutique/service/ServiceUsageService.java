package com.uehboutique.service;

import com.uehboutique.entity.Booking;
import com.uehboutique.entity.Service;
import com.uehboutique.entity.ServiceUsage;
import com.uehboutique.repository.BookingRepository;
import com.uehboutique.repository.ServiceRepository;
import com.uehboutique.repository.ServiceUsageRepository;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceUsageService {
    private final ServiceUsageRepository serviceUsageRepository;
    private final BookingRepository bookingRepository;
    private final ServiceRepository serviceRepository;

    public ServiceUsage addServiceToBooking(Integer bookingId, Integer serviceId, Integer quantity) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));

        ServiceUsage usage = new ServiceUsage();
        usage.setBooking(booking);
        usage.setService(service);
        usage.setQuantity(quantity);
        usage.setUsageTime(LocalDateTime.now());

        return serviceUsageRepository.save(usage);
    }
}
