package com.asdf.minilog.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateArticleInput {
    private Long articleId; // 수정할 게시글 id
    private String content; // 변경할 내용
}
