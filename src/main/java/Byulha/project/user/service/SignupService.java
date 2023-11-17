package Byulha.project.user.service;

import Byulha.project.global.auth.role.UserRole;
import Byulha.project.global.generator.SignupTokenGenerator;
import Byulha.project.user.exception.AlreadyNameException;
import Byulha.project.user.exception.AlreadyNicknameException;
import Byulha.project.user.model.UserStatus;
import Byulha.project.user.model.dto.request.RequestSignupDto;
import Byulha.project.user.model.dto.response.ResponseSignupTokenDto;
import Byulha.project.user.model.entity.User;
import Byulha.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignupService {

    private final SMSVerificationService smsVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    //TODO NicknameFilter 필요

    @Transactional
    public void signup(RequestSignupDto dto, String signupToken) {
        checkAlreadyNickname(dto.getNickname());
        checkAlreadyName(dto.getName());

        String phone = smsVerificationService.getPhoneNumber(signupToken);
        String encryptedPassword = passwordEncoder.encode(dto.getPassword());

        Optional<User> inactiveUser = userRepository.findByInactiveByNickname(dto.getNickname());

        if (inactiveUser.isPresent()) {
            User user = inactiveUser.get();
            user.changeStatus(UserStatus.ACTIVE);
            user.changeName(dto.getName());
            user.changeNickname(dto.getNickname());
            user.changePhone(phone);
            user.changeAge(dto.getAge());
            user.changePassword(encryptedPassword);
        } else{
            User user = User.builder()
                    .name(dto.getName())
                    .nickname(dto.getNickname())
                    .password(encryptedPassword)
                    .phone(phone)
                    .age(dto.getAge())
                    .sex(dto.getSex())
                    .userRole(UserRole.USER)
                    .status(UserStatus.ACTIVE)
                    .build();
            userRepository.save(user);
        }

        deleteSignupAuths(signupToken);
    }

    public ResponseSignupTokenDto generateSignupToken() {
        String signupToken = SignupTokenGenerator.generateUUIDCode();
        return new ResponseSignupTokenDto(signupToken);
    }

    private void checkAlreadyName(String name) {
        Optional<User> alreadyUser = userRepository.findByName(name);
        if (alreadyUser.isPresent()){
            throw new AlreadyNameException();
        }
    }

    private void checkAlreadyNickname(String nickname) {
        Optional<User> alreadyUser = userRepository.findByNickname(nickname);
        if (alreadyUser.isPresent()) {
            throw new AlreadyNicknameException();
        }
    }

    private void deleteSignupAuths(String signupToken) {
        if (!smsVerificationService.deleteSMSAuth(signupToken)) {
            log.error("Can't delete user signup authentication: sms auth");
        }
    }
}
