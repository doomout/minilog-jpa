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

    private final ArticleService articleService;
    private final FollowService followService;
    private final UserService userService;

    @Autowired
    public GraphQLQueryController(
            ArticleService articleService, FollowService followService, UserService userService) {
        this.articleService = articleService;
        this.followService = followService;
        this.userService = userService;
    }

    @QueryMapping
    public List<ArticleResponse> getArticles(@Argument Long userId) {
        return articleService.getArticleListByUserId(userId).stream()
                .map(DtoGraphqlMapper::toGraphql)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public ArticleResponse getArticle(@Argument Long articleId) {
        return DtoGraphqlMapper.toGraphql(articleService.getArticleById(articleId));
    }

    @QueryMapping
    public List<ArticleResponse> getFeedList(@Argument Long followerId) {
        return articleService.getFeedListByFollowerId(followerId).stream()
                .map(DtoGraphqlMapper::toGraphql)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<FollowResponse> getFollowList(@Argument Long followerId) {
        return followService.getFollowList(followerId).stream()
                .map(DtoGraphqlMapper::toGraphql)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<UserResponse> getUsers() {
        return userService.getUsers().stream()
                .map(DtoGraphqlMapper::toGraphql)
                .collect(Collectors.toList());
    }

    @QueryMapping
    public UserResponseDto getUserById(@Argument Long userId) {
        return userService.getUserById(userId).orElse(null);
    }
}
