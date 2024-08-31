const viewMapLib = {
    departure: null, // 출발지 LatLng 객체
    arrival: null, // 도착지 LatLng 객체
    via: [], // 경유지 LatLng 객체 배열
    markers: [], // 마커
    viaMarkers: [], // 경유 마커
    resultDrawArr: [],  // 경로
    map: null, // 지도 객체
    width: '40%',
    height: '500px',
    zoom: 17,
    markerPointArray: [],
    init() {
        const opt = {
            position: this.departure,
            map: this.map,
            icon: 'https://github.com/user-attachments/assets/dfb7b9b2-49c2-4ac1-a3cb-d129d9b36eb9',
            iconSize: new Tmapv2.Size(50, 50),
            animation: Tmapv2.MarkerOptions.ANIMATE_BOUNCE,
            animationLength: 900,
        };
        this.arrival = this.departure;
        const startMarker = new Tmapv2.Marker(opt);
        this.markers.push(startMarker);
        console.log(startMarker)

        let clickable = true;

        startMarker.addListener('click', () => {
            if (clickable) {
                // 경유지 마커 설정
                const viaPointsData = JSON.parse(viewMapLib.markerPointArray.viaPoints);
                this.via = viaPointsData;
                this.viaPoints = viaPointsData.map(point => new Tmapv2.LatLng(point.lat, point.lng));

                clickable = false;

                this.viaPoints.forEach(viaPoint => {
                    const opt = {
                        position: viaPoint,
                        map: this.map,
                        icon: 'https://github.com/user-attachments/assets/62de235a-400d-4f78-b865-e4ab7d061828',
                        iconSize: new Tmapv2.Size(35, 35),
                        animation: Tmapv2.MarkerOptions.ANIMATE_BALLOON,
                    };
                    const viaMarker = new Tmapv2.Marker(opt);
                    this.viaMarkers.push(viaMarker);
                });
                this.route();
            } else {
                this.viaMarkers.forEach(marker => {
                    marker.setMap(null);
                });
                this.viaMarkers = [];

                this.hideRoute();
                clickable = true;
                this.resultDrawArr = [];
            }
        });
    },

    load(mapId, width, height, zoom) {
        this.width = width ?? '80%';
        this.height = height ?? '500px';
        this.zoom = zoom || 17;

        // navigator.geolocation.getCurrentPosition((pos) => {
        //     const { latitude, longitude } = pos.coords;

            this.map = new Tmapv2.Map(mapId, {
                center: this.departure,
                width: this.width,
                height: this.height,
                zoom: this.zoom,
                zoomControl: true,
                scrollwheel: true
            });

            if (typeof this.init === 'function') {
                this.init();
            }
        // });
    },

    async route() {
        const appKey = document.querySelector("meta[name='tmap_apikey']")?.content;

        if (!this.departure || !this.arrival || !appKey) {
            return;
        }

        const { ajaxLoad } = commonLib;

        const passList = this.via.map(point => `${point.lng},${point.lat}`).join('_');

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

                if (geometry.type === "LineString") {
                    for (let j in geometry.coordinates) {
                        const latLng = new Tmapv2.Point(
                            geometry.coordinates[j][0],
                            geometry.coordinates[j][1]
                        );

                        const convertPoint = new Tmapv2.Projection.convertEPSG3857ToWGS84GEO(latLng);
                        const convertChange = new Tmapv2.LatLng(
                            convertPoint._lat.toFixed(12),
                            convertPoint._lng.toFixed(12)
                        );
                        drawInfoArr.push(convertChange);
                    }
                }
            }

            this.drawLine(drawInfoArr);
        } catch (err) {
            console.log(err);
        }
    },

    drawLine(arrPoint) {
        const polyline_ = new Tmapv2.Polyline({
            path: [], // 초기 경로는 빈 배열입니다.
            strokeColor: 'rgba(178,102,53,0.22)',
            strokeWeight: 9,
            direction: true,
            strokeStyle: 'solid',
            directionColor: "white",
            directionOpacity: 0.6,
            map: this.map
        });

        let index = 0;
        const path = [];
        const totalPoints = arrPoint.length;

        function animate() {
            if (index < totalPoints) {
                path.push(arrPoint[index]); // 새로운 점을 배열에 추가.
                polyline_.setPath(path); // 경로를 업데이트
                index++;
                requestAnimationFrame(animate); // 다음 프레임을 요청
            }
        }

        animate();

        this.resultDrawArr.push(polyline_);
    },

    hideRoute() {
        this.resultDrawArr.forEach(d => d.setMap(null));
    },

    showRoute() {
        this.resultDrawArr.forEach(d => d.setMap(this.map));
    },
};

window.addEventListener("DOMContentLoaded", function() {
    if (typeof viewMapLib.load === 'function') {
        const markerPointElement = document.querySelector('[data-markerPoint]');
        const markerPointData = markerPointElement.getAttribute('data-markerPoint');
        viewMapLib.markerPointArray = JSON.parse(markerPointData);

        // 출발지 마커 설정
        const departureData = JSON.parse(viewMapLib.markerPointArray.departurePoint)[0];
        viewMapLib.departure = new Tmapv2.LatLng(departureData.lat, departureData.lng);

        viewMapLib.load("mapId");
    }
});
