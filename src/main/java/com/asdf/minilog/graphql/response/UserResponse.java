package com.asdf.minilog.graphql.response;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class UserResponse {
    @NonNull private Long id; // 사용자 ID

    @NonNull private String username; // 사용자 이름
}
