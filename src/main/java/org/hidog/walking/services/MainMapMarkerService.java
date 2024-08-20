package org.hidog.walking.services;

import lombok.RequiredArgsConstructor;
import org.hidog.board.repositories.BoardDataRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MainMapMarkerService {

    private final BoardDataRepository dataRepository;

}
