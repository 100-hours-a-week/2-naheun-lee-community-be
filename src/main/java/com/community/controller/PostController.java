package com.community.controller;

import com.community.dto.PostRequestDTO;
import com.community.dto.PostResponseDTO;
import com.community.dto.PostUpdateDTO;
import com.community.security.JwtUtil;
import com.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final JwtUtil jwtUtil;

    // 게시글 작성
    @PostMapping
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String token,
                                        @RequestPart("data") @Valid PostRequestDTO request,
                                        @RequestPart(value = "postImage", required = false) MultipartFile imageFile) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        postService.createPost(userId, request, imageFile);
        return ResponseEntity.status(201).body(Map.of("message", "post_created"));
    }

    // 게시글 리스트 조회
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        List<PostResponseDTO> response = postService.getAllPosts();
        return ResponseEntity.ok(Map.of("message", "posts_fetched", "data", response));
    }


    // 게시글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@RequestHeader("Authorization") String token,
                                         @PathVariable("postId") Long postId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        PostResponseDTO response = postService.getPostDTOById(postId, userId);
        return ResponseEntity.ok(Map.of("message", "post_fetched", "data", response));
    }

    // 조회수 증가
    @PatchMapping("/{postId}/views")
    public ResponseEntity<?> incrementViews(@PathVariable("postId") Long postId) {
        postService.incrementViews(postId);
        return ResponseEntity.ok(Map.of("message", "views_updated"));
    }

    // 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<?> updatePost(@RequestHeader("Authorization") String token,
                                        @PathVariable("postId") Long postId,
                                        @RequestPart(value = "data", required = false) @Valid PostUpdateDTO request,
                                        @RequestPart(value = "postImage", required = false) MultipartFile imageFile) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        postService.updatePost(postId, userId, request, imageFile);
        return ResponseEntity.ok(Map.of("message", "post_updated"));
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@RequestHeader("Authorization") String token,
                                        @PathVariable("postId") Long postId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        postService.deletePost(postId, userId);
        return ResponseEntity.ok(Map.of("message", "post_deleted"));
    }
}

