package Byulha.project.user.service;

import Byulha.project.user.exception.UserNotFoundException;
import Byulha.project.user.model.entity.User;
import Byulha.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static Byulha.project.user.model.UserStatus.INACTIVE;

@Service
@RequiredArgsConstructor
public class UserWithdrawService {

    private final UserRepository userRepository;

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.changeStatus(INACTIVE);
    }
}
