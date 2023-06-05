package com.github.yunmo.dinger.core.entity;

/**
 * NoticeLevel
 *
 * @author ZhangYiHao
 * @version v1.0
 * @package com.github.yunmo.dinger.core.entity
 * @date 2023/4/13 14:14
 */
public enum NoticeLevel {
    /**
     * NORMAL 正常
     * INFO 绿色标题
     * WARNING，ERROR 橙色标题
     * @author ZhangYiHao
     * @date 2023/4/13 14:15
     */
    NORMAL,INFO,WARNING,ERROR;

    public String format(String content) {
        String fontXml = "<font color=\"%s\">%s</font>";
        String color;
        switch (this){
            case NORMAL:
                color = "comment";
            case INFO:
                color = "info";
            case WARNING:
            case ERROR:
                color = "warning";
            default:
                color = "comment";
        }
        return String.format(fontXml, this.name(), content);
    }
}
