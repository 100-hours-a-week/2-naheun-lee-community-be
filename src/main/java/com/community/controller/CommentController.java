package com.community.controller;

import com.community.dto.CommentRequestDTO;
import com.community.dto.CommentResponseDTO;
import com.community.security.JwtUtil;
import com.community.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post/{postId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final JwtUtil jwtUtil;

    // 댓글 작성
    @PostMapping
    public ResponseEntity<?> createComment(@RequestHeader("Authorization") String token,
                                           @PathVariable("postId") Long postId,
                                           @RequestBody @Valid CommentRequestDTO request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        commentService.createComment(userId, postId, request);
        return ResponseEntity.status(201).body(Map.of("message", "comment created"));
    }

    // 댓글 조회
    @GetMapping
    public ResponseEntity<?> getComments(@PathVariable("postId") Long postId) {
        List<CommentResponseDTO> comments = commentService.getComments(postId);
        return ResponseEntity.ok(Map.of("total_comments", comments.size(), "data", comments));
    }

    // 댓글 수정
    @PatchMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@RequestHeader("Authorization") String token,
                                           @PathVariable("postId") Long postId,
                                           @PathVariable("commentId") Long commentId,
                                           @RequestBody @Valid CommentRequestDTO request) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        commentService.updateComment(userId, postId, commentId, request);
        return ResponseEntity.ok(Map.of("message", "comment_updated"));
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@RequestHeader("Authorization") String token,
                                           @PathVariable("postId") Long postId,
                                           @PathVariable("commentId") Long commentId) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        commentService.deleteComment(userId, postId, commentId);
        return ResponseEntity.ok(Map.of("message", "comment_deleted"));
    }
}

