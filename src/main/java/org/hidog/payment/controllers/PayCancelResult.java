package org.hidog.payment.controllers;

import lombok.Data;

@Data
public class PayCancelResult {
    private String resultCode;
    private String resultMsg;
    private String cancelDate;
    private String cancelTime;
    private String cshrCancelNum;
    private String detailResultCode;
    private String receiptInfo;
}
