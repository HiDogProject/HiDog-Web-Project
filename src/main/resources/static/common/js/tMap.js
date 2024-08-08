// 페이지가 로딩이 된 후 호출하는 함수입니다.
var map,marker;
var lonlat;
var markers = [];
document.addEventListener('DOMContentLoaded', function() {
        initTmap();
    }
);

function initTmap() {
    // map 생성
    // Tmapv3.Map을 이용하여, 지도가 들어갈 div, 넓이, 높이를 설정합니다.
    var map = new Tmapv3.Map("map_div", { // 지도가 생성될 div
        center: new Tmapv3.LatLng(37.56520450, 126.98702028),
        width: "80%",    // 지도의 넓이
        height: "600px",    // 지도의 높이
        zoom: 16    // 지도 줌레벨
    });

    map.on("Click", function(evt) {
        removeMarkers()
        lngLat = evt.data.lngLat;
        console.log(lngLat);
        marker = new Tmapv3.Marker({
            position: new Tmapv3.LatLng(lngLat._lat,lngLat._lng),
            map: map
        });

        markers.push(marker);
    });
}

// 모든 마커를 제거하는 함수입니다.
function removeMarkers() {
    for (var i = 0; i < markers.length; i++) {
        markers[i].setMap(null);
    }
    markers = [];
}

