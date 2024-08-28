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
    currentAction: null,
    subject: "게시글 제목",
    poster: "작성자",
    content: "게시글 내용",
    seq: null,
    clickable: true,
    init() {
        const startMarkerElement = document.querySelector('[data-startMarker]');
        const startMarkerData = startMarkerElement.getAttribute('data-startMarker');
        const startMarkerArray = JSON.parse(startMarkerData);

        for (let i = 0; i < startMarkerArray.length; i += 2) {
            const lat = parseFloat(startMarkerArray[i]).toFixed(12);
            const lng = parseFloat(startMarkerArray[i + 1]).toFixed(12);

            const opt = {
                position: new Tmapv2.LatLng(lat, lng),
                map: this.map,
                icon: 'https://github.com/user-attachments/assets/dfb7b9b2-49c2-4ac1-a3cb-d129d9b36eb9',
                iconSize: new Tmapv2.Size(50, 50),

            };

            const startMarker = new Tmapv2.Marker(opt);

            this.markers.push(startMarker);

            let clickDeparturePoint = [];

            startMarker.addListener('click', () => {
                if (this.clickable) {
                    this.markers.forEach(marker => {
                        if (marker !== startMarker) {
                            marker.setVisible(false);
                        }
                    });
                    this.updateInfoBoxState()
                    this.showRoute();
                    this.clickable = false;

                    const position = startMarker.getPosition();
                    const latFixed = parseFloat(position.lat()).toFixed(12);
                    const lngFixed = parseFloat(position.lng()).toFixed(12);
                    clickDeparturePoint.push({ "lat": latFixed, "lng": lngFixed });

                    this.departure = this.arrival = position;

                    commonLib.ajaxLoad('walking//map', 'POST', { clickDeparturePoint }, {
                        "Content-Type": "application/json"
                    })
                        .then(response => {
                            console.log('Server Response:', response);
                            this.callback(response);
                        })
                        .catch(error => {
                            console.error('Error:', error);
                        });

                    clickDeparturePoint = [];
                } else {
                    this.markers.forEach(marker => {
                        marker.setVisible(true);
                    });
                    this.viaMarkers.forEach(marker => {
                        marker.setMap(null);
                    });
                    this.updateInfoBoxState()

                    this.viaMarkers = [];

                    this.hideRoute();
                    this.clickable = true;
                    this.resultDrawArr = [];

                    this.subject = null;
                    this.content = null;
                    this.poster = null;
                    this.seq = null;

                    this.updateInfoBox(this.subject, this.content, this.poster, this.seq);
                }
            });
        }
    },

    load(mapId, width, height, zoom) {
        this.width = width ?? '70%';
        this.height = height ?? '900px';
        this.zoom = zoom || 17;


            this.map = new Tmapv2.Map(mapId, {
                center: new Tmapv2.LatLng(37.55643383051803, 126.94502484798474),
                width: this.width,
                height: this.height,
                zoom: this.zoom,
                zoomControl: true,
                scrollwheel: true
            });

            if (typeof this.init === 'function') {
                this.init();
            }
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
            path: arrPoint,
            strokeColor: 'rgba(178,102,53,0.22)',
            strokeWeight: 7,
            direction: true,
            strokeStyle: 'solid',
            directionColor: "white",
            directionOpacity: 0.6,
            map: this.map
        });
        this.resultDrawArr.push(polyline_);
    },

    hideRoute() {
        this.resultDrawArr.forEach(d => d.setMap(null));
    },

    showRoute() {
        this.resultDrawArr.forEach(d => d.setMap(this.map));
    },

    callback(response) {
        this.subject = response.subject;
        this.poster = response.poster;
        this.content = response.content;
        this.seq = response.seq;
        this.boardData = response.boardData;

        const viaPoints = JSON.parse(response.viaPoints);
        console.log("viaPoints", viaPoints);
        this.via = viaPoints;
        viaPoints.forEach(point => {
            const lat = point.lat;
            const lng = point.lng;

            const opt = {
                position: new Tmapv2.LatLng(lat, lng),
                map: this.map,
                icon: 'https://github.com/user-attachments/assets/62de235a-400d-4f78-b865-e4ab7d061828',
                iconSize: new Tmapv2.Size(35, 35)
            };

            const viaMarker = new Tmapv2.Marker(opt);
            this.viaMarkers.push(viaMarker);
        });

        this.route();

        // 인포박스 업데이트
        this.updateInfoBox(this.subject, this.content, this.poster, this.seq);
    },

    updateInfoBox(subject, content, poster, seq, boardData) {
        const infoBox = document.getElementById('infoBox');
        if (infoBox) {
            const titleEl = infoBox.querySelector('.info-title');
            const contentEl = infoBox.querySelector('.info-content');
            const posterEl = infoBox.querySelector('.info-poster');
            const seqEl = infoBox.querySelector('.info-seq');


            if (titleEl) titleEl.innerHTML = subject || "제목";
            if (contentEl) contentEl.innerHTML = content || "게시글 내용";
            if (posterEl) posterEl.innerHTML = poster || "작성자";
            if (seqEl) seqEl.innerHTML = seq || "seq";
        }
    }

    ,
    updateInfoBoxState() {
        const infoBox = document.getElementById('infoBox');
        const toggleButton = document.querySelector('#toggleButton');

        if (!this.clickable && infoBox.classList.contains('info-box-expanded')) {
            infoBox.classList.remove('info-box-expanded');
        } else {
            infoBox.classList.add('info-box-expanded');
        }

        if (infoBox.classList.contains('info-box-expanded')) {
            toggleButton.style.right = '300px';
            toggleButton.textContent = '>'; // 열렸을 때
        } else {
            toggleButton.style.right = '0px';
            toggleButton.textContent = '<'; // 닫혔을 때
        }
    }

};
