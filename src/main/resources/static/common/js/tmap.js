const tmapLib = {
    departure: null, // 출발지 LatLng 객체
    arrival: null, // 도착지 latLng 객체
    via: [],   // 경유지 LatLng 객체 배열
    markers: [], // 마커
    resultDrawArr: [],
    mapId: null,
    width: '100%',
    height: '400px',
    zoom: 17,
    currentAction: null, // start, end, via
    load(mapId, width, height, zoom) {
        // 버튼 이벤트 리스너


        this.mapId = mapId;
        this.width = width;
        this.height = height;
        this.zoom = zoom;
        navigator.geolocation.getCurrentPosition((pos) => {
            const { latitude, longitude } = pos.coords;

            // 현재 위치 기반으로 지도 띄우기
            const map = new Tmapv2.Map(mapId, {
                center : new Tmapv2.LatLng(latitude, longitude),
                width : width ?? "100%",
                height : height ?? "400px",
                zoom : zoom || 17,
                zoomControl : true,
                scrollwheel : true
            });

            // 지도 클릭 이벤트
            map.addListener("click", function(e) {
                const opt = { position : e.latLng, map };

                if (tmapLib.currentAction === 'start') { // 출발지 선택
                    tmapLib.departure = e.latLng;
                    const marker = new Tmapv2.Marker(opt);
                    tmapLib.markers.push(marker);
                    tmapLib.currentAction = null;
                    console.log(e.latLng)

                } else if (tmapLib.currentAction === 'end') { // 도착지 선택
                    tmapLib.arrival = e.latLng;
                    const marker = new Tmapv2.Marker(opt);
                    tmapLib.markers.push(marker);
                    tmapLib.currentAction = null;

                } else if (tmapLib.currentAction === 'via') { // 경유지 추가
                    tmapLib.via.push(e.latLng);
                    const marker = new Tmapv2.Marker(opt);
                    tmapLib.markers.push(marker);
                    console.log(tmapLib.via)
                }
            });
        });
    },

    // 경로 그리기
    route(map) {
        const appKey = document.querySelector("meta[name='tmap_apikey']")?.content;

        if (!this.departure || !this.arrival || !appKey) {
            return;
        }

        const { ajaxLoad } = commonLib;

        const viaPoints = tmapLib.via.map((point) => ({
            viaX: point._lng,
            viaY: point._lat
        }));

        const data = {
            startX : this.departure._lng,
            startY : this.departure._lat,
            endX : this.arrival._lng,
            endY : this.arrival._lat,
            reqCoordType : "WGS84GEO",
            resCoordType : "EPSG3857",
            startName : "출발지",
            endName : "도착지",
            viaPoints
        };

        const headers = { appKey };
        const url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json&callback=result";

        (async() => {
            try {
                const response = await ajaxLoad(url, 'POST', data, headers, 'JSON');
                const resultData = response.features;
                const drawInfoArr = [];
                for (let i in resultData) {
                    const geometry = resultData[i].geometry;

                    if (geometry.type === "LineString") { // 경로 선 표기
                        for (let j in geometry.coordinates) {
                            const latLng = new Tmapv2.Point(
                                geometry.coordinates[j][0],
                                geometry.coordinates[j][1]
                            );

                            const convertPoint = new Tmapv2.Projection.convertEPSG3857ToWGS84GEO(latLng);
                            const convertChange = new Tmapv2.LatLng(
                                convertPoint._lat,
                                convertPoint._lng
                            );
                            drawInfoArr.push(convertChange);
                        }
                    }
                }

                tmapLib.drawLine(drawInfoArr, map);


            } catch (err) {
                console.error(err);
            }
        })();
    },

    drawLine(arrPoint, map) {
        const polyline_ = new Tmapv2.Polyline({
            path : arrPoint,
            strokeColor : "#DD0000", // 경로 선 색상
            strokeWeight : 6, // 두께
            map : map
        });
        this.resultDrawArr.push(polyline_);
    },

    // 초기화
    reset() {
        this.departure = this.arrival = null;
        this.via = [];
        this.markers.forEach(m => {
            try {
                m?.setMap(null);
            } catch (e) {}
        });
        this.resultDrawArr.forEach(d => {
            try {
                d?.setMap(null);
            } catch (e) {}
        });
        this.markers = this.resultDrawArr = [];
    }

};

