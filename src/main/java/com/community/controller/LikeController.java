package com.community.controller;

import com.community.security.JwtUtil;
import com.community.service.LikeService;
import com.community.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/post/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;
    private final JwtUtil jwtUtil;

    // 좋아요 추가
    @PostMapping
    public ResponseEntity<?> likePost(@RequestHeader("Authorization") String token,
                                      @PathVariable("postId") Long postId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) throw new UnauthorizedException("Unauthorized");
        likeService.likePost(userId, postId);
        return ResponseEntity.ok(Map.of("message", "like_updated"));
    }

    // 좋아요 취소
    @DeleteMapping
    public ResponseEntity<?> unlikePost(@RequestHeader("Authorization") String token,
                                        @PathVariable("postId") Long postId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) throw new UnauthorizedException("Unauthorized");
        likeService.unlikePost(userId, postId);
        return ResponseEntity.ok(Map.of("message", "like_deleted"));
    }
}

