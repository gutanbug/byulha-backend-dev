package Byulha.project.domain.user.service;

import Byulha.project.domain.user.exception.UserNotFoundException;
import Byulha.project.domain.user.exception.WrongPasswordException;
import Byulha.project.domain.user.model.dto.AutoLoginDto;
import Byulha.project.domain.user.model.dto.request.RequestLoginDto;
import Byulha.project.domain.user.model.dto.response.ResponseLoginDto;
import Byulha.project.domain.user.model.entity.User;
import Byulha.project.domain.user.repository.AutoLoginRepository;
import Byulha.project.domain.user.repository.UserRepository;
import Byulha.project.global.auth.jwt.AuthenticationToken;
import Byulha.project.global.auth.jwt.JwtAuthenticationToken;
import Byulha.project.global.auth.jwt.JwtProvider;
import Byulha.project.infra.s3.model.ImageFile;
import Byulha.project.infra.s3.model.ImageRequest;
import Byulha.project.infra.s3.model.UploadedImage;
import Byulha.project.infra.s3.model.dto.request.RequestUploadFileDto;
import Byulha.project.infra.s3.service.AmazonS3Service;
import Byulha.project.mock.UserMock;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AutoLoginRepository autoLoginRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private AmazonS3Service amazonS3Service;

    @Mock
    private Pageable pageable;

    @Test
    @DisplayName("로그인")
    void login() {
        // given
        User user = UserMock.createDummyUser();
        RequestLoginDto dto = new RequestLoginDto(user.getNickname(), user.getPassword());
        AuthenticationToken auth = JwtAuthenticationToken.builder()
                .accessToken("access")
                .refreshToken("refresh")
                .build();

        when(userRepository.findByNickname(user.getNickname())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.issue(user)).thenReturn(auth);

        // when
        ResponseLoginDto response = userService.login(dto);

        //then
        assertThat(response.getAccessToken()).isEqualTo("access");
        assertThat(response.getRefreshToken()).isEqualTo("refresh");
        assertThat(response.getName()).isEqualTo(user.getName());
        assertThat(response.getPhone()).isEqualTo(user.getPhone());
        assertThat(response.isAdmin()).isEqualTo(user.getUserRole().isAdmin());
    }

    @Test
    @DisplayName("로그인 실패 - 찾을 수 없는 아이디")
    void failedLoginByNotFoundId() {
        // given
        RequestLoginDto dto = new RequestLoginDto("notFound", "password");

        when(userRepository.findByNickname(dto.getNickname())).thenReturn(Optional.empty());

        // when & then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.login(dto));
    }

    @Test
    @DisplayName("로그인 실패 - 틀린 비밀번호")
    void failedLoginByWrongPassword() {
        // given
        User user = UserMock.createDummyUser();
        RequestLoginDto dto = new RequestLoginDto("nickname", "wrongPassword");

        when(userRepository.findByNickname(dto.getNickname())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

        //when & then
        assertThrows(WrongPasswordException.class,
                () -> userService.login(dto));
    }

    @Test
    @DisplayName("토큰 재발급")
    void reissue() {
        // given
        AuthenticationToken token = JwtAuthenticationToken.builder()
                .accessToken("newAccess")
                .refreshToken("refresh")
                .build();
        when(jwtProvider.getAccessTokenFromHeader(any())).thenReturn("access");
        when(jwtProvider.reissue("access", "refresh")).thenReturn(token);

        // when
        userService.reissue(null, "refresh");

        // then
        assertThat(token.getAccessToken()).isEqualTo("newAccess");
        assertThat(token.getRefreshToken()).isEqualTo("refresh");
    }

    @Test
    @DisplayName("유저 정보 조회")
    void getUserInfo() {
        // given
        User user = UserMock.createDummyUser();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // when
        userService.getUserInfo(user.getId());

        // then
        assertThat(user.getName()).isEqualTo("username");
        assertThat(user.getNickname()).isEqualTo("nickname");
        assertThat(user.getPhone()).isEqualTo("01011112222");
        assertThat(user.getAge()).isEqualTo("24");
        assertThat(user.getGender().name()).isEqualTo("MALE");
        assertThat(user.getUserRole().name()).isEqualTo("USER");
    }

    @Test
    @DisplayName("유저 정보 조회 실패 - 찾을 수 없는 아이디")
    void failedGetUserInfoByNotFoundId() {
        // given
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserInfo(userId));
    }

    @Test
    @DisplayName("이미지 업로드")
    void uploadImage() throws Exception {
    }
}