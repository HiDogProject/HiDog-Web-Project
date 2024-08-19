package org.hidog.member;

import lombok.RequiredArgsConstructor;
import org.hidog.member.constants.Authority;
import org.hidog.member.entities.Authorities;
import org.hidog.member.entities.Member;
import org.hidog.member.repositories.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MemberUtil {
   //  private final HttpSession session;
   //  private final MemberInfoService infoService;
    private final MemberRepository memberRepository;
    @Value("${file.upload.url}")
    private String fileUploadUrl;

    public boolean isLogin() {
        return getMember() != null;
    }

    public boolean isAdmin() {
        if (isLogin()) {
            List<Authorities> authorities = getMember().getAuthorities();
            return authorities.stream().anyMatch(s -> s.getAuthority().equals(Authority.ADMIN));
        }
        return false;
    }

    public Member getMember() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof MemberInfo memberInfo) {

            /*
            if (session.getAttribute("userInfoChanged") != null) { // 회원 정보를 변경한 경우
                memberInfo = (MemberInfo)infoService.loadUserByUsername(memberInfo.getEmail());
            }
            */
            return memberInfo.getMember();
        }

        return null;
    }

    // 프로필 이미지 저장
    public String saveProfileImage(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("파일 이름이 null입니다.");
        }

        String fileExtension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;

        File targetFile = new File(Paths.get(fileUploadUrl, fileName).toString());
        image.transferTo(targetFile);

        return "/images/" + fileName;
    }

    // 파일 확장자 추출
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(dotIndex + 1);
    }

    // 데이터베이스에 프로필 이미지 URL 업데이트
    public void updateProfileImageUrl(Long memberId, String newImageUrl) {
        memberRepository.updateProfileImageUrl(memberId, newImageUrl);
    }

    // 현재 프로필 이미지 URL 가져옴
    public String getProfileImageUrl() {
        return "/images/default-profile.png";
    }
}