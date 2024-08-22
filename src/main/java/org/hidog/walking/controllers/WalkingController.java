package org.hidog.walking.controllers;

import lombok.RequiredArgsConstructor;
import org.hidog.global.Utils;
import org.hidog.global.services.ApiConfigService;
import org.hidog.walking.services.MainMapMarkerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

        List<Double> startMarker = mainMapMarkerService.startMarkerLocation(); // 출발 좌표
//        List<Double> viaMarker = mainMapMarkerService.startMarkerLocation(1); // 경유 좌표


        addScript.add("walking/mainMapMark");
        addScript.add("walking/mainMap");

        model.addAttribute("addScript", addScript);
        model.addAttribute("startMarker", startMarker);
//        model.addAttribute("viaMarker", viaMarker);
        model.addAttribute("addCommonCss", List.of("map"));

        return utils.tpl("walking/map");
    }

    @PostMapping("/map")
    public String postMainMap(@RequestBody Map<String, List<Map<String, Double>>> data, Model model) {
        // Ajax로 선택한 마커 "clickDeparturePoint" 데이터 받아옴
        List<Map<String, Double>> clickDeparturePoint = data.get("clickDeparturePoint");

        String viaPoints = mainMapMarkerService.viaMarkerLocation(clickDeparturePoint);
        model.addAttribute("viaPoints", viaPoints);

        return utils.tpl("walking/map");
    }
}
