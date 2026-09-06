package com.asdf.minilog.repository;

import com.asdf.minilog.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> { // Article Entity 와 관련된 DB 연산을 담당
    // 특정 작성자가 작성한 모든 게시글을 조회하는 메서드
    // AuthorId 필드에 대해 주어진 값과 일치하는 Article 들을 조회
    List<Article> findAllByAuthorId(Long authorId);

    // 팔로우 관계를 기반으로 팔로워가 작성한 게시글을 최신순으로 조회하는 메서드
    // Article 과 Follow 테이블을 조인하는 JPQL 쿼리문
    // 지정된 사용자가 팔로우하는 author(작성자)가 작성한 모든 게시글을 최신순으로 정렬하여 조회
    @Query("SELECT a FROM Article a JOIN a.author u JOIN Follow f"
            + " ON u.id = f.followee.id WHERE"
            + " f.follower.id = :authorId ORDER BY a.createdAt DESC")
    List<Article> findAllByFollowerId(@Param("authorId") Long authorId);
}
