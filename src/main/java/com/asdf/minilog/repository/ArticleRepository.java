package com.asdf.minilog.repository;

import com.asdf.minilog.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> { // Article Entity 와 관련된 DB 연산을 담당
    List<Article> findAllByAuthorId(Long authorId);

    @Query("SELECT a FROM Article a JOIN a.author u JOIN Follow f"
            + " ON u.id = f.followee.id WHERE"
            + " f.follower.id = :authorId ORDER BY a.createdAt DESC")
    List<Article> findAllByFollowerId(@Param("authorId") Long authorId);
}
