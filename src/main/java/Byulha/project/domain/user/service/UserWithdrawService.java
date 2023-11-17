package Byulha.project.domain.user.service;

import Byulha.project.domain.user.exception.UserNotFoundException;
import Byulha.project.domain.user.model.UserStatus;
import Byulha.project.domain.user.model.entity.User;
import Byulha.project.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserWithdrawService {

    private final UserRepository userRepository;

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.changeStatus(UserStatus.INACTIVE);
    }
}
