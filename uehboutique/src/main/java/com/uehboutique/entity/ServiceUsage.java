package com.uehboutique.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "ServiceUsage")
@Data
public class ServiceUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer usageId;

    @ManyToOne
    @JoinColumn(name = "bookingId", nullable = false)
    private Booking booking;

    @ManyToOne
    @JoinColumn(name = "serviceId", nullable = false)
    private Service service;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "usage_time",nullable = false)
    private LocalDateTime usageTime;
}