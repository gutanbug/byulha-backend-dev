package Byulha.project.domain.user.model.entity;

import Byulha.project.domain.user.model.Gender;
import Byulha.project.domain.user.model.UserStatus;
import Byulha.project.global.auth.role.UserRole;
import Byulha.project.global.base.BaseEntity;
import Byulha.project.infra.s3.model.ImageFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

    @NotNull
    private String age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ImageFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<ImageResult> results = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserStatus status;


    @Builder
    private User(@NotNull String name,
                 @NotNull String nickname,
                 @NotNull String password,
                 @NotNull String phone,
                 @NotNull String age,
                 Gender gender,
                 UserRole userRole,
                 UserStatus status) {
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
        this.age = age;
        this.gender = gender;
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
     * 이름을 변경합니다.
     *
     * @param name 이름
     */
    public void changeName(String name) {
        this.name = name;
    }

    /**
     * 닉네임(아이디)을 변경합니다.
     *
     * @param nickname 닉네임(아이디)
     */
    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 비밀번호를 변경합니다.
     *
     * @param encodedPassword 인코딩된 비밀번호
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * 휴대폰 번호를 변경합니다.
     *
     * @param phone 휴대폰번호
     */
    public void changePhone(String phone) {
        this.phone = phone;
    }

    /**
     * 나이를 변경합니다.
     *
     * @param age 나이
     */
    public void changeAge(String age) {
        this.age = age;
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
