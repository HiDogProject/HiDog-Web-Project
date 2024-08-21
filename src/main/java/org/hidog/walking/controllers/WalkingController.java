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
        List<Double> startMarker = mainMapMarkerService.startMarkerLocation();
        model.addAttribute("startMarker", startMarker);
        model.addAttribute("addCommonCss", List.of("map"));
//        model.addAttribute("addCommonScript", List.of("map"));
        model.addAttribute("addScript", List.of("mainMap/mainMap"));


        return utils.tpl("walking/map");
    }
}
//[37.560154248445315, 126.94330930709881, 37.557251187359554, 126.9434273242955, 37.55736175523448, 126.94284796714825, 37.55748082814708, 126.94234371185348]