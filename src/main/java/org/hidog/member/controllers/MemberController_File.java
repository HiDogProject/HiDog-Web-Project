package org.hidog.member.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hidog.member.MemberUtilFile;
import org.hidog.member.services.MemberSaveService;
import org.hidog.member.validators.JoinValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
@SessionAttributes("requestLogin")
public class MemberController_File {

    private final JoinValidator joinValidator;
    private final MemberSaveService memberSaveService;
    private final MemberUtilFile memberUtilFile;

    @ModelAttribute
    public RequestLogin requestLogin() {
        return new RequestLogin();
    }

    @GetMapping("/join_file")
    public String join(@ModelAttribute RequestJoin form, Model model) {
        commonProcess("join_file", model);


        return "front/member/join_file";
    }

    @PostMapping("/join_file")
    public String joinPs(@Valid RequestJoin form, Errors errors, Model model) {
        commonProcess("join_file", model);

        joinValidator.validate(form, errors);

        if (errors.hasErrors()) {
            return "front/member/join_file";
        }

        memberSaveService.save(form);

        return "redirect:/member/login";
    }

    @GetMapping("/login")
    public String login(@Valid @ModelAttribute RequestLogin form, Errors errors, Model model) {
        commonProcess("login", model);

        String code = form.getCode();
        if (StringUtils.hasText(code)) {
            errors.reject(code, form.getDefaultMessage());
            //비번이 만료인 경우 비번 재설정 페이지 이동
            if (code.equals("CredentialsExpired.Login")) {
                return "redirect:/member/password/reset ";
            }
        }
        return "front/member/login";
    }

    /**
     * 회원 관련 컨트롤러 공통 처리
     *
     * @param mode
     * @param model
     */
    private void commonProcess(String mode, Model model) {
        mode = Objects.requireNonNullElse(mode, "join_file");

        List<String> addCss = new ArrayList<>();
        List<String> addCommonScript = new ArrayList<>();
        List<String> addScript = new ArrayList<>();

        addCss.add("member/style");  // 회원 공통 스타일
        if (mode.equals("join_file")) {
            addCommonScript.add("fileManager");
            addCss.add("member/join_file");
            addScript.add("member/join_file");

        } else if (mode.equals("login")) {
            addCss.add("member/login");
        }

        model.addAttribute("addCss", addCss);
        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
    }
}
