package Byulha.project.global.auth.jwt;

import Byulha.project.global.auth.role.UserRole;
import org.springframework.security.core.Authentication;

public interface AppAuthentication extends Authentication {
    Long getUserId();

    UserRole getUserRole();

    boolean isAdmin();
}
