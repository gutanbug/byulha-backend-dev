package Byulha.project.user.service;

import Byulha.project.global.generator.CodeGenerator;
import Byulha.project.infra.naver.sms.model.dto.MessageDto;
import Byulha.project.infra.naver.sms.service.NaverSMSService;
import Byulha.project.user.exception.AlreadyPhoneException;
import Byulha.project.user.exception.NotSMSAuthorizedException;
import Byulha.project.user.exception.NotSMSSentException;
import Byulha.project.user.exception.WrongSMSCodeException;
import Byulha.project.user.model.SMSAuth;
import Byulha.project.user.model.entity.User;
import Byulha.project.user.repository.SignupAuthRepository;
import Byulha.project.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

import static lombok.AccessLevel.PROTECTED;

@Service
@RequiredArgsConstructor(access = PROTECTED)
public class SMSVerificationService {

    public static final String SMS_AUTH_NAME = "sms";
    public static final String SMS_AUTH_COMPLETE_SIGN = "OK";

    private final Clock clock;
    private final MessageSource messageSource;
    private final NaverSMSService smsService;
    private final UserRepository userRepository;
    private final SignupAuthRepository smsAuthRepository;

    @Value("${app.auth.sms.digit-count}")
    private int digitCount;

    /**
     * 회원가입 토큰을 사용하여 인증된 휴대폰 정보를 가져옵니다.
     * 회원가입 진행자를 대상으로 사용합니다.
     *
     * @param signupToken 회원가입 토큰
     * @throws NotSMSAuthorizedException 휴대폰 인증을 하지 않았을 경우
     */
    public String getPhoneNumber(String signupToken) throws NotSMSAuthorizedException {
        Instant now = Instant.now(clock);
        SMSAuth authObj = smsAuthRepository.getAuthPayload(signupToken, SMS_AUTH_NAME, SMSAuth.class, now)
                .orElseThrow(NotSMSSentException::new);

        if (!authObj.getCode().equals(SMS_AUTH_COMPLETE_SIGN)) {
            throw new WrongSMSCodeException();
        }

        return authObj.getPhone();
    }

    /**
     * 회원가입 토큰을 사용하여 가입이 완료된 후에는 휴대폰 정보를 삭제해야 합니다.
     * 중복 가입을 막기 위해 사용됩니다.
     *
     * @param signupToken 회원가입 토큰
     */
    public boolean deleteSMSAuth(String signupToken) {
        return smsAuthRepository.deleteAuthPayload(signupToken, SMS_AUTH_NAME);
    }

    /**
     * 회원가입 시 해당 전화번호로 SMS인증 메시지를 전송합니다.
     *
     */
    @Transactional(readOnly = true)
    public void sendSMSCode(String signupToken, String phoneNumber) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        checkAlreadyPhone(phoneNumber);

        String code = CodeGenerator.generateDigitCode(digitCount);
        phoneNumber = phoneNumber.trim().replaceAll("-", "");

        Instant now = Instant.now(clock);
        smsAuthRepository.setAuthPayload(signupToken, SMS_AUTH_NAME, new SMSAuth(phoneNumber, code), now);

        Locale locale = LocaleContextHolder.getLocale();
        MessageDto messageDto = MessageDto.builder()
                .to(phoneNumber)
                .content(messageSource.getMessage("sms.auth.message", new Object[]{code}, locale))
                .build();
        smsService.sendSMS(messageDto);
    }

    private void checkAlreadyPhone(String phoneNumber) {
        Optional<User> alreadyUser = userRepository.findByPhone(phoneNumber);
        if (alreadyUser.isPresent()) {
            throw new AlreadyPhoneException();
        }
    }

    public void verifySMSCode(String signupToken, String code) {
        Instant now = Instant.now(clock);
        SMSAuth authObj = smsAuthRepository.getAuthPayload(signupToken, SMS_AUTH_NAME, SMSAuth.class, now)
                .orElseThrow(NotSMSSentException::new);

        if (!authObj.getCode().equals(code.trim())) {
            throw new WrongSMSCodeException();
        }

        SMSAuth newAuthObj = new SMSAuth(authObj.getPhone(), SMS_AUTH_COMPLETE_SIGN);
        smsAuthRepository.setAuthPayload(signupToken, SMS_AUTH_NAME, newAuthObj, now);
    }

}
