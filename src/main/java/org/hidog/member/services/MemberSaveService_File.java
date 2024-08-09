package org.hidog.member.services;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hidog.file.services.FileUploadDoneService;
import org.hidog.member.MemberUtilFile;
import org.hidog.member.constants.Authority;
import org.hidog.member.controllers.RequestJoin;
import org.hidog.member.entities.MemberFile;
import org.hidog.member.exceptions.MemberNotFoundException;
import org.hidog.member.repositories.MemberFileRepository;
import org.hidog.mypage.controllers.RequestProfile;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class MemberSaveService_File {
    private final FileUploadDoneService uploadDoneService;
    private final MemberFileRepository memberFileRepository;
    //private final AuthoritiesRepository authoritiesRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberUtilFile memberUtilFile;
    private final HttpSession session;
    /**
     * 회원 가입 처리
     *
     * @param form
     */
    public void save(RequestJoin form) {
        MemberFile memberFile = new ModelMapper().map(form, MemberFile.class);
        String hash = passwordEncoder.encode(memberFile.getPassword());
        memberFile.setPassword(hash);
        save(memberFile, List.of(Authority.USER));
    }

    /**
     * 회원정보 수정
     * @param form
     */
    public void save(RequestProfile form) {
        MemberFile memberFile = memberUtilFile.getMemberFile();
        String email = memberFile.getEmail();
        memberFile = memberFileRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        String password = form.getPassword();
        String mobile = form.getMobile();

        if (StringUtils.hasText(password)) {
            String hash = passwordEncoder.encode(password);
            memberFile.setPassword(hash);
        }

        memberFileRepository.saveAndFlush(memberFile);

        session.setAttribute("userInfoChanged", true);
    }

    public void save(MemberFile memberFile, List<Authority> authorities) {
        memberFileRepository.saveAndFlush(memberFile);
        /*
        if (authorities != null) {
            List<Authorities> items = authoritiesRepository.findByMember(memberFile);
            authoritiesRepository.deleteAll(items);
            authoritiesRepository.flush();

            items = authorities.stream().map(authority -> Authorities.builder()
                    //.member(memberFile)
                    .authority(authority)
                    .build()).toList();
            authoritiesRepository.saveAllAndFlush(items);
        }
         */

        // 파일 업로드 완료 처리
        uploadDoneService.process(memberFile.getGid());
    }
}
