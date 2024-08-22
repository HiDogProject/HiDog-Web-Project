const mainMapLib = {
    departure: null, // 출발지 LatLng 객체
    arrival: null, // 도착지 LatLng 객체
    departureList: [], // DB 출발지 배열
    via: [], // 경유지 LatLng 객체 배열
    markers: [], // 마커
    resultDrawArr: [],
    map: null, // 지도 객체
    width: '100%',
    height: '400px',
    zoom: 17,
    currentAction: null, // start, end, via
    init() {
        const startMarkerElement = document.querySelector('[data-startMarker]');
        const startMarkerData = startMarkerElement.getAttribute('data-startMarker');
        const startMarkerArray = JSON.parse(startMarkerData);

        // const viaMarkerElement = document.querySelector('[data-viaMarker]');
        // const viaMarkerData = startMarkerElement.getAttribute('data-viaMarker');
        // const viaMarkerArray = JSON.parse(viaMarkerData);

        for (let i = 0; i < startMarkerArray.length; i += 2) {
            const lat = startMarkerArray[i];
            const lng = startMarkerArray[i + 1];



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
                    console.log(startMarker.lat);
                    this.markers.forEach(marker => {
                        if (marker !== startMarker) {
                            marker.setVisible(false);
                        }

                    });

                    clickable = false;
                    console.log("클릭")

                    const position = startMarker.getPosition(); // 마커의 좌표를 가져옴
                    clickDeparturePoint.push({ "lat": position.lat(), "lng": position.lng() });
                    console.log(clickDeparturePoint);

                    commonLib.ajaxLoad('walking/map', 'POST', {clickDeparturePoint}, {
                        "Content-Type": "application/json"
                    })
                        .then(response => {
                            console.log('Server Response:', response);
                        })
                        .catch(error => {
                            console.error('Error:', error);
                        });
                    clickDeparturePoint = [];

                } else {
                    // 모든 마커 보이기
                    this.markers.forEach(marker => {
                        marker.setVisible(true);
                    });
                    clickable = true;
                    console.log("재클릭")
                }
            });
        }
    },
    load(mapId, width, height, zoom) {
        this.width = width ?? '80%';
        this.height = height ?? '600px';
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


    },


}
