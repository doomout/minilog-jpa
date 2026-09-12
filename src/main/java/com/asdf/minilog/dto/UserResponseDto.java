package com.asdf.minilog.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class UserResponseDto { // 등록,수정,조회에 대한 응답을 위한 DTO
    @NonNull private Long id;

    @NonNull private String username;
}
