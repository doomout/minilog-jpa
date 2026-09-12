package com.asdf.minilog.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class ArticleRequestDto { // 게시글 작성, 수정 요청용 DTO
    @NonNull private String content;

    @NonNull private Long authorId;
}
