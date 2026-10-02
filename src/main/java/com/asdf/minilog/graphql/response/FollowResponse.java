package com.asdf.minilog.graphql.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class FollowResponse {
    @NonNull Long followerId; // 팔로우한 사용자

    @NonNull private Long followeeId; // 팔로우 대상 사용자 정보
}
