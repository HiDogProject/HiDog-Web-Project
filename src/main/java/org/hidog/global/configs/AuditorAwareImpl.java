package org.hidog.global.configs;

import lombok.RequiredArgsConstructor;

import org.hidog.member.MemberUtil;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<String> {

    private final MemberUtil memberUtil;

    @Override
    public Optional<String> getCurrentAuditor() {

        String email = null;
        if (memberUtil.isLogin()) {
            email = SecurityContextHolder.getContext().getAuthentication().getName();

        }

        //String email = memberUtil.isLogin() ? memberUtil.getMember().getEmail() : null;
        return Optional.ofNullable(email);
    }
}