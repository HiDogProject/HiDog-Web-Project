package org.hidog.member;

import lombok.RequiredArgsConstructor;
import org.hidog.member.constants.Authority;
import org.hidog.member.entities.Authorities;
import org.hidog.member.entities.Member;
import org.hidog.member.repositories.MemberRepository;
import org.hidog.member.services.MemberInfoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final MemberRepository repository;
    private final MemberInfoService infoService;

    public boolean isLogin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof MemberInfo;
    } // 오류 발생하므로 코드 수정

    public boolean isAdmin() {
        if (isLogin()) {
            List<Authorities> authorities = getMember().getAuthorities();
            return authorities.stream().anyMatch(s -> s.getAuthority().equals(Authority.ADMIN));
        }
        return false;
    }

    public Member getMember() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Member member = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof MemberInfo memberInfo) {

            member = memberInfo.getMember();
            if(member == null){
                member = repository.findByEmail(memberInfo.getEmail()).orElse(null);

                infoService.addInfo(member);

                memberInfo.setMember(member);
            }
        }
        return member;
    }
}