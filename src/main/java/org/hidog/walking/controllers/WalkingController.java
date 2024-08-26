package org.hidog.walking.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
        List<String> addCommonScript = new ArrayList<>();

        List<Double> startMarker = mainMapMarkerService.startMarkerLocation(); // 출발 좌표

        model.addAttribute("startMarker", startMarker);

        addScript.add("walking/mainMapMark");
        addScript.add("walking/mainMap");

        addCommonScript.add("map");

        model.addAttribute("addCommonScript", addCommonScript);
        model.addAttribute("addScript", addScript);
        model.addAttribute("addCommonCss", List.of("map"));
        model.addAttribute("addCss", List.of("walking/style"));

        return utils.tpl("walking/map");
    }

    @ResponseBody
    @PostMapping("/map")
    public Map<String, Object> postMainMap(@RequestBody Map<String, List<Map<String, String>>> data) throws JsonProcessingException {
        // Ajax로 선택한 마커 "clickDeparturePoint" 데이터 받아옴
        List<Map<String, String>> clickDeparturePoint = data.get("clickDeparturePoint");

        Map<String, Object>  boardData = mainMapMarkerService.viaMarkerLocation(clickDeparturePoint);

        return boardData;
    }
}
