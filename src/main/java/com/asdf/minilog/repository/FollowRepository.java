package com.asdf.minilog.repository;

import com.asdf.minilog.entity.Follow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> { // Follow Entity 와 DB 연산을 담당
    // 특성 사용자가 팔로우하는 사용자 목록을 조회하는 메서드
    List<Follow> findByFollowerId(Long followerId);

    // 특정 사용자와 팔로우 대상 간의 관계가 존재하는지 확인하는 메서드
    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
}
