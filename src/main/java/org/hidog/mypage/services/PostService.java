package org.hidog.mypage.services;

import org.hidog.mypage.entities.Post;
import org.hidog.mypage.repositories.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    // 생성자 주입
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // 사용자 ID로 게시글 목록 조회
    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByUserId(userId);
    }
}