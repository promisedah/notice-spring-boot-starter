package com.github.yunmo.dinger.bytetalk.entity.enums;

/**
 * ByteTalkCardType
 *
 * @author ZhangYiHao
 * @version v1.0
 * @package com.github.yunmo.dinger.bytetalk.entity.enums
 * @date 2024/10/16 11:25
 */
public enum ByteTalkCardType {
    /**
     * template类型
     */
    TEMPLATE("template"),
    ;

    private String type;

    ByteTalkCardType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
