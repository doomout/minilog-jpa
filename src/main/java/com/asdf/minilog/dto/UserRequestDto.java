package com.asdf.minilog.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class UserRequestDto { // 사용자 등록 및 수정 DTO (컨트롤러 -> 서비스 계층으로 전달)
    @NonNull private String username;

    @NonNull private String password;
}
