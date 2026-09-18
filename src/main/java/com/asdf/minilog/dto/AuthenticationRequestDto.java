package com.asdf.minilog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDto { // 인증 요청 수신 DTO
    private String username;
    private String password;
}
