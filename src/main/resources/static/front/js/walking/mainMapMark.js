const mainMapLib = {
    departure: null, // 출발지 LatLng 객체
    arrival: null, // 도착지 LatLng 객체
    departureList: [], // DB 출발지 배열
    via: [], // 경유지 LatLng 객체 배열
    markers: [], // 마커
    viaMarkers: [], // 경유 마커
    resultDrawArr: [],  // 경로
    map: null, // 지도 객체
    width: '100%',
    height: '600px',
    zoom: 17,
    currentAction: null, // start, end, via
    init() {
        const startMarkerElement = document.querySelector('[data-startMarker]');
        const startMarkerData = startMarkerElement.getAttribute('data-startMarker');
        const startMarkerArray = JSON.parse(startMarkerData);

        for (let i = 0; i < startMarkerArray.length; i += 2) {
            const lat = parseFloat(startMarkerArray[i]).toFixed(12);
            const lng = parseFloat(startMarkerArray[i + 1]).toFixed(12);

            // 마커 옵션 설정
            const opt = {
                position: new Tmapv2.LatLng(lat, lng),
                map: this.map,
                icon: 'https://notion-emojis.s3-us-west-2.amazonaws.com/prod/svg-twitter/1f415.svg',
                iconSize: new Tmapv2.Size(50, 50)
            };

            // 마커 생성
            const startMarker = new Tmapv2.Marker(opt);
            this.markers.push(startMarker);
            let clickable = true;

            let clickDeparturePoint = [];

            startMarker.addListener('click', () => {
                if (clickable) {
                    // 다른 마커 숨기기
                    this.markers.forEach(marker => {
                        if (marker !== startMarker) {
                            marker.setVisible(false);
                        }
                    });
                    this.showRoute();
                    clickable = false;
                    console.log("클릭");

                    const position = startMarker.getPosition(); // 마커의 좌표를 가져옴
                    const latFixed = parseFloat(position.lat()).toFixed(12);
                    const lngFixed = parseFloat(position.lng()).toFixed(12);
                    clickDeparturePoint.push({ "lat": latFixed, "lng": lngFixed });

                    // 출발점을 클릭한 마커 좌표로 고정함.
                    this.departure = this.arrival = position;

                    let content = "<div style='position: static; display: flex; flex-direction: column; align-items: center; font-size: 14px; box-shadow: 5px 5px 5px #00000040; border-radius: 10px; top: 410px; left: 500px; width: 200px; background: #E0F7FA;' >" +
                        "<div class='img-box' style='position: relative; width: 70%; height: 50px; margin-top: 10px; border-radius: 10px; background: #B2EBF2 url(https://github.com/user-attachments/assets/992cd994-72b8-4320-aed6-6dbd2b86557d) no-repeat center;'>" +
                        "</div>" +
                        "<div class='info-box' style='padding: 10px; width: 100%; color: #006064; text-align: center;'>" +
                        "<p style='margin-bottom: 7px; overflow: hidden; margin-top: -10px;'>" + <!-- 제목 위로 이동 -->
                        "<span class='tit' style='font-size: 16px; font-weight: bold; display: block;'>게시글 제목</span>" + <!-- display: block으로 변경 -->
                        "<a href='/' target='_blank' class='link' style='color: #0288D1; font-size: 13px;'></a>" +
                        "</p>" +
                        "<ul class='ul-info' style='list-style: none; padding: 0;'>" + <!-- 중앙 정렬을 위한 리스트 스타일 제거 -->
                        "<li class='li-addr' style='margin-bottom: 5px; color: #004D40;'>" + <!-- padding 제거 -->
                        "<p class='new-addr' style='text-align: center;'>글 내용 간단</p>" + <!-- 중앙 정렬 -->
                        "<p class='old-addr' style='color: #00796B; text-align: center;'>작성자 : 김 짱 현</p>" + <!-- 중앙 정렬 -->
                        "</li>" +
                        "<li class='li-tell' style='color: #004D40;'>" +
                        "</li>" +
                        "</ul>" +
                        "<ul class='btn-group' style='display: table; width: 100%; border-radius: 3px; height: 40px; border: 1px solid #B2EBF2; margin-top: 10px; text-align: center; background: #B2DFDB;'>" +
                        "<li style='display: table-cell; vertical-align: middle; width: 50%; height: 100%; border-right: 1px solid #B2EBF2;'>" +
                        "<a href='#' title='게시글 가기' style='color: #006064;'><img src='https://github.com/user-attachments/assets/ee5cbff7-8855-4950-8153-9acae4582203' style='vertical-align: middle; margin-right: 5px;'></a>" +
                        "</li>" +
                        "<li style='display: table-cell; vertical-align: middle; width: 50%; height: 100%;'>" +
                        "<a href='#' title='채팅하기' style='color: #006064;'><img src='https://github.com/user-attachments/assets/f00b91e9-0c10-4ef7-acc9-05d81eb687d0' style='vertical-align: middle; margin-right: 5px;'></a>" +
                        "</li>" +
                        "</ul>" +
                        "</div>" +
                        "<a href='javascript:void(0)' onclick='onClose()' class='btn-close' style='position: absolute; top: 10px; right: 10px; display: block; width: 15px; height: 15px; background: url(resources/images/sample/btn-close-w.svg) no-repeat center;'></a>" +
                        "</div>";







                    //Popup 객체 생성.
                    infoWindow = new Tmapv2.InfoWindow({
                        position: new Tmapv2.LatLng(latFixed, lngFixed), //Popup 이 표출될 맵 좌표
                        content: content, //Popup 표시될 text
                        type: 2, //Popup의 type 설정.
                        map: this.map //Popup이 표시될 맵 객체
                    });
                    console.log(infoWindow)

                    commonLib.ajaxLoad('walking/map', 'POST', {clickDeparturePoint}, {
                        "Content-Type": "application/json"
                    })
                        .then(response => {
                            console.log('Server Response:', response);
                                callback(response);
                        })
                        .catch(error => {
                            console.error('Error:', error);
                        });

                    clickDeparturePoint = [];

                    function callback(response) {
                        const viaPoints = response;
                        mainMapLib.via = viaPoints;
                        response.forEach(point => {
                            const lat = point.lat;
                            const lng = point.lng;

                            // 마커 옵션 설정
                            const opt = {
                                position: new Tmapv2.LatLng(lat, lng),
                                map: mainMapLib.map,
                                icon: 'https://notion-emojis.s3-us-west-2.amazonaws.com/prod/svg-twitter/1f6a9.svg',
                                iconSize: new Tmapv2.Size(50, 50)
                            };

                            const viaMarker = new Tmapv2.Marker(opt);

                            mainMapLib.viaMarkers.push(viaMarker);
                        });

                        mainMapLib.route();




                        console.log(infoWindow);
                    }
                } else {
                    // 모든 마커 보이기
                    this.markers.forEach(marker => {
                        marker.setVisible(true);
                    });
                    mainMapLib.viaMarkers.forEach(marker => {
                        marker.setMap(null);
                    });
                    mainMapLib.viaMarkers = [];
                    this.hideRoute();
                    clickable = true;
                    console.log("재클릭");
                    this.resultDrawArr = [];
                    infoWindow.setVisible(false);
                }
            });
        }
    },
    load(mapId, width, height, zoom) {
        this.width = width ?? '80%';
        this.height = height ?? '800px';
        this.zoom = zoom || 17;



        navigator.geolocation.getCurrentPosition((pos) => {
            const {latitude, longitude} = pos.coords;

            // 현재 위치 기반으로 지도 띄우기
            this.map = new Tmapv2.Map(mapId, {
                center: new Tmapv2.LatLng(latitude, longitude),
                width: this.width,
                height: this.height,
                zoom: this.zoom,
                zoomControl: true,
                scrollwheel: true
            });

            if (typeof this.init === 'function') {
                this.init();
            }

        })
    }
    ,
    // 경로 그리기
    async route() {
        const appKey = document.querySelector("meta[name='tmap_apikey']")?.content;

        if (!this.departure || !this.arrival || !appKey) {
            return;
        }

        const { ajaxLoad } = commonLib;

        console.log(this.via)
        // 경유지 좌표를 passList 형식으로 변환
        // 경유지 좌표를 passList 형식으로 변환
        const passList = this.via.map(point => `${point.lng},${point.lat}`).join('_');



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
                            convertPoint._lat.toFixed(12),
                            convertPoint._lng.toFixed(12)
                        );
                        drawInfoArr.push(convertChange);
                    }
                }
            }

            this.drawLine(drawInfoArr);
        }
        catch (err) {
            console.log()
        }
    },

    // 경로 선 그리기
    drawLine(arrPoint) {
        const polyline_ = new Tmapv2.Polyline({
            path: arrPoint,
            strokeColor: '#ff0090', // 경로 선 색상
            strokeWeight: 6, // 두께
            map: this.map
        });
        this.resultDrawArr.push(polyline_);
    },

    // 경로 숨기기
    hideRoute() {
        this.resultDrawArr.forEach(d => d.setMap(null));
    },

    // 경로 표시 하기
    showRoute() {
        this.resultDrawArr.forEach(d => d.setMap(this.map));
    }
}
