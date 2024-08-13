package org.hidog.mypage.repositories;

import org.hidog.mypage.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 사용자 ID로 게시글 목록 조회
    List<Post> findByUserId(Long userId);
}