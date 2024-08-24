package org.hidog.payment.services;

import lombok.RequiredArgsConstructor;
import org.hidog.order.entities.OrderInfo;
import org.hidog.order.repository.OrderInfoRepository;
import org.hidog.order.services.OrderInfoService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayLogUpdateService {

    private final OrderInfoRepository orderInfoRepository;
    private final OrderInfoService infoService;

    public void update(Long orderNo, String payLog){
        OrderInfo orderInfo = infoService.get(orderNo);
        String oldPayLog = orderInfo.getPayLog();
        orderInfo.setPayLog(oldPayLog + "\n" + payLog);
        orderInfoRepository.saveAndFlush(orderInfo);



    }
}
