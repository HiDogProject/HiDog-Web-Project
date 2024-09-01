package org.hidog.order;

import lombok.RequiredArgsConstructor;
import org.hidog.order.entities.OrderItem;
import org.hidog.order.repository.OrderItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("orderUtils")
@RequiredArgsConstructor
public class OrderUtils {

    private OrderItemRepository orderItemRepository;

    public boolean isOrderAllowed(Long seq) {
        List<OrderItem> orderItem = orderItemRepository.findByBoardDataSeq(seq);
        if(orderItem.isEmpty()) return true;
        return false;
    }
}

