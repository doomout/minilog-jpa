package com.asdf.minilog.repository;

import com.asdf.minilog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> { //User Entity 와 관련된 DB 연산을 담당하는 인터페이스
    Optional<User> findByUsername(String username);
}
