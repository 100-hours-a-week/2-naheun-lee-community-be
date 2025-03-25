package com.community.service;

import com.community.entity.UserEntity;
import com.community.dto.*;
import com.community.repository.UserRepository;
import com.community.security.JwtUtil;
import com.community.util.NullSafeUtils;
import com.community.util.FileHandler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import com.community.exception.ConflictException;
import com.community.exception.NotFoundException;
import com.community.exception.BadRequestException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final FileHandler fileHandler;

    // 회원가입 
    @Transactional
    public String signup(UserSignupRequestDTO request, MultipartFile profileImage) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException("Nickname already exists");
        }
    
        String profileImageUrl = null;
        try {
            if (NullSafeUtils.isPresent(profileImage)) {
                profileImageUrl = fileHandler.saveFile(profileImage, "profileuploads"); 
            }
    
            UserEntity user = UserEntity.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickname(request.getNickname())
                    .profileImg(profileImageUrl)
                    .member(true)
                    .build();
            userRepository.save(user);
    
            return "register_success";
        } catch (Exception e) {
            if (profileImageUrl != null) fileHandler.deleteFile(profileImageUrl); 
            throw e;
        }
    }
    
    // 로그인 
    public String login(UserLoginRequestDTO request) {
        UserEntity user = userRepository.findActiveUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getId(), user.getEmail());
    }

    // 회원정보 조회
    public UserResponseDTO getUserProfile(Long userId) {
        UserEntity user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return UserResponseDTO.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImg(user.getProfileImg())
                .build();
    }

    // 회원정보 수정
    @Transactional 
    public void updateProfile(Long userId, UserProfileUpdateDTO request, MultipartFile profileImage) {
        UserEntity user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        String nickname = request != null ? request.getNickname() : null;
        boolean hasNickname = NullSafeUtils.hasText(nickname); 
        boolean hasImage = NullSafeUtils.isPresent(profileImage); 

        if (!hasNickname && !hasImage) {
            throw new BadRequestException("No valid fields to update");
        }

        String profileImageUrl = null;
        try {
            if (hasImage) {
                fileHandler.deleteFile(user.getProfileImg()); 
                profileImageUrl = fileHandler.saveFile(profileImage, "profileuploads");
            }

            user.updateProfile(nickname, profileImageUrl);
            userRepository.save(user);
        } catch (Exception e) {
            if (profileImageUrl != null) fileHandler.deleteFile(profileImageUrl); 
            throw e;
        }
    }

    // 비밀번호 변경 
    @Transactional
    public void updatePassword(Long userId, UserPasswordUpdateDTO request) {
        UserEntity user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.updatePassword(request.getPassword(), passwordEncoder);
        userRepository.save(user);
    }

    // 회원탈퇴 
    @Transactional
    public void deactivateUser(Long userId) {
        UserEntity user = userRepository.findActiveUserById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.deactivate();
        userRepository.save(user);
    }
}




