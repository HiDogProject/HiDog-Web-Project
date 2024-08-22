package org.hidog.board.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.Board;
import org.hidog.global.Utils;
import org.hidog.global.rests.JSONData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardConfigInfoService {
    private final RestTemplate restTemplate; // 외부 API를 호출하기 위해 사용되는 Spring의 HTTP 클라이언트
    private final ObjectMapper om; // JSON 데이터를 Java 객체로 변환하거나, 그 반대로 변환하기 위해 사용되는 Jackson 라이브러리의 객체
    private final Utils utils;

    /**
     * 게시판 설정 조회
     *
     * @param bid
     * @return
     */
    public Optional<Board> get(String bid) {
        try {
            String url = utils.adminUrl("/api/board/config/" + bid); // 특정 게시판 ID(bid)에 대한 설정을 가져올 API의 URL을 생성
            ResponseEntity<JSONData> response = restTemplate.getForEntity(url, JSONData.class); // 지정된 URL로 GET 요청을 보내고, 그 응답을 ResponseEntity<JSONData> 객체에 저장

            if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                JSONData jsonData = response.getBody(); // 응답의 본문을 JSONData 객체로 가져옴
                if (!jsonData.isSuccess()) {

                    return Optional.empty();
                }

                Object data = jsonData.getData(); // JSONData 객체에서 data 필드를 가져옴. 이 데이터는 보드 설정 정보를 포함

                Board board = om.readValue(om.writeValueAsString(data), Board.class); // ObjectMapper를 사용하여 data를 Board 객체로 변환. 먼저 data를 JSON 문자열로 직렬화하고, 이를 다시 Board 클래스의 객체로 역직렬화

                //om.writeValueAsString(data)
                //역할: 이 메서드는 data 객체를 JSON 문자열로 직렬화합니다.
                //이유: data는 앞서 Object 타입으로 저장되었기 때문에, 이를 직접 Board 객체로 변환하기 전에 먼저 JSON 문자열로 변환합니다.
                //결과: data가 포함된 JSON 구조를 문자열로 얻습니다. 예를 들어, 만약 data가 게시판 정보라면 다음과 같은 JSON 문자열이 생성될 수 있습니다:

                // om.readValue(jsonString, Board.class) : 이 메서드는 JSON 문자열을 읽어서 지정된 클래스 타입의 객체로 변환. 여기서 jsonString은 om.writeValueAsString(data)에서 얻은 JSON 문자열입니다.
                //이유: 이제 이 JSON 문자열을 Board 클래스의 인스턴스로 변환하여 실제로 사용할 수 있는 객체로 만듭니다.
                //결과: JSON 문자열이 Board 클래스의 인스턴스로 변환

                // 직렬화: data 객체를 JSON 문자열로 변환합니다. 이는 data가 다양한 형태일 수 있기 때문에 JSON 문자열로 통일하기 위한 작업입니다.
                //역직렬화: 이 JSON 문자열을 Board 클래스의 인스턴스로 변환합니다. 이를 통해 응답 데이터가 Board 객체로 변환되어 이후 코드에서 쉽게 사용할 수 있습니다.
                return Optional.ofNullable(board); // 응답이 성공적이지 않으면 빈 Optional<Board>를 반환
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * 게시판 리스트 조회
     *
     * @return
     */
    public Optional<Board> getBoardList() {
        try {
            String url = utils.adminUrl("/api/board");
            ResponseEntity<JSONData> response = restTemplate.getForEntity(url, JSONData.class);

            if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                JSONData jsonData = response.getBody();
                if (!jsonData.isSuccess()) {

                    return Optional.empty();
                }

                Object data = jsonData.getData();

                Board board = om.readValue(om.writeValueAsString(data), Board.class);


                return Optional.ofNullable(board);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}