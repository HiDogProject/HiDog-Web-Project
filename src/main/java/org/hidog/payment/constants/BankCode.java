package org.hidog.payment.constants;

import java.util.List;

public enum BankCode {
    NH("011", "농협은행"),
    WB("020", "우리은행"),
    SH("088", "신한은행"),
    IB("003", "기업은행"),
    DG("031", "대구은행"),
    BS("032", "부산은행"),
    GJ("034", "광주은행"),
    GN("039", "경남은행"),
    KB("004", "국민은행"),
    SU("007", "수협은행"),
    EC("071", "우체국"),
    HA("081", "하나은행"),
    TS("092", "토스뱅크"),
    KV("089", "케이뱅크"),
    KD("002", "산업은행"),
    SC("023", "제일은행"),
    CT("027", "씨티은행"),
    JJ("035", "제주은행"),
    JB("037", "전북은행"),
    SM("045", "새마을금고"),
    SY("048", "신협은행"),
    KK("090", "카카오뱅크");

    private final String code;
    private final String name;

    // Enum의 생성자 (private)
    private BankCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    // code를 반환하는 메서드
    public String getCode() {
        return code;
    }

    // name을 반환하는 메서드
    public String getName() {
        return name;
    }

    public static List<String[]> getList() {
        return List.of(
                new String[] {NH.name(), NH.getCode(), NH.getName()},
                new String[] {WB.name(), WB.getCode(), WB.getName()},
                new String[] {SH.name(), SH.getCode(), SH.getName()},
                new String[] {IB.name(), IB.getCode(), IB.getName()},
                new String[] {DG.name(), DG.getCode(), DG.getName()},
                new String[] {BS.name(), BS.getCode(), BS.getName()},
                new String[] {GJ.name(), GJ.getCode(), GJ.getName()},
                new String[] {GN.name(), GN.getCode(), GN.getName()},
                new String[] {KB.name(), KB.getCode(), KB.getName()},
                new String[] {SU.name(), SU.getCode(), SU.getName()},
                new String[] {EC.name(), EC.getCode(), EC.getName()},
                new String[] {HA.name(), HA.getCode(), HA.getName()},
                new String[] {TS.name(), TS.getCode(), TS.getName()},
                new String[] {KV.name(), KV.getCode(), KV.getName()},
                new String[] {KD.name(), KD.getCode(), KD.getName()},
                new String[] {SC.name(), SC.getCode(), SC.getName()},
                new String[] {CT.name(), CT.getCode(), CT.getName()},
                new String[] {JJ.name(), JJ.getCode(), JJ.getName()},
                new String[] {JB.name(), JB.getCode(), JB.getName()},
                new String[] {SM.name(), SM.getCode(), SM.getName()},
                new String[] {SY.name(), SY.getCode(), SY.getName()},
                new String[] {KK.name(), KK.getCode(), KK.getName()}
        );
    }
}
