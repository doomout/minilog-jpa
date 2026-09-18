package com.asdf.minilog.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class AuthenticationResponseDto { // 인증 성공 후 JWT 를 반환하는 DTO
    @NonNull private String jwt;
}
