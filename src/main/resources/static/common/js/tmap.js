const tmapLib = {
    departure: null, // 출발지 LatLng 객체
    arrival: null, // 도착지 LatLng 객체
    via: [], // 경유지 LatLng 객체 배열
    markers: [], // 마커
    resultDrawArr: [],
    map: null, // 지도 객체
    width: '100%',
    height: '400px',
    zoom: 17,
    currentAction: null, // start, end, via

    // 지도 로딩 및 초기화
    load(mapId, width, height, zoom) {
        this.width = width ?? '100%';
        this.height = height ?? '400px';
        this.zoom = zoom || 17;

        navigator.geolocation.getCurrentPosition((pos) => {
            const { latitude, longitude } = pos.coords;

            // 현재 위치 기반으로 지도 띄우기
            this.map = new Tmapv2.Map(mapId, {
                center: new Tmapv2.LatLng(latitude, longitude),
                width: this.width,
                height: this.height,
                zoom: this.zoom,
                zoomControl: true,
                scrollwheel: true
            });

            // 지도 클릭 이벤트
            this.map.addListener('click', (e) => {
                const opt = { position: e.latLng, map: this.map, icon : markerImg,};

                if (this.currentAction === 'start') { // 출발지 선택
                    if (this.departure != null) {
                        alert("출발지는 1개까지 입력이 가능합니다.");
                        return;
                    }
                    this.departure = e.latLng;
                    const marker = new Tmapv2.Marker(opt);
                    this.markers.push(marker);
                    this.currentAction = null;
                    console.log('출발지:', e.latLng);

                } else if (this.currentAction === 'end') { // 도착지 선택
                    if (this.arrival != null) {
                        alert("도착지는 1개까지 입력이 가능합니다.");
                        return;
                    }
                    this.arrival = e.latLng;
                    const marker = new Tmapv2.Marker(opt);
                    this.markers.push(marker);
                    this.currentAction = null;
                    console.log('도착지:', e.latLng);

                } else if (this.currentAction === 'via') { // 경유지 추가
                    if (this.via.length > 5) {
                        alert("경유지는 최대 5개까지 선택할 수 있습니다.");
                        return;
                    }
                    this.via.push(e.latLng);
                    const marker = new Tmapv2.Marker(opt);
                    this.markers.push(marker);
                    this.currentAction = null;
                    console.log('경유지 추가:', e.latLng);
                    this.currentAction = 'via'
                }
            });
        });
    },

    // 경로 그리기
    async route() {
        const appKey = document.querySelector("meta[name='tmap_apikey']")?.content;

        if (!this.departure || !this.arrival || !appKey) {
            return;
        }
        const { ajaxLoad } = commonLib;

// 경유지 좌표를 passList 형식으로 변환
        const passList = this.via.map(point => `${point.lng()},${point.lat()}`).join('_');


        console.log("passList:", passList)

        const data = {
            startX: this.departure.lng(),
            startY: this.departure.lat(),
            endX: this.arrival.lng(),
            endY: this.arrival.lat(),
            passList: passList,
            reqCoordType: 'WGS84GEO',
            resCoordType: 'EPSG3857',
            startName: '출발지',
            endName: '도착지',
        };

        const headers = { appKey };
        const url = "https://apis.openapi.sk.com/tmap/routes/pedestrian?version=1&format=json&callback=result";

        try {
            const response = await ajaxLoad(url, 'POST', data, headers, "JSON");
            const resultData = response.features;
            const drawInfoArr = [];
            for (let i in resultData) {
                const geometry = resultData[i].geometry;
                const properties = resultData[i].properties;
                let polyline_;

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

            this.drawLine(drawInfoArr);

        } catch (err) {
        }
    },

    // 경로 선 그리기
    drawLine(arrPoint) {
        const polyline_ = new Tmapv2.Polyline({
            path: arrPoint,
            strokeColor: '#DD0000', // 경로 선 색상
            strokeWeight: 6, // 두께
            map: this.map
        });
        this.resultDrawArr.push(polyline_);
    },

    // 초기화
    reset() {
        this.departure = this.arrival = null;
        this.via = []; // 경유점 초기화
        this.markers.forEach(m => m.setMap(null));
        this.resultDrawArr.forEach(d => d.setMap(null));
        this.markers = this.resultDrawArr = [];
    }
};