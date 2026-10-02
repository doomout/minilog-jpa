package com.asdf.minilog.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateArticleInput { // 게시글을 생성할 때 사용되는 입력 타입
    private String content;
}
