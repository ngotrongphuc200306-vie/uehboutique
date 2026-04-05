package com.uehboutique.dto.request;

import lombok.*;

@Data
@AllArgsConstructor // Tạo hàm có 2 tham số LoginRequest(username, password)
@NoArgsConstructor  // Tạo hàm không có tham số LoginRequest() để dự phòng
public class LoginRequest {
    private String username;
    private String password;
}