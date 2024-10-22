package com.github.yunmo.dinger.bytetalk.entity;

import com.github.yunmo.dinger.bytetalk.entity.enums.ByteTalkMsgType;
import com.github.yunmo.dinger.support.sign.SignBase;
import com.github.yunmo.dinger.support.sign.SignResult;

import java.io.Serializable;
import java.util.Map;

/**
 * BytePost
 *
 * @author ZhangYiHao
 * @version v1.0
 */
public class BytePost extends ByteTalkMessage {
    private String msg_type;
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
        this.msg_type = ByteTalkMsgType.POST.type();
        this.content = content;
    }

    public BytePost() {
        setMsgType(ByteTalkMsgType.POST.type());
    }

    @Override
    public void signAttributes(SignBase sign) {
        if (sign == null || !(sign instanceof SignResult)) {
            return;
        }
        SignResult signResult = (SignResult) sign;
        this.timestamp = String.valueOf(signResult.getTimestamp());
        this.sign = signResult.getSign();
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


    public String getMsg_type() {
        return msg_type;
    }

    public BytePost setMsg_type(String msg_type) {
        this.msg_type = msg_type;
        return this;
    }

    public Content getContent() {
        return content;
    }

    public BytePost setContent(Content content) {
        this.content = content;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public BytePost setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public BytePost setSign(String sign) {
        this.sign = sign;
        return this;
    }
}

    
    