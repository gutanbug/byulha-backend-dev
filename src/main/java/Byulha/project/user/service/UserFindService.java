package Byulha.project.user.service;

import Byulha.project.naver.sms.model.dto.MessageDto;
import Byulha.project.naver.sms.service.NaverSMSService;
import Byulha.project.user.exception.UserNotFoundException;
import Byulha.project.user.model.entity.User;
import Byulha.project.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserFindService {

    private final UserRepository userRepository;
    private final NaverSMSService naverSMSService;
    private final MessageSource messageSource;

    @Transactional(readOnly = true)
    public void sendNicknameBySMS(String phone) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        phone = eliminateDash(phone);
        User user = userRepository.findByPhone(phone).orElseThrow(UserNotFoundException::new);
        String username = user.getNickname();
        sendSMS(phone, "sms.find.id-message", username);
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
}
