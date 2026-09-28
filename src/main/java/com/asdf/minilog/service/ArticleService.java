package com.asdf.minilog.service;

import com.asdf.minilog.dto.ArticleResponseDto;
import com.asdf.minilog.entity.Article;
import com.asdf.minilog.entity.User;
import com.asdf.minilog.exception.ArticleNotFoundException;
import com.asdf.minilog.exception.NotAuthorizedException;
import com.asdf.minilog.exception.UserNotFoundException;
import com.asdf.minilog.repository.ArticleRepository;
import com.asdf.minilog.repository.UserRepository;
import com.asdf.minilog.util.EntityDtoMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(isolation = Isolation.REPEATABLE_READ) // 비반복 읽기를 방지,데이터가 변하지 않도록 보장, 일관된 데이터 조회 제공
public class ArticleService { // 게시글과 관련된 비즈니스 로직
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    // 게시글 생성
    public ArticleResponseDto createArticle(String content, Long userId) {
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format(
                                                        "해당 아이디(%d)를 가진 사용자를 찾을 수 없습니다.", userId)));
        Article article = Article.builder().content(content).author(user).build();

        Article savedArticle = articleRepository.save(article);

        return EntityDtoMapper.toDto(savedArticle);
    }

    // 게시글 삭제
    public void deleteArticle(Long authorId, Long articleId) {
        Article article =
                articleRepository
                        .findById(articleId)
                        .orElseThrow(
                                () ->
                                        new ArticleNotFoundException(
                                                String.format(
                                                        "해당 아이디(%d)를 가진 게시글을 찾을 수 없습니다.",
                                                        articleId)));

        if (!article.getAuthor().getId().equals(authorId)) {
            throw new NotAuthorizedException("게시글 작성자만 삭제할 수 있습니다.");
        }

        articleRepository.deleteById(articleId);
    }

    // 게시글 수정
    public ArticleResponseDto updatedArticle(Long authorId, Long articleId, String content) {
        Article article =
                articleRepository
                        .findById(articleId)
                        .orElseThrow(
                                () ->
                                        new ArticleNotFoundException(
                                                String.format(
                                                        "해당 아이디(%d)를 가진 게시글을 찾을수 없습니다.",
                                                        articleId)));

        if (!article.getAuthor().getId().equals(authorId)) {
            throw new NotAuthorizedException("게시글 작성자만 수정할 수 있습니다.");
        }

        article.setContent(content);
        Article updatedArticle = articleRepository.save(article);

        return EntityDtoMapper.toDto(updatedArticle);
    }

    // 게시글 id로 게시글 조회
    @Transactional(readOnly = true)
    public ArticleResponseDto getArticleById(Long articleId) {
        Article article =
                articleRepository
                        .findById(articleId)
                        .orElseThrow(
                                () ->
                                        new ArticleNotFoundException(
                                                String.format(
                                                        "해당 아이디(%d)를 가진 게시글을 찾을 수 없습니다.",
                                                        articleId)));

        return EntityDtoMapper.toDto(article);
    }

    // 특정 사용자가 팔로우하고 있는 사람들의 게시글(피드)를 조회
    @Transactional(readOnly = true)
    public List<ArticleResponseDto> getFeedListByFollowerId(Long userId) {
        // 1. 전달받은 userId로 실제 사용자가 존재하는지 먼저 조회
        // findById()의 결과는 Optional<User>이므로,
        // 사용자가 없으면 UserNotFoundException 발생
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format(
                                                        "해당 아이디(%d)를 가진 게시글을 찾을 수 없스니다.", userId)));

        // 2. 해당 사용자가 팔로우하는 사람들의 게시글을 Repository에서 조회
        // findAllByFollowerId()의 실제 조회 로직은 Repository 쪽에서 정의됨
        // 결과는 여러 게시글이므로 List<Article> 계열의 값
        var feedList = articleRepository.findAllByFollowerId(user.getId());

        // 3. 조회된 Entity 목록을 그대로 Controller에 반환하지 않고
        // 각각 ArticleResponseDto로 변환
        //
        // feedList.stream()
        //     → Article 목록을 하나씩 처리할 수 있는 Stream으로 변환
        //
        // .map(EntityDtoMapper::toDto)
        //     → 각각의 Article에 EntityDtoMapper.toDto(article)를 실행
        //
        // .toList()
        //     → 변환된 ArticleResponseDto들을 다시 List로 묶음
        return feedList.stream().map(EntityDtoMapper::toDto).toList();
    }

    // 특정 사용자가 직접 작성한 모든 게시글 조회
    @Transactional(readOnly = true)
    public List<ArticleResponseDto> getArticleListByUserId(Long userId) {
        // 1. userId에 해당하는 사용자가 실제로 존재하는지 확인
        // 존재하지 않으면 UserNotFoundException 발생
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format(
                                                        "해당 아이디(%d)를 가진 사용자를 찾을 수 없스니다.", userId)));

        // 2. 해당 사용자가 작성한 게시글을 모두 조회
        // AuthorId → Article.author.id를 조건으로 검색
        // 즉 개념적으로 WHERE author_id = userId
        var articlesList = articleRepository.findAllByAuthorId(user.getId());

        // 3. List<Article>을 List<ArticleResponseDto>로 변환
        return articlesList.stream().map(EntityDtoMapper::toDto).toList();
    }
}
