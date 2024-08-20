package org.hidog.board.controllers;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class RequestWalk {
    // 출발 마커 좌표
    @NotBlank
    private List<viaPoint> departurePoints;

    // 경유 마커 좌표 리스트
    @NotBlank
    private List<viaPoint> viaPoints;

    public static class viaPoint {
        private double lat;
        private double lng;
    }
}
