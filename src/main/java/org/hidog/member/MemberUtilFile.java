package org.hidog.member;


import lombok.RequiredArgsConstructor;
import org.hidog.member.entities.MemberFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUtilFile {
    //private final HttpSession session;
    //private final MemberInfoService infoService;

    public boolean isLogin() {
        return getMemberFile() != null;
    }

    /*
    public boolean isAdmin() {
        if (isLogin()) {
            List<Authorities> authorities = getMemberInfo().getAuthorities();
            return authorities.stream().anyMatch(s -> s.getAuthority().equals(Authority.ADMIN));
        }
        return false;
    }

     */

    public MemberFile getMemberFile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof MemberInfoFile memberInfoFile) {
            /*
            if (session.getAttribute("userInfoChanged") != null) { // 회원 정보를 변경한 경우
                memberInfo = (MemberInfo)infoService.loadUserByUsername(memberInfo.getEmail());
            }
            */
            return memberInfoFile.getMemberFile();
        }

        return null;
    }
}
