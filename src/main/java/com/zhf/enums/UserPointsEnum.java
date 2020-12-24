package com.zhf.enums;

import lombok.Getter;

/**
 * 支付枚举
 */
@Getter
public enum UserPointsEnum {

    START(0,"用户初始积分"),
    SUCCESS(1,"其他"),
    ;

    private Integer code;

    private String message;

    UserPointsEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
