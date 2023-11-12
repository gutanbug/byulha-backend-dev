package Byulha.project.user.model.entity;

import Byulha.project.global.auth.role.UserRole;
import Byulha.project.global.base.BaseEntity;
import Byulha.project.user.model.UserStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @NotNull
    @Column(length = 20)
    private String name;

    @NotNull
    private String nickname;

    @NotNull
    private String password;

    @NotNull
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Builder
    private User(@NotNull String name,
                 @NotNull String nickname,
                 @NotNull String password,
                 @NotNull String phone,
                 UserRole userRole,
                 UserStatus status) {
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
        this.userRole = userRole;
        this.status = status;
    }

    /**
     * User 상태를 변경합니다.
     *
     * @param status 상태
     */
    public void changeStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * Dummy 회원의 값으로 변경하기 위해서 null로 만듭니다.
     */
    public void emptyOutUserInfo() {
        this.name = "";
        this.nickname = "";
        this.password = "";
        this.phone = "";
    }
}
