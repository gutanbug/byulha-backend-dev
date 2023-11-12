package Byulha.project.user.controller;

import Byulha.project.user.model.dto.request.RequestPhoneNumberDto;
import Byulha.project.user.model.dto.request.RequestVerifySMSCodeDto;
import Byulha.project.user.service.SMSVerificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequiredArgsConstructor
@Tag(name = "SMS" , description = "SMS 관련 API")
@RequestMapping("sms")
public class SMSController {

    private final SMSVerificationService smsService;

    /**
     * 사용자 인증 SMS 전송
     *
     * @param dto SMS 인증할 핸드폰 번호 DTO
     */
    @PostMapping("/{signup-token}")
    public void sendSMS(@Valid @RequestBody RequestPhoneNumberDto dto,
                        @PathVariable("signup-token") String signupToken) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        smsService.sendSMSCode(signupToken, dto.getPhoneNumber());
    }

    /**
     * 사용자 인증 SMS 검증
     *
     * @param dto SMS 검증 요청 DTO
     * @param signupToken 회원가입 토큰
     */
    @PostMapping("/verify/{signup-token}")
    public void verifySMSCode(@Valid @RequestBody RequestVerifySMSCodeDto dto,
                              @PathVariable("signup-token") String signupToken) {
        smsService.verifySMSCode(signupToken, dto.getCode());
    }
}
