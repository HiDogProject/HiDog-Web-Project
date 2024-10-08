package org.hidog.order.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hidog.board.entities.BoardData;
import org.hidog.global.Utils;
import org.hidog.global.exceptions.ExceptionProcessor;
import org.hidog.member.MemberUtil;
import org.hidog.order.constants.OrderStatus;
import org.hidog.order.entities.OrderInfo;
import org.hidog.order.services.OrderInfoService;
import org.hidog.order.services.OrderPayService;
import org.hidog.order.services.OrderSaveService;
import org.hidog.order.services.OrderStatusService;
import org.hidog.payment.constants.BankCode;
import org.hidog.payment.constants.PayMethod;
import org.hidog.payment.services.PaymentConfig;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController implements ExceptionProcessor {

    private final Utils utils;
    private final OrderInfoService infoService;
    private final OrderSaveService saveService;
    private final OrderPayService payService;
    private final OrderStatusService statusService;
    private final MemberUtil memberUtil;

    @ModelAttribute("payMethods")
    public List<String[]> payMethods(){
        return PayMethod.getList();
    }

    @ModelAttribute("bankCodes")
    public List<String[]> bankCodes(){ return BankCode.getList(); }


    @GetMapping //주문서양식
    public String index(@ModelAttribute RequestOrder form, HttpSession session, Model model){
        BoardData boardData = (BoardData) session.getAttribute("boardData");
        model.addAttribute("addScript", List.of("order/joinAddress", "order/form"));
        model.addAttribute("addCss", List.of("order/form"));
        form.setOrderName(memberUtil.getMember().getUserName());
        form.setOrderEmail(memberUtil.getMember().getEmail());



        if (boardData != null) {
            form.setBSeq(boardData.getSeq());
            model.addAttribute("boardData", boardData);
        }
        return utils.tpl("order/form");
    }

    @PostMapping
    public String orderSave(@Valid RequestOrder form, Errors erros, Model model, HttpSession session){
        BoardData boardData = (BoardData) session.getAttribute("boardData");
        if (boardData != null) {
            form.setBSeq(boardData.getSeq());
            model.addAttribute("boardData", boardData);
            model.addAttribute("addCss", List.of("order/form"));
        }
        OrderInfo orderInfo = saveService.save(form);
        if(!erros.hasErrors()){
            PaymentConfig config = payService.getConfig(orderInfo.getOrderNo());
            model.addAttribute("config", config);
            model.addAttribute("orderInfo", orderInfo);
        }

        return utils.tpl("order/form");
    }

    @GetMapping("/detail/{orderNo}")
    public String orderDetail(@PathVariable("orderNo") Long orderNo, Model model){
        OrderInfo orderInfo = infoService.get(orderNo, "detail");
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("addCss", List.of("order/detail"));
        return utils.tpl("order/detail");
    }

    //주문 상태 변경
    @PostMapping("/change/{state}/{orderNo}")
    public String orderStateChange(@PathVariable("state") OrderStatus state, @PathVariable("orderNo") Long orderNo, Model model){
        statusService.change(orderNo, state);
        return utils.redirectUrl("shop/sell");
    }
}
