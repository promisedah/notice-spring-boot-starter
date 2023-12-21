package com.github.yunmo.dinger.bytetalk.entity;

import com.github.yunmo.dinger.bytetalk.entity.enums.ByteTalkMsgType;

import java.io.Serializable;
import java.util.Map;

/**
 * BytePost
 *
 * @author ZhangYiHao
 * @version v1.0
 */
public class BytePost extends ByteTalkMessage {
    private String msgType;
    private Content content;

    /**
     * 签名-时间戳
     */
    private String timestamp;
    /**
     * 签名-秘钥
     */
    private String sign;

    public BytePost(Content content) {
        this.msgType = ByteTalkMsgType.POST.type();
        this.content = content;
    }

    public BytePost() {
        setMsgType(ByteTalkMsgType.POST.type());
    }

    @Override
    public void transfer(Map<String, Object> params) {
        this.content.setText(replaceContent(this.content.getText(), params));
    }

    public static class Content implements Serializable {
        /**
         * 文本内容，最长不超过2048个字节，必须是utf8编码
         * */
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Content() {
        }

        public Content(String text) {
            this.text = text;
        }
    }

}

    
    