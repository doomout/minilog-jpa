package com.asdf.minilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

@Data
public class FollowRequestDto { // 팔로우 요청을 위한 DTO
    @Deprecated(since = "2.0", forRemoval = true)
    @Schema(
            description = "팔로우 ID(이 필드는 더 이상 사용되지 않습니다.)",
            example = "0",
            required = true,
            deprecated = true
    )
    private Long followerId;

    @NonNull private Long followeeId;
}
