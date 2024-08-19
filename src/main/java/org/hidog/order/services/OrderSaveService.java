package org.hidog.order.services;

import lombok.RequiredArgsConstructor;
import org.hidog.board.services.BoardInfoService;
import org.hidog.order.repository.OrderInfoRepository;
import org.hidog.order.repository.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderSaveService {

    private final OrderInfoRepository orderInfoRepository;
    private final OrderItemRepository orderItemRepository;
    private final BoardInfoService boardInfoService;


}
