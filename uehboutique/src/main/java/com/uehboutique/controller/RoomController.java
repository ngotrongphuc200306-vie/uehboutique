package com.uehboutique.controller;

import com.uehboutique.entity.Room;
import com.uehboutique.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*") // Cho phép Frontend (React/Vue) gọi API mà không bị chặn lỗi CORS
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/status")
    public ResponseEntity<List<Room>> getAllRoomStatus(@RequestParam String status) {
        return ResponseEntity.ok(roomService.getRoomsByStatus(status));
    }
}
