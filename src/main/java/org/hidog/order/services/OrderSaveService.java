package org.hidog.order.services;

import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.BoardData;
import org.hidog.board.services.BoardInfoService;
import org.hidog.member.MemberUtil;
import org.hidog.order.constants.OrderStatus;
import org.hidog.order.controllers.RequestOrder;
import org.hidog.order.entities.OrderInfo;
import org.hidog.order.entities.OrderItem;
import org.hidog.order.repository.OrderInfoRepository;
import org.hidog.order.repository.OrderItemRepository;
import org.hidog.payment.constants.PayMethod;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderSaveService {

    private final OrderInfoRepository infoRepository;
    private final OrderItemRepository itemRepository;
    private final BoardInfoService boardInfoService;
    private final MemberUtil memberUtil;


    public OrderInfo save(RequestOrder form){

//        //현재 거래중 인지 확인
//        Long bSeq = form.getBSeq();
//        List<OrderItem> orderItemList = itemRepository.findByBoardDataSeq(bSeq);
//        if(!orderItemList.isEmpty()){
//            throw new ItemAlreadySoldException();
//        }
//
//        //구매자와 판매자가 같은 경우
//        BoardData data = boardInfoService.get(bSeq);
//        if(data.getMember().getEmail().equals(memberUtil.getMember().getEmail())){
//         throw new BuyerSellerConflictError();
//        }



        //게시글 조회
        BoardData boardData = boardInfoService.get(form.getBSeq());
        long orderNo = form.getOrderNo() < 1L ? System.currentTimeMillis() : form.getOrderNo();
        Long num1 = boardData.getNum1(); //가격
        Long num2 = boardData.getNum2(); //수량

        int price = num1 == null ? 0 : num1.intValue();
        int qty = num2 == null && num2 > 1L ? 1: num2.intValue();
        String itemName = boardData.getText1();



        /* 주문서 정보 저장 S*/
        OrderInfo orderInfo = new ModelMapper().map(form, OrderInfo.class);
        orderInfo.setPayMethod(PayMethod.valueOf(form.getPayMethod()));
        orderInfo.setOrderNo(orderNo);
        orderInfo.setMember(memberUtil.getMember());
        orderInfo.setStatus(OrderStatus.START);


        String orderMobile = form.getOrderMobile();
        if(StringUtils.hasText(orderMobile)){
            orderMobile = orderMobile.replaceAll("\\D","");
            orderInfo.setOrderMobile(orderMobile);
        }

        infoRepository.saveAndFlush(orderInfo);


        /* 주문서 정보 저장 E*/

        /* 주문 상품 정보 저장 S */
        OrderItem orderItem = OrderItem.builder()
                .orderInfo(orderInfo)
                .itemName(itemName) //시점데이터 때문에 boardData대신 이전 데이터도 이미 있어야한다.
                .price(price)
                .qty(qty)
                .boardData(boardData)
                .build();

        itemRepository.saveAndFlush(orderItem);


        orderInfo.setOrderItem(orderItem); //추가추가
        infoRepository.saveAndFlush(orderInfo);
        /* 주문 상품 정보 저장 E */

        return orderInfo;
    }

}
