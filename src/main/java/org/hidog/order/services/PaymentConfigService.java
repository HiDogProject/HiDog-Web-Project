package org.hidog.order.services;

import lombok.RequiredArgsConstructor;
import org.hidog.config.services.ConfigInfoService;
import org.hidog.global.SHA256;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PaymentConfigService {
    private final ConfigInfoService infoService;

    public PaymentConfig get(long oid, int price) { // oid : 주문번호, price : 가격
        PaymentConfig config = infoService.get("payment", PaymentConfig.class)
                .orElseGet(PaymentConfig::new);

        long timestamp = new Date().getTime();
        String signKey = config.getSignKey();

        // 필수로 필요한 데이터 2차가공
        try {
            // signature S
            String data = String.format("oid=%d&price=%d&timestamp=%d", oid, price, timestamp);
            String signature = SHA256.encrypt(data);
            config.setSignature(signature);
            //signature E

            // verification S
            data=String.format("oid=%d&price=%d&signKey=%s&timestamp=%d", oid, price, signature, timestamp);
            String verification = SHA256.encrypt(data); // 해시만들기
            config.setVerification(verification);
            // verification E

            // mKey S
            String mKey = SHA256.encrypt(signKey);
            config.setMKey(mKey);
            // mKey E

        }catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        config.setTimestamp(timestamp);
        return config;
    }
}
