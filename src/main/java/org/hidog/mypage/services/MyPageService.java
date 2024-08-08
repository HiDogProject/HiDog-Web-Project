package org.hidog.mypage.services;

import lombok.RequiredArgsConstructor;
import org.hidog.member.services.MemberSaveService;
import org.hidog.mypage.controllers.RequestProfile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberSaveService saveService;

    public void update(RequestProfile form) {

    }
}
