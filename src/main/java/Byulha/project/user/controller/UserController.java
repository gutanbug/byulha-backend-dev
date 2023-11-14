package Byulha.project.user.controller;

import Byulha.project.global.auth.role.UserAuth;
import Byulha.project.user.model.dto.request.RequestLoginDto;
import Byulha.project.user.model.dto.request.RequestReissueDto;
import Byulha.project.user.model.dto.request.RequestSignupDto;
import Byulha.project.user.model.dto.response.ResponseLoginDto;
import Byulha.project.user.model.dto.response.ResponseReissueDto;
import Byulha.project.user.model.dto.response.ResponseSignupTokenDto;
import Byulha.project.user.service.SignupService;
import Byulha.project.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "사용자", description = "사용자 관련 api")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor

public class UserController {

    private final UserService userService;
    private final SignupService signupService;

    /**
     * 회원가입 토큰 생성
     *
     * @return 회원가입 토큰
     */
    @GetMapping("/signup-token")
    public ResponseSignupTokenDto generateSignupToken() {
        return signupService.generateSignupToken();
    }

    /**
     * 회원가입
     *
     * @param dto           요청 body
     * @param signupToken   회원가입 토큰
     */
    @PostMapping("/{signup-token}")
    public void signup(@Valid @RequestBody RequestSignupDto dto,
                       @PathVariable("signup-token") String signupToken) {
        signupService.signup(dto, signupToken);
    }

    /**
     * 로그인
     *
     * @param dto           요청 body
     * @return              로그인 인증 정보
     */
    @PostMapping("/login")
    public ResponseLoginDto login(@Valid @RequestBody RequestLoginDto dto) {
        return userService.login(dto);
    }

    /**
     * 토큰 재발급
     *
     * @param dto           요청 body
     * @return              새로 발급된 토큰
     */
    @UserAuth
    @PostMapping("/reissue")
    public ResponseReissueDto reissue(@Valid @RequestBody RequestReissueDto dto) {
        return userService.reissue(dto.getRefreshToken());
    }
}
