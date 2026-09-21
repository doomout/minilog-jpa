package com.asdf.minilog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ArticleRequestDto { // 게시글 작성, 수정 요청용 DTO
    @NonNull private String content;

    @Deprecated(since = "2.0", forRemoval = true)
    @Schema(
          description = "작성자 ID(이 필드는 더 이상 사용되지 않습니다.)",
          example = "0",
          required = true,
          deprecated = true
    )
    @NonNull private Long authorId;
}
