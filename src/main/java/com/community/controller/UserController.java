package com.community.controller;

import com.community.security.JwtUtil;
import com.community.dto.*;
import com.community.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 로그인 
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequestDTO request) {
        try {
            Map<String, Object> result = userService.login(request);
            return ResponseEntity.ok().body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage())); 
        }
    }

    // 회원가입 
    @Validated
    @PostMapping
    public ResponseEntity<?> signup(
        @RequestPart("data") @Valid UserSignupRequestDTO request,  
        @RequestPart(value = "profileImage") MultipartFile imageFile) { 

        String result = userService.signup(request, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 회원정보 조회 
    @GetMapping
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        Long userId =  jwtUtil.getUserIdFromToken(token); 
        UserResponseDTO userProfile = userService.getUserProfile(userId);
        return ResponseEntity.ok(Map.of("message", "profile_success", "data", userProfile));
    }

    // 회원정보 수정 
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestHeader("Authorization") String token,
                                           @RequestPart(value = "data", required = false) @Valid UserProfileUpdateDTO request,
                                           @RequestPart(value = "profileImage", required = false) MultipartFile imageFile) {
        Long userId = jwtUtil.getUserIdFromToken(token); 
        userService.updateProfile(userId, request, imageFile);
        return ResponseEntity.ok(Map.of("message", "profile_update_success"));
    }

    // 비밀번호 변경 
    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestHeader("Authorization") String token,
                                            @RequestBody @Valid UserPasswordUpdateDTO request) {
        Long userId = jwtUtil.getUserIdFromToken(token); 
        userService.updatePassword(userId, request);
        return ResponseEntity.ok(Map.of("message", "password_update_success"));
    }

    
    // 회원탈퇴 
    @DeleteMapping
    public ResponseEntity<?> deactivateUser(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token); 
        userService.deactivateUser(userId);
        return ResponseEntity.ok(Map.of("message", "member_delete_success"));
    }

}


