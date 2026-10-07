package com.asdf.minilog.graphql;

import com.asdf.minilog.dto.UserResponseDto;
import com.asdf.minilog.graphql.response.ArticleResponse;
import com.asdf.minilog.graphql.response.FollowResponse;
import com.asdf.minilog.graphql.response.UserResponse;
import com.asdf.minilog.service.ArticleService;
import com.asdf.minilog.service.FollowService;
import com.asdf.minilog.service.UserService;
import com.asdf.minilog.util.DtoGraphqlMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GraphQLQueryController {

    // 게시글 관련 비즈니스 로직을 처리하는 서비스
    private final ArticleService articleService;

    // 팔로우 관련 비즈니스 로직을 처리하는 서비스
    private final FollowService followService;

    // 사용자 관련 비즈니스 로직을 처리하는 서비스
    private final UserService userService;

    // 생성자 주입을 통해 필요한 Service 객체를 주입받음
    @Autowired
    public GraphQLQueryController(
            ArticleService articleService, FollowService followService, UserService userService) {
        this.articleService = articleService;
        this.followService = followService;
        this.userService = userService;
    }

    // 특정 사용자가 작성한 게시글 목록 조회
    // schema.graphqls의 Query.getArticles와 매핑됨
    // @Argument는 GraphQL 요청으로 전달된 userId 값을 메서드 파라미터에 매핑
    @QueryMapping
    public List<ArticleResponse> getArticles(@Argument Long userId) {
        return articleService.getArticleListByUserId(userId).stream()
                // Service에서 반환한 DTO를 GraphQL 응답 객체로 변환
                .map(DtoGraphqlMapper::toGraphql)
                .collect(Collectors.toList());
    }

    // 게시글 ID를 이용하여 게시글 하나를 조회
    // schema.graphqls의 Query.getArticle과 매핑됨
    @QueryMapping
    public ArticleResponse getArticle(@Argument Long articleId) {
        return DtoGraphqlMapper.toGraphql(articleService.getArticleById(articleId));
    }

    // 특정 사용자가 팔로우하고 있는 사용자들의 게시글(피드) 목록 조회
    // schema.graphqls의 Query.getFeedList와 매핑됨
    @QueryMapping
    public List<ArticleResponse> getFeedList(@Argument Long followerId) {
        return articleService.getFeedListByFollowerId(followerId).stream()
                // 각 게시글 DTO를 GraphQL 응답 객체로 변환
                .map(DtoGraphqlMapper::toGraphql)
                .collect(Collectors.toList());
    }

    // 특정 사용자의 팔로우 목록 조회
    // schema.graphqls의 Query.getFollowList와 매핑됨
    @QueryMapping
    public List<FollowResponse> getFollowList(@Argument Long followerId) {
        return followService.getFollowList(followerId).stream()
                // Follow DTO를 GraphQL 응답 객체로 변환
                .map(DtoGraphqlMapper::toGraphql)
                .collect(Collectors.toList());
    }

    // 전체 사용자 목록 조회
    // schema.graphqls의 Query.getUsers와 매핑됨
    @QueryMapping
    public List<UserResponse> getUsers() {
        return userService.getUsers().stream()
                // User DTO를 GraphQL 응답 객체로 변환
                .map(DtoGraphqlMapper::toGraphql)
                .collect(Collectors.toList());
    }

    // 사용자 ID를 이용하여 사용자 한 명을 조회
    // schema.graphqls의 Query.getUserById와 매핑됨
    // 사용자가 존재하지 않으면 Optional이 비어 있으므로 null 반환
    @QueryMapping
    public UserResponseDto getUserById(@Argument Long userId) {
        return userService.getUserById(userId).orElse(null);
    }
}