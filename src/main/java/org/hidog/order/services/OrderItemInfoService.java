package org.hidog.order.services;

import lombok.RequiredArgsConstructor;
import org.hidog.order.entities.OrderItem;
import org.hidog.order.repository.OrderItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemInfoService {

    private final OrderItemRepository repository;

    public List<OrderItem> get(Long bSeq){
        List<OrderItem> orderItem = repository.findByBoardDataSeq(bSeq);
        return orderItem;
    }
}
