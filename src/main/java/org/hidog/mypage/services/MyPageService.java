package org.hidog.mypage.services;

import lombok.RequiredArgsConstructor;
import org.hidog.member.entities.Member;
import org.hidog.member.exceptions.MemberNotFoundException;
import org.hidog.member.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ComponentScan
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload.path}")
    private String uploadPath;

    // 회원 정보 수정 시 본인인증 -> 비밀번호
    public boolean checkPassword(String email, String rawPassword) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        return passwordEncoder.matches(rawPassword, member.getPassword());
    }

    // 프로필 이미지 저장
    @Transactional
    public void saveProfileImage(Long memberId, MultipartFile profileImage) throws IOException {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        // 파일명 생성
        String originalFilename = profileImage.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("파일 이름이 없습니다.");
        }

        String filename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = Paths.get(uploadPath, filename);

        // 디렉토리가 없을 경우 생성
        Files.createDirectories(filePath.getParent());

        // 파일 저장
        try {
            profileImage.transferTo(filePath.toFile());
        } catch (IOException e) {
            throw new IOException("이미지 저장을 실패하였습니다.", e);
        }

        // 파일명 저장
        member.setProfileImageFilename(filename);
        memberRepository.save(member);
    }
}