package org.hidog.map.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/map")
@RequiredArgsConstructor
public class MapController {

    @GetMapping("/main")
    public String join(Model model) {
        model.addAttribute("addCommonScript", List.of("tMap"));
        model.addAttribute("addScript", List.of("map/mainMap"));
        model.addAttribute("addCommonCss", List.of("tMap"));
        return "front/map/tMap";
    }
}
