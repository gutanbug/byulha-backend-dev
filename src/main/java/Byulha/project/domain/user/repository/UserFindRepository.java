package Byulha.project.domain.user.repository;

import Byulha.project.domain.user.model.SMSAuth;

import java.time.Instant;
import java.util.Optional;

public interface UserFindRepository {

    void setAuthCode(String token, String code, String phone, Instant now);

    Optional<SMSAuth> getAuthCode(String token, Instant now);

    void deleteAuthCode(String token);
}
