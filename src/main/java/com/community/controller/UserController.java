package com.community.controller;

import com.community.security.JwtUtil;
import com.community.dto.*;
import com.community.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.community.exception.UnauthorizedException;
import com.community.exception.BadRequestException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 회원가입 
    @Validated
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
        @RequestPart("data") @Valid UserSignupRequest request,  
        @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) { 

        String result = userService.signup(request, profileImage);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 로그인 
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest request) {
        try {
            String token = userService.login(request);
            return ResponseEntity.ok().body(Map.of("token", token)); 
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage())); 
        }
    }

    // 로그아웃 (프론트에서 토큰 삭제) 
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid token");
        }
        return ResponseEntity.ok(Map.of("message","logout_success"));
    }

    // 회원정보 조회 
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        Long userId =  getCurrentUserId();

        UserResponseDTO userProfile = userService.getUserProfile(userId); 

        Map<String, Object> response = new HashMap<>();
        response.put("message", "profile_success");
        response.put("data", userProfile);

        return ResponseEntity.ok(response);
    }

    // 회원정보 수정 
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token,
                                           @RequestPart(value = "nickname", required = false) @Valid UserProfileRequest request,
                                           @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        Long userId = getCurrentUserId();

        if (request == null) {
            request = new UserProfileRequest();
        }

        // 프로필 이미지 저장 (파일을 업로드한 경우에만 저장)
        if (request.getNickname() != null || (profileImage != null && !profileImage.isEmpty())) {
            userService.updateProfile(userId, request, profileImage);
            return ResponseEntity.ok(Map.of("message", "profile_update_success"));
        }
    
        throw new BadRequestException("No valid fields to update");
    }

    // 비밀번호 변경 
    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token,
                                            @RequestBody @Valid UserPasswordRequest request) {
        Long userId = getCurrentUserId();
        userService.updatePassword(userId, request);
        return ResponseEntity.ok(Map.of("message", "password_update_success"));
    }

    
    // 회원탈퇴 
    @DeleteMapping("")
    public ResponseEntity<?> deactivateUser(@RequestHeader("Authorization") String token) {
        Long userId = getCurrentUserId();
        userService.deactivateUser(userId);
        return ResponseEntity.ok(Map.of("message", "member_delete_success"));
    }

    // SecurityContextHolder에서 현재 로그인한 유저의 ID 가져오기기
    private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
    if (authentication == null || !authentication.isAuthenticated()) {
        throw new UnauthorizedException("User not authenticated"); 
    }

    Object principal = authentication.getPrincipal();
    
    if (principal instanceof UserDetails) {
        try {
            return Long.parseLong(((UserDetails) principal).getUsername()); 
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid user ID format"); 
        }
    }
    
    throw new UnauthorizedException("User not authenticated"); 
    }
}


