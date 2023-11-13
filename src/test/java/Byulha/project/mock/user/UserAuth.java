package Byulha.project.mock.user;

import Byulha.project.global.auth.jwt.JwtAuthentication;
import Byulha.project.global.auth.role.UserRole;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserAuth {
    public static void withUser(Long userId) {
        SecurityContextHolder.getContext()
                .setAuthentication(new JwtAuthentication(userId, UserRole.USER));
    }

    public static void withAdmin(Long userId) {
        SecurityContextHolder.getContext()
                .setAuthentication(new JwtAuthentication(userId, UserRole.ADMIN));
    }
}
