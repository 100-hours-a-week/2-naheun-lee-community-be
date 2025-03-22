package com.community.repository;

import com.community.entity.LikeEntity;
import com.community.entity.PostEntity;
import com.community.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인
    boolean existsByUserAndPost(UserEntity user, PostEntity post);

    Optional<LikeEntity> findByUserAndPost(UserEntity user, PostEntity post);

    int countByPost(PostEntity post);
}

