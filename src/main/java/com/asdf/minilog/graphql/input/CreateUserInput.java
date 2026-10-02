package com.asdf.minilog.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserInput { // 사용자를 생성할 때 사용되는 입력 타입
    private String username;
    private String password;
}
