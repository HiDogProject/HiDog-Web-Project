package org.hidog.walking.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.global.Utils;
import org.hidog.global.services.ApiConfigService;
import org.hidog.walking.services.MainMapMarkerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/walking")
@RequiredArgsConstructor
public class WalkingController {

    private final ApiConfigService apiConfigService;
    private final Utils utils;
    private final MainMapMarkerService mainMapMarkerService;

    @ModelAttribute("tmapJavascriptKey")
    public String tmapJavascriptKey() {
        return apiConfigService.get("tmapJavascriptKey");
    }

    @GetMapping("/map")
    public String getMainMap(Model model) {
        List<String> addScript = new ArrayList<>();

        List<Double> startMarker = mainMapMarkerService.startMarkerLocation();

        addScript.add("walking/mainMapMark");
        addScript.add("walking/mainMap");

        model.addAttribute("addScript", addScript);
        model.addAttribute("startMarker", startMarker);
        model.addAttribute("addCommonCss", List.of("map"));

        return utils.tpl("walking/map");
    }
}
