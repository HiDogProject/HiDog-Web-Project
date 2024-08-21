package org.hidog.walking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.BoardData;
import org.hidog.board.repositories.BoardDataRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MainMapMarkerService {

    private final BoardDataRepository dataRepository;
    private final ObjectMapper objectMapper;

    public List<Double> startMarkerLocation(int num) {
        List<Double> result = new ArrayList<>();
        List<BoardData> boardDataList = dataRepository.findAll();

        for (BoardData boardData : boardDataList) {
            String jsonData = boardData.getLongText1();
            try {
                List<Map<String, Double>> locations = objectMapper.readValue(jsonData, new TypeReference<List<Map<String, Double>>>(){});
                if (!locations.isEmpty()) {
                    Map<String, Double> firstLocation = locations.get(num);
                    Double lat = firstLocation.get("lat");
                    Double lng = firstLocation.get("lng");
                    result.add(lat);
                    result.add(lng);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(result);
        return result;
    }
}
