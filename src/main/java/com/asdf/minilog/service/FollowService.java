package com.asdf.minilog.service;

import com.asdf.minilog.dto.FollowResponseDto;
import com.asdf.minilog.entity.Follow;
import com.asdf.minilog.entity.User;
import com.asdf.minilog.exception.UserNotFoundException;
import com.asdf.minilog.repository.FollowRepository;
import com.asdf.minilog.repository.UserRepository;
import com.asdf.minilog.util.EntityDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FollowService { // 팔로우 관계와 관련된 비즈니스 로직 처리
    // 팔로우 관계(Follow Entity)의 DB 처리를 담당
    private final FollowRepository followRepository;

    // 사용자(User Entity)의 DB 처리를 담당
    private final UserRepository userRepository;

    // 생성자 주입
    // Spring이 FollowRepository와 UserRepository 구현체를 자동으로 주입한다.
    @Autowired
    public FollowService(FollowRepository followRepository, UserRepository userRepository){
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    // 팔로우하기
    // followerId : 팔로우를 하는 사람
    // followeeId : 팔로우를 당하는 사람
    public FollowResponseDto follow(Long followerId, Long followeeId) {
        if(followerId.equals(followeeId)) {
            throw new IllegalArgumentException("자신을 팔로우 할 수 없습니다.");
        }

        // 팔로우를 하는 사용자가 실제로 DB에 존재하는지 확인
        // 존재하지 않으면 UserNotFoundException 발생
        User follower = userRepository.findById(followerId).orElseThrow(() ->
                new UserNotFoundException(
                        String.format("팔로어 아이디(%d)를 가진 사용자를 찾을수 없습니다.", followerId)
                ));

        // 팔로우 대상 사용자가 실제로 DB에 존재하는지 확인
        // 존재하지 않으면 UserNotFoundException 발생
        User followee = userRepository.findById(followeeId).orElseThrow(() ->
                new UserNotFoundException(
                        String.format("팔로잉 아이디(%d)를 가진 사용자를 찾을 수 없습니다.", followeeId)
                ));

        // follower와 followee의 ID를 이용하여 Follow Entity 생성
        //
        // EntityDtoMapper.toEntity(...)
        //      ↓
        // Follow Entity 생성
        //
        // followRepository.save(...)
        //      ↓
        // 생성한 Follow Entity를 DB에 저장
        Follow follow = followRepository.save(EntityDtoMapper.toEntity(follower.getId(), followee.getId()));

        // 저장된 Follow Entity를 응답용 DTO로 변환하여 반환
        return EntityDtoMapper.toDto(follow);
    }

    // 언팔로우하기
    // followerId와 followeeId로 기존 팔로우 관계를 찾아 삭제
    public void unfollow(Long followerId, Long followeeId) {
        // followerId와 followeeId가 모두 일치하는 Follow 관계 조회
        //
        // Repository의
        // findByFollowerIdAndFolloweeId()
        // 메서드 이름을 Spring Data JPA가 분석하여
        // 해당 조건에 맞는 조회 쿼리를 생성한다.
        //
        // 존재하지 않으면 예외 발생
        Follow follow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId).orElseThrow(
                () ->
                    new UserNotFoundException(
                            String.format(
                                    "팔로어(%d)와 팔로잉(%d)을 연결하는 Follow를 찾을 수 없습니다.", followerId, followeeId)
                    )
        );

        // 조회된 Follow Entity 삭제
        followRepository.delete(follow);
    }

    // 특정 사용자가 팔로우하고 있는 사용자 목록 조회
    // 조회 작업만 수행하므로 readOnly = true
    @Transactional(readOnly = true)
    public List<FollowResponseDto> getFollowList(Long userId) {
        // 먼저 해당 사용자가 실제로 존재하는지 확인
        if(userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(
                    String.format("해당 아이디(%d)를 가진 사용자를 찾을 수 없습니다.", userId));
        }

        // 해당 사용자가 follower인 모든 Follow Entity 조회
        //
        // findByFollowerId(userId)
        //      ↓
        // List<Follow>
        //
        // stream()
        //      ↓
        // Follow를 하나씩 처리
        //
        // map(EntityDtoMapper::toDto)
        //      ↓
        // 각각의 Follow를 FollowResponseDto로 변환
        //
        // toList()
        //      ↓
        // List<FollowResponseDto> 생성 후 반환
        return followRepository.findByFollowerId(userId).stream().map(EntityDtoMapper::toDto).toList();
    }
}
