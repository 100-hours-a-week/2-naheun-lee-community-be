package com.community.service;

import com.community.entity.UserEntity;
import com.community.dto.*;
import com.community.repository.UserRepository;
import com.community.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.community.exception.ConflictException;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /* 회원가입 */
    public String signup(UserSignupRequest request, MultipartFile profileImage) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException("Nickname already exists");
        }
    
        String profileImageUrl = null;
        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                profileImageUrl = saveProfileImage(profileImage);
            }
    
            UserEntity user = UserEntity.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .nickname(request.getNickname())
                    .profileImage(profileImageUrl)
                    .member(true)
                    .build();
            userRepository.save(user);
    
            return "register_success";
        } catch (Exception e) {
            if (profileImageUrl != null) {
                deleteProfileImage(profileImageUrl);
            }
            throw e;
        }
    }
    

    /* 로그인 */
    public String login(UserLoginRequest request) {
        UserEntity user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(request.getEmail());
    }

    /* 회원정보 조회 */
    public UserResponseDTO getUserProfile(Long userId) {
        UserEntity user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponseDTO.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .build();
    }

    /* 회원정보 수정 */
    public void updateProfile(Long userId, UserProfileRequest request) {
        UserEntity user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getNickname() != null) user.setNickname(request.getNickname());

        if (request.getProfileImage() != null) {
            user.setProfileImage(request.getProfileImage()); // 업로드된 이미지 URL 적용
        }

        userRepository.save(user);
    }

    /* 비밀번호 변경 */
    public void updatePassword(Long userId, UserPasswordRequest request) {
        UserEntity user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    /* 회원탈퇴 */
    public void deactivateUser(Long userId) {
        UserEntity user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.deactivate();
        userRepository.save(user);
    }

    /* 파일 저장 로직 */
    public String saveProfileImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null; 
        }
    
        try {
            String uploadDir = System.getProperty("user.dir") + "/uploads/"; 
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); 
            }
    
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String savedFilePath = uploadDir + fileName;
    
            File destinationFile = new File(savedFilePath);
            file.transferTo(destinationFile);
    
            return savedFilePath; 
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }

    /* 파일 삭제 로직 */
    private void deleteProfileImage(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}




