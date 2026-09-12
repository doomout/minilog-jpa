package com.asdf.minilog.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ArticleResponseDto { // 게시글 작성, 수정, 조회에 대한 응답에 사용되는 DTO
    @NonNull private Long articleId;

    @NonNull private String content;

    @NonNull private Long authorId;

    @NonNull private String authorName;

    @NonNull private LocalDateTime createdAt;
}
