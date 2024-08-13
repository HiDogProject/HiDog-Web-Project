package org.hidog.tmap.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.global.Utils;
import org.hidog.global.services.ApiConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tmap")
@RequiredArgsConstructor
public class TMapController {

    private final ApiConfigService apiConfigService;
    private final Utils utils;

    @ModelAttribute("tmapJavascriptKey")
    public String tmapJavascriptKey() {
        return apiConfigService.get("tmapJavascriptKey");
    }

    @GetMapping("/test")
    public String tmapTest(Model model) {
        model.addAttribute("addCommonCss", List.of("tmap"));
        model.addAttribute("addCommonScript", List.of("tmap"));
        model.addAttribute("addScript", List.of("tmap/test"));

        return utils.tpl("tmap/test");
    }
}
