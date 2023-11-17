package Byulha.project.domain.user.service;

import Byulha.project.domain.user.exception.NotSMSSentException;
import Byulha.project.domain.user.exception.UserNotFoundException;
import Byulha.project.domain.user.exception.WrongSMSCodeException;
import Byulha.project.domain.user.model.SMSAuth;
import Byulha.project.domain.user.model.dto.response.ResponseChangeTokenDto;
import Byulha.project.domain.user.repository.UserFindRepository;
import Byulha.project.global.generator.CodeGenerator;
import Byulha.project.global.generator.SignupTokenGenerator;
import Byulha.project.infra.naver.sms.model.dto.MessageDto;
import Byulha.project.infra.naver.sms.service.NaverSMSService;
import Byulha.project.domain.user.model.entity.User;
import Byulha.project.domain.user.repository.UserRepository;
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
@Service
@RequiredArgsConstructor
public class UserFindService {

    public static final String CODE_AUTH_COMPLETED = "OK";

    private final Clock clock;
    private final UserRepository userRepository;
    private final UserFindRepository userFindRepository;
    private final NaverSMSService naverSMSService;
    private final MessageSource messageSource;

    @Value("${app.auth.sms.digit-count}")
    private int digitCount;

    /**
     * SMS로 닉네임을 전송합니다.
     *
     * @param phone 전화번호
     */
    @Transactional(readOnly = true)
    public void sendNicknameBySMS(String phone) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        phone = eliminateDash(phone);
        User user = userRepository.findByPhone(phone).orElseThrow(UserNotFoundException::new);
        String username = user.getNickname();
        sendSMS(phone, "sms.find.nickname-message", username);
    }

    private void sendSMS(String phone, String messageCode, String argument) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        Locale locale = LocaleContextHolder.getLocale();
        MessageDto messageDto = MessageDto.builder()
                .to(phone)
                .content(messageSource.getMessage(messageCode, new Object[]{argument}, locale))
                .build();
        naverSMSService.sendSMS(messageDto);
    }

    private String eliminateDash(String phone) {
        return phone.replaceAll("-", "");
    }

    @Transactional
    public ResponseChangeTokenDto sendChangePhoneCodeBySMS(Long userId, String phone) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        Instant now = Instant.now(clock);
        String code = CodeGenerator.generateDigitCode(digitCount);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        phone = eliminateDash(phone);

        String token = SignupTokenGenerator.generateUUIDCode();
        userFindRepository.setAuthCode(token, code, phone, now);
        sendSMS(phone, "sms.auth.message", code);

        return new ResponseChangeTokenDto(token);
    }

    @Transactional
    public void changePhoneNumber(Long userId, String token, String code) {
        Instant now = Instant.now(clock);
        SMSAuth auth = userFindRepository.getAuthCode(token, now)
                .orElseThrow(NotSMSSentException::new);

        if(!auth.getCode().equals(code)){
            throw new WrongSMSCodeException();
        }
        String phone = eliminateDash(auth.getPhone());
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new).changePhone(phone);
        userFindRepository.deleteAuthCode(token);
    }
}
