package com.asdf.minilog.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class FollowResponseDto { // 팔로우 관계 생성, 조회시 응답 DTO
    @NonNull private Long followerId;

    @NonNull private Long followeeId;
}
