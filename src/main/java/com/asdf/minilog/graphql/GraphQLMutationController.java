package com.asdf.minilog.graphql;

import com.asdf.minilog.dto.UserRequestDto;
import com.asdf.minilog.graphql.input.CreateArticleInput;
import com.asdf.minilog.graphql.input.CreateUserInput;
import com.asdf.minilog.graphql.input.UpdateArticleInput;
import com.asdf.minilog.graphql.input.UpdateUserInput;
import com.asdf.minilog.graphql.response.ArticleResponse;
import com.asdf.minilog.graphql.response.FollowResponse;
import com.asdf.minilog.graphql.response.UserResponse;
import com.asdf.minilog.security.MinilogUserDetails;
import com.asdf.minilog.service.ArticleService;
import com.asdf.minilog.service.FollowService;
import com.asdf.minilog.service.UserService;
import com.asdf.minilog.util.DtoGraphqlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
public class GraphQLMutationController {

    // 게시글 관련 생성, 수정, 삭제 비즈니스 로직을 처리하는 서비스
    private final ArticleService articleService;

    // 팔로우 / 언팔로우 비즈니스 로직을 처리하는 서비스
    private final FollowService followService;

    // 사용자 생성, 수정, 삭제 비즈니스 로직을 처리하는 서비스
    private final UserService userService;

    // 생성자 주입을 통해 필요한 Service 객체를 주입받음
    @Autowired
    public GraphQLMutationController(
            ArticleService articleService, FollowService followService, UserService userService) {
        this.articleService = articleService;
        this.followService = followService;
        this.userService = userService;
    }

    // 현재 JWT 인증이 완료된 사용자의 정보를 가져오는 공통 메서드
    //
    // JwtRequestFilter에서 JWT 검증에 성공하면
    // MinilogUserDetails가 포함된 Authentication 객체를
    // SecurityContext에 저장해 두므로 여기에서 다시 꺼내 사용할 수 있음
    private MinilogUserDetails getCurrentUser() {
        return (MinilogUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // 새 게시글 생성
    // schema.graphqls의 Mutation.createArticle과 매핑됨
    //
    // 게시글 작성자는 클라이언트가 전달하지 않고
    // JWT로 인증된 현재 사용자의 ID를 사용함
    @MutationMapping
    public ArticleResponse createArticle(@Argument CreateArticleInput input) {
        MinilogUserDetails userDetails = getCurrentUser();

        return DtoGraphqlMapper.toGraphql(
                articleService.createArticle(input.getContent(), userDetails.getId()));
    }

    // 기존 게시글 수정
    // schema.graphqls의 Mutation.updateArticle과 매핑됨
    //
    // 현재 로그인한 사용자 ID와 수정할 게시글 ID를 Service에 전달하여
    // 해당 사용자가 게시글을 수정할 권한이 있는지 확인할 수 있도록 함
    @MutationMapping
    public ArticleResponse updateArticle(@Argument UpdateArticleInput input) {
        MinilogUserDetails userDetails = getCurrentUser();

        return DtoGraphqlMapper.toGraphql(
                articleService.updateArticle(
                        userDetails.getId(),
                        input.getArticleId(),
                        input.getContent()));
    }

    // 게시글 삭제
    // schema.graphqls의 Mutation.deleteArticle과 매핑됨
    //
    // 현재 로그인한 사용자 ID와 삭제할 게시글 ID를 Service에 전달
    // 정상적으로 삭제되면 true 반환
    @MutationMapping
    public Boolean deleteArticle(@Argument Long articleId) {
        MinilogUserDetails userDetails = getCurrentUser();

        articleService.deleteArticle(userDetails.getId(), articleId);

        return true;
    }

    // 다른 사용자를 팔로우
    // schema.graphqls의 Mutation.follow와 매핑됨
    //
    // follower  : 현재 로그인한 사용자
    // followee  : 팔로우 대상 사용자
    @MutationMapping
    public FollowResponse follow(@Argument Long followeeId) {
        MinilogUserDetails userDetails = getCurrentUser();

        return DtoGraphqlMapper.toGraphql(
                followService.follow(userDetails.getId(), followeeId));
    }

    // 다른 사용자를 언팔로우
    // schema.graphqls의 Mutation.unfollow와 매핑됨
    //
    // 현재 로그인한 사용자와 followeeId 사이의 팔로우 관계를 삭제
    @MutationMapping
    public Boolean unfollow(@Argument Long followeeId) {
        MinilogUserDetails userDetails = getCurrentUser();

        followService.unfollow(userDetails.getId(), followeeId);

        return true;
    }

    // 새 사용자 생성(회원가입)
    // schema.graphqls의 Mutation.createUser와 매핑됨
    //
    // GraphQL의 CreateUserInput을
    // 기존 Service 계층에서 사용하는 UserRequestDto로 변환한 뒤 전달
    @MutationMapping
    public UserResponse createUser(@Argument CreateUserInput input) {
        return DtoGraphqlMapper.toGraphql(
                userService.createUser(
                        UserRequestDto.builder()
                                .username(input.getUsername())
                                .password(input.getPassword())
                                .build()));
    }

    // 사용자 정보 수정
    // schema.graphqls의 Mutation.updateUser와 매핑됨
    //
    // 현재 인증된 사용자 정보와 수정 대상 userId,
    // 수정할 username/password를 Service에 전달
    @MutationMapping
    public UserResponse updateUser(@Argument UpdateUserInput input) {
        MinilogUserDetails userDetails = getCurrentUser();

        return DtoGraphqlMapper.toGraphql(
                userService.updateUser(
                        userDetails,
                        input.getUserId(),
                        UserRequestDto.builder()
                                .username(input.getUsername())
                                .password(input.getPassword())
                                .build()));
    }

    // 사용자 삭제
    // schema.graphqls의 Mutation.deleteUser와 매핑됨
    // 정상적으로 삭제되면 true 반환
    @MutationMapping
    public Boolean deleteUser(@Argument Long userId) {
        userService.deleteUser(userId);

        return true;
    }
}