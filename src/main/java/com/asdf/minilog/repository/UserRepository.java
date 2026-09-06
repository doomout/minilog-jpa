package com.asdf.minilog.repository;

import com.asdf.minilog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { //User Entity 와 관련된 DB 연산을 담당하는 인터페이스
    // 사용자 이름(username)으로 User Entity 조회
    Optional<User> findByUsername(String username);
}
