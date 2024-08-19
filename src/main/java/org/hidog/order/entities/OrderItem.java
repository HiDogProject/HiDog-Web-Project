package org.hidog.order.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hidog.board.entities.BoardData;
import org.hidog.global.entities.BaseEntity;
import org.hidog.order.constants.OrderStatus;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {

    @Id @GeneratedValue
    private Long seq;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.START;

    private String itemName; //ㅇㅇ님의 구매, ㅇㅇ님의 예약 활용가능

    @ManyToOne(fetch = FetchType.LAZY)
    private BoardData boardData;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderInfo orderInfo;

    private int price; //상품 단품 금액

    private int qty; //주문 수량



}