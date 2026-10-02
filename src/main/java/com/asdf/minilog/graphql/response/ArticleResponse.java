package com.asdf.minilog.graphql.response;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ArticleResponse {
    @NonNull private Long articleId; // 게시글 ID

    @NonNull private String content; // 게시글 내용

    @NonNull private Long authorId; // 작성자 ID

    @NonNull private String authorName; // 작성자 이름

    @NonNull private OffsetDateTime createdAt; // 생성 시각
}
