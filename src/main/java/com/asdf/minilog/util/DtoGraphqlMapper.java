package com.asdf.minilog.util;

import com.asdf.minilog.dto.ArticleResponseDto;
import com.asdf.minilog.dto.FollowResponseDto;
import com.asdf.minilog.dto.UserResponseDto;
import com.asdf.minilog.graphql.response.ArticleResponse;
import com.asdf.minilog.graphql.response.FollowResponse;
import com.asdf.minilog.graphql.response.UserResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DtoGraphqlMapper {
    public static ArticleResponse toGraphql(ArticleResponseDto article) {
        OffsetDateTime createdAt = article.getCreatedAt().atOffset(ZoneOffset.UTC);

        return ArticleResponse.builder()
                .articleId(article.getArticleId())
                .content(article.getContent())
                .authorId(article.getAuthorId())
                .authorName(article.getAuthorName())
                .createdAt(createdAt)
                .build();
    }

    public static FollowResponse toGraphql(FollowResponseDto follow) {
        return FollowResponse.builder()
                .followerId(follow.getFollowerId())
                .followeeId(follow.getFolloweeId())
                .build();
    }

    public static UserResponse toGraphql(UserResponseDto user) {
        return UserResponse.builder().id(user.getId()).username(user.getUsername()).build();
    }
}
