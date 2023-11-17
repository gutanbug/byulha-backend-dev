package Byulha.project.domain.user.model.dto;

import Byulha.project.global.auth.role.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AutoLoginDto {

    private final String userId;
    private final UserRole userRole;
}
