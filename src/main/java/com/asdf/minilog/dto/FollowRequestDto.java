package com.asdf.minilog.dto;

import lombok.Data;
import lombok.NonNull;

@Data
public class FollowRequestDto { // 팔로우 요청을 위한 DTO
    @NonNull private Long followerId;

    @NonNull private Long followeeId;
}
