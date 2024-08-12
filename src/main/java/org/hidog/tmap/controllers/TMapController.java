package org.hidog.tmap.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.global.services.ApiConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller("/tmap")
@RequiredArgsConstructor
public class TMapController {
    private final ApiConfigService apiConfigService;

    @ModelAttribute("tmapJavascriptKey")
    public String tmapJavascriptKey() {
        return apiConfigService.get("tmapJavascriptKey");
    }
}
