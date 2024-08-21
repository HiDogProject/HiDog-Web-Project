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
    load(mapId, width, height, zoom) {
        this.width = width ?? '80%';
        this.height = height ?? '600px';
        this.zoom = zoom || 17;


        // tmapLib.init();
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
        })
    }

}
