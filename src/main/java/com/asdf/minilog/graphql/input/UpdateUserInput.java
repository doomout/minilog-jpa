package com.asdf.minilog.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInput {
    private Long userId; // 수정할 사용자 id
    private String username; // 새로운 사용자 이름
    private String password; // 새로운 비밀번호
}
