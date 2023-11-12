package Byulha.project.user.service;

import Byulha.project.global.auth.jwt.AuthenticationToken;
import Byulha.project.global.auth.jwt.JwtProvider;
import Byulha.project.user.exception.UserNotFoundException;
import Byulha.project.user.exception.WrongPasswordException;
import Byulha.project.user.model.dto.AutoLoginDto;
import Byulha.project.user.model.dto.request.RequestLoginDto;
import Byulha.project.user.model.dto.response.ResponseLoginDto;
import Byulha.project.user.model.entity.User;
import Byulha.project.user.repository.AutoLoginRepository;
import Byulha.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    public static final String AUTO_LOGIN_NAME = "auto-login";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AutoLoginRepository autoLoginRepository;

    public ResponseLoginDto login(RequestLoginDto dto) {
        Instant now = Instant.now();
        User user = userRepository.findByNickname(dto.getNickname())
                .orElseThrow(UserNotFoundException::new);

        if (passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            AuthenticationToken token = jwtProvider.issue(user);
            autoLoginRepository.setAutoLoginPayload(token.getRefreshToken(), AUTO_LOGIN_NAME,
                    new AutoLoginDto(user.getId().toString(), user.getUserRole()), now);
            return new ResponseLoginDto(token, user);
        } else {
            throw new WrongPasswordException();
        }
    }

//    public ResponseReissueDto reissue(String refreshToken) {
//        Instant now = Instant.now();
//        AutoLoginDto autoLoginObj = autoLoginRepository.getAutoLoginPayload(refreshToken, AUTO_LOGIN_NAME, AutoLoginDto.class, now)
//                .orElseThrow(AutoLoginUserNotFoundException::new);
//        AuthenticationToken token = jwtProvider.reissue(autoLoginObj.getUserId(), autoLoginObj.getUserRole());
//        return new ResponseReissueDto(token.getAccessToken());
//    }
}
