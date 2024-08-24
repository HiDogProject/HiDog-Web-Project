package org.hidog.payment.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hidog.global.SHA512;
import org.hidog.order.entities.OrderInfo;
import org.hidog.order.services.OrderInfoService;
import org.hidog.payment.constants.PayMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentCancelService {

    private final OrderInfoService orderInfoService;
    private final PayLogUpdateService payLogUpdateService;
    private final PaymentConfigService paymentConfigService;
    private final RestTemplate restTemplate;
    private final ObjectMapper om;

    /**
     *
     * @param orderNo : 주문번호
     * @param message : 취소메세지
     */
    public void cancel(Long orderNo, String message) {
        OrderInfo orderInfo = orderInfoService.get(orderNo);
        PaymentConfig config = paymentConfigService.get();
        PayMethod payMethod = orderInfo.getPayMethod();
        String tid = orderInfo.getPayTid();
        String authUrl = "https://iniapi.inicis.com/api/v1/refund";


        String iniApiKey = config.getIniApiKey();
        String iniApiIv = config.getIniApiIv();
        String mid = config.getMid();

        long timestamp = orderInfo.getTimestamp();
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDate = dateTime.format(formatter);


        String ip =" 127.0.0.1";

        String hashData = "";
        String text = iniApiKey + "Refund" + payMethod + formattedDate +  ip + mid + tid;
        try {
            hashData = SHA512.encrypt(text);
        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("type", "Refund");
        params.add("paymethod", payMethod.toString()); //수정해야함
        params.add("timestamp", formattedDate);
        params.add("clientIp", ip);
        params.add("mid", mid);
        params.add("tid", tid);
        params.add("msg", message);
        params.add("hashData",hashData);
        System.out.println("params : " + params + "!!!!");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("charset", "utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(URI.create(authUrl), request, String.class);
        System.out.println("response : " + response + "!!!!!!");

        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                Map<String, String> resultMap = om.readValue(response.getBody(), new TypeReference<>() {});
                if (!resultMap.get("resultCode").equals("00")) {
                }
                String payLog = resultMap.entrySet()
                        .stream()
                        .map(entry -> String.format("%s : %s", entry.getKey(), entry.getValue())).collect(Collectors.joining("\n"));


                payLogUpdateService.update(orderNo, payLog);

            }catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
