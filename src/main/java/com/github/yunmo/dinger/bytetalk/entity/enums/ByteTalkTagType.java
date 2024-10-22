package com.github.yunmo.dinger.bytetalk.entity.enums;

/**
 * TagType
 *
 * @author ZhangYiHao
 * @version v1.0
 * @package com.github.yunmo.dinger.bytetalk.entity.enums
 * @date 2023/12/18 18:07
 */
public enum ByteTalkTagType {
    PLAIN_TEXT("plain_text"),
    MARKDOWN("markdown"),
    HR("hr"),
    div("div"),
    lark_md("lark_md"),
    // 下面三个支持post类型
    TEXT("text"),
    A("a"),
    AT("at");

    private String type;

    ByteTalkTagType(String type) {
        this.type = type;
    }

    public String type() {
        return type;
    }
}
