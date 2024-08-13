package org.hidog.mypage.services;

import lombok.RequiredArgsConstructor;
import org.hidog.member.entities.Member;
import org.hidog.member.exceptions.MemberNotFoundException;
import org.hidog.member.repositories.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 정보 수정 시 본인인증 -> 비밀번호
    public boolean checkPassword(String email, String rawPassword) {
        Member member = memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        return passwordEncoder.matches(rawPassword, member.getPassword());
    }
}