package com.asdf.minilog.service;

import com.asdf.minilog.dto.UserRequestDto;
import com.asdf.minilog.dto.UserResponseDto;
import com.asdf.minilog.entity.User;
import com.asdf.minilog.exception.UserNotFoundException;
import com.asdf.minilog.repository.UserRepository;
import com.asdf.minilog.util.EntityDtoMapper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService { // 사용자 관련 비즈니스 로직
    // 사용자 DB 접근을 담당하는 Repository
    private final UserRepository userRepository;

    // 생성자 주입
    // Spring이 UserRepository 구현체를 자동으로 주입해준다.
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 전체 사용자 조회
    // 조회만 수행하므로 readOnly = true 사용
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers() {
        // 1. DB에서 모든 User Entity 조회
        // 2. 각각 UserResponseDto로 변환
        // 3. List<UserResponseDto>로 반환
        return userRepository.findAll().stream()
                .map(EntityDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    // 사용자 ID로 사용자 한 명 조회
    @Transactional(readOnly = true)
    public Optional<UserResponseDto> getUserById(Long userId) {
        // findById() 결과는 Optional<User>
        // 사용자가 존재하면 EntityDtoMapper.toDto()를 실행하여
        // Optional<UserResponseDto>로 변환
        return userRepository.findById(userId).map(EntityDtoMapper::toDto);
    }

    // 사용자 생성
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        // 같은 username을 가진 사용자가 이미 존재하는지 확인
        // findByUsername() 역시 Spring Data JPA가 메서드 이름을 분석하여
        // 조회 쿼리를 자동 생성한다.
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이릅입니다.");
        }

        // DTO로 전달받은 값을 이용하여 새로운 User Entity 생성
        User savedUser =
                userRepository.save(
                        User.builder()
                                .username(userRequestDto.getUsername())
                                .password(userRequestDto.getPassword())
                                .build());

        // 저장된 User Entity를 응답용 DTO로 변환하여 반환
        return EntityDtoMapper.toDto(savedUser);
    }

    // 사용자 정보 수정
    public UserResponseDto updatedUser(Long userId, UserRequestDto userRequestDto) {
        // 수정할 사용자가 실제로 존재하는지 조회
        // 없으면 UserNotFoundException 발생
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format(
                                                        "해당 아이디(%d)를 가진 사용자를 찾을수 없습니다.", userId)));

        // 조회한 User Entity의 값 변경
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(userRequestDto.getPassword());

        // 변경된 Entity 저장
        var updatedUser = userRepository.save(user);

        // Entity를 응답 DTO로 변환
        return EntityDtoMapper.toDto(updatedUser);
    }

    // 사용자 삭제
    public void deleteUser(Long userId) {
        // 삭제할 사용자가 실제로 존재하는지 확인
        // 존재하지 않으면 예외 발생
        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                String.format(
                                                        "해당 아이디(%d)를 가진 사용자를 찾을 수 없습니다.", userId)));

        // 조회한 사용자의 ID를 이용하여 삭제
        userRepository.deleteById(user.getId());
    }

    public UserResponseDto getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(EntityDtoMapper::toDto).orElseThrow(
                () ->
                        new UserNotFoundException(
                                String.format("해당 이름(%s)을 가진 사용자를 찾을 수 없습니다.", username)
                        )
        );
    }
}
