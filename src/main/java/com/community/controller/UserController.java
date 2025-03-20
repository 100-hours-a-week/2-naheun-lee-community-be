package com.community.controller;

import com.community.security.JwtUtil;
import com.community.dto.*;
import com.community.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /* 회원가입 */
    @PostMapping(value = "/signup", consumes = "multipart/form-data")
        public ResponseEntity<?> signup(
                @RequestParam("email") String email,
                @RequestParam("password") String password,
                @RequestParam("nickname") String nickname,
                @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

            String profileImageUrl = (profileImage != null && !profileImage.isEmpty()) 
                    ? userService.saveProfileImage(profileImage) 
                    : null;

            UserSignupRequest request = UserSignupRequest.builder()
                    .email(email)
                    .password(password)
                    .nickname(nickname)
                    .profileImage(profileImageUrl)
                    .build();

            return ResponseEntity.ok(userService.signup(request));
        }

    /* 로그아웃 (프론트에서 토큰 삭제) */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        return ResponseEntity.ok("Logout successful");
    }

    /* 회원탈퇴 */
    @DeleteMapping("")
    public ResponseEntity<?> deactivateUser(@RequestHeader("Authorization") String token) {
        Long userId = Long.parseLong(jwtUtil.validateToken(token.replace("Bearer ", "")));
        userService.deactivateUser(userId);
        return ResponseEntity.ok("Account deactivated successfully");
    }

    /* 회원정보 조회 */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        Long userId = Long.parseLong(jwtUtil.validateToken(token.replace("Bearer ", "")));
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    /* 회원정보 수정 */
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token,
                                           @RequestPart("data") UserProfileRequest request,
                                           @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        Long userId = Long.parseLong(jwtUtil.validateToken(token.replace("Bearer ", "")));

        // 프로필 이미지 저장 (파일을 업로드한 경우에만 저장)
        if (profileImage != null && !profileImage.isEmpty()) {
            String profileImageUrl = userService.saveProfileImage(profileImage);
            request.setProfileImage(profileImageUrl);
        }

        userService.updateProfile(userId, request);
        return ResponseEntity.ok("Profile updated successfully");
    }

    /* 비밀번호 변경 */
    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token,
                                            @RequestBody @Valid UserPasswordRequest request) {
        Long userId = Long.parseLong(jwtUtil.validateToken(token.replace("Bearer ", "")));
        userService.updatePassword(userId, request);
        return ResponseEntity.ok("Password updated successfully");
    }
}



