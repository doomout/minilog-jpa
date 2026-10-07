package com.asdf.minilog.util;

import com.asdf.minilog.dto.ArticleResponseDto;
import com.asdf.minilog.dto.FollowResponseDto;
import com.asdf.minilog.dto.UserResponseDto;
import com.asdf.minilog.graphql.response.ArticleResponse;
import com.asdf.minilog.graphql.response.FollowResponse;
import com.asdf.minilog.graphql.response.UserResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * 기존 Service 계층에서 사용하는 ResponseDto를
 * GraphQL API에서 사용하는 Response 객체로 변환하는 Mapper 클래스
 *
 * REST와 GraphQL이 동일한 Service 계층을 사용하면서도
 * 각각 필요한 응답 객체를 따로 사용할 수 있도록 변환 역할을 담당함
 */
public class DtoGraphqlMapper {

    /**
     * ArticleResponseDto를 GraphQL용 ArticleResponse로 변환
     *
     * 게시글의 기본 정보와 작성자 정보를 그대로 옮기고,
     * LocalDateTime 타입의 createdAt을 GraphQL DateTime Scalar에서 사용할
     * OffsetDateTime 타입으로 변환함
     */
    public static ArticleResponse toGraphql(ArticleResponseDto article) {

        // 기존 DTO의 LocalDateTime에 UTC(+00:00) 오프셋을 추가하여
        // GraphQL DateTime에서 사용할 OffsetDateTime으로 변환
        OffsetDateTime createdAt =
                article.getCreatedAt().atOffset(ZoneOffset.UTC);

        // ArticleResponseDto의 값을 GraphQL용 ArticleResponse로 변환
        return ArticleResponse.builder()
                .articleId(article.getArticleId())
                .content(article.getContent())
                .authorId(article.getAuthorId())
                .authorName(article.getAuthorName())
                .createdAt(createdAt)
                .build();
    }

    /**
     * FollowResponseDto를 GraphQL용 FollowResponse로 변환
     *
     * followerId : 팔로우를 하는 사용자 ID
     * followeeId : 팔로우를 당하는 사용자 ID
     */
    public static FollowResponse toGraphql(FollowResponseDto follow) {

        return FollowResponse.builder()
                .followerId(follow.getFollowerId())
                .followeeId(follow.getFolloweeId())
                .build();
    }

    /**
     * UserResponseDto를 GraphQL용 UserResponse로 변환
     *
     * 사용자 ID와 username을 GraphQL 응답 객체에 전달
     */
    public static UserResponse toGraphql(UserResponseDto user) {

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}