package org.hidog.payment.constants;

import java.util.List;

public enum BankCode {
    NH("11", "농협은행"),
    WB("20", "우리은행"),
    SH("88", "구)신한은행"),
    IB("3", "기업은행"),
    DG("31", "대구은행"),
    BS("32", "부산은행"),
    GJ("34", "광주은행"),
    GN("39", "경남은행"),
    KB("04", "국민은행"),
    SU("07", "수협중앙"),
    EC("71", "우체국"),
    HA("81", "하나은행"),
    TS("92", "토스뱅크"),
    KV("89", "케이뱅크"),
    KD("02", "한국산업은행"),
    SC("23", "SC제일은행"),
    CT("27", "한국씨티은행 (구 한미"),
    JJ("35", "제주은행"),
    JB("37", "전북은행"),
    SM("45", "새마을금고"),
    SY("48", "신용협동조합중앙회"),
    KK("90", "카카오뱅크");


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
