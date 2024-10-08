package org.hidog.global.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hidog.global.Utils;
import org.hidog.member.MemberUtil;
import org.hidog.member.entities.Member;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class CommonInterceptor implements HandlerInterceptor {

    private final Utils utils;
    private final MemberUtil memberUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        checkDevice(request);

        request.setAttribute("utils", utils);

        return true;
    }

    /**
     * PC와 Mobile 수동 변환
     *
     * ?device=MOBILE
     * ?device=PC
     * @param request
     */
    private void checkDevice(HttpServletRequest request) {
        String device = request.getParameter("device");
        if (!StringUtils.hasText(device)) {
            return;
        }

        device = device.toUpperCase().equals("MOBILE") ? "MOBILE" : "PC";

        HttpSession session = request.getSession();
        session.setAttribute("device", device);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv) throws Exception {

        request.setAttribute("loggedMember", memberUtil.getMember());
        request.setAttribute("isLogin", memberUtil.isLogin());
        request.setAttribute("isAdmin", memberUtil.isAdmin());

        if (memberUtil.isLogin()) {
            Member member = memberUtil.getMember();
            request.setAttribute("myProfileImage", member.getProfileImage());
        }
    }
}