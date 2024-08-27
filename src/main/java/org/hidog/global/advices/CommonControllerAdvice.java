package org.hidog.global.advices;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice("org.hidog")// 범위 설정
@RequiredArgsConstructor
public class CommonControllerAdvice {//전역에서 확인 가능

}