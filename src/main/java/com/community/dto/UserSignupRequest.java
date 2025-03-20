package com.community.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignupRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "유효한 이메일 형식을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "비밀번호는 대문자, 소문자, 숫자, 특수문자를 최소 1개 이상 포함해야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(max = 10, message = "닉네임은 최대 10자까지 입력 가능합니다.")
    @Pattern(
        regexp = "^[a-zA-Z0-9가-힣]+$",
        message = "닉네임은 공백 없이 한글, 영문, 숫자만 입력 가능합니다."
    )
    private String nickname;

    @NotBlank(message = "프로필 사진은 필수입니다.")
    private String profileImage;
}


