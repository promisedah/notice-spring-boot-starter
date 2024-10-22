package com.github.yunmo.dinger.bytetalk.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.yunmo.dinger.bytetalk.entity.enums.ByteTalkCardType;
import com.github.yunmo.dinger.bytetalk.entity.enums.ByteTalkMsgType;
import com.github.yunmo.dinger.core.annatations.DingerInteractive;
import com.github.yunmo.dinger.support.sign.SignBase;
import com.github.yunmo.dinger.support.sign.SignResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ByteInteractive 飞书自定义机器人交互卡片
 *
 * @author ZhangYiHao
 * @version v1.0
 * @package com.github.yunmo.dinger.bytetalk.entity
 * @date 2024/10/15 18:12
 */
public class ByteInteractive extends ByteTalkMessage {
    private String msg_type;
    private Card card;

    /**
     * 签名-时间戳
     */
    private String timestamp;
    /**
     * 签名-秘钥
     */
    private String sign;

    public ByteInteractive() {
        this.msg_type = ByteTalkMsgType.INTERACTIVE.type();
    }

    public ByteInteractive(Card card) {
        this.msg_type = ByteTalkMsgType.INTERACTIVE.type();
        this.card = card;
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
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (DingerInteractive.clazz.isInstance(value)) {
                ByteInteractive.Card card = (ByteInteractive.Card) value;
                this.card = card;
                break;
            }
        }
    }

    public static class Card implements Serializable {
        private String type;
        private Template data;
        private List<Element> elements;
        private Header header;

        public Card() {
        }

        public Card(Template template) {
            this.type = ByteTalkCardType.TEMPLATE.type();
            this.data = template;
        }

        public static Card ofTemplate(Template template) {
            return new Card(template);
        }

        public static Card ofCustom(Header Header, List<Element> elements) {
            final Card customCard = new Card();
            customCard.setHeader(Header);
            customCard.setElements(elements);
            return customCard;
        }

        public String getType() {
            return type;
        }

        public Card setType(String type) {
            this.type = type;
            return this;
        }

        public Template getData() {
            return data;
        }

        public Card setData(Template data) {
            this.data = data;
            return this;
        }

        public List<Element> getElements() {
            return elements;
        }

        public Card setElements(List<Element> elements) {
            this.elements = elements;
            return this;
        }

        public Header getHeader() {
            return header;
        }

        public Card setHeader(Header header) {
            this.header = header;
            return this;
        }

        /**
         * Represents the main header structure of the card.
         */
        public static class Header implements Serializable {
            private Title title;
            private Subtitle subtitle;

            @JsonProperty("text_tag_list")
            private List<TextTag> text_tag_list;

            @JsonProperty("i18n_text_tag_list")
            private Map<String, List<TextTag>> i18nTextTagList;

            /**
             * The theme color of the title.
             * Supports "blue"|"wathet"|"tuiquoise"|"green"|"yellow"|"orange"|"red"|"carmine"|"violet"|"purple"|"indigo"|"grey"|"default".
             * Default value is "default".
             */
            private String template;

            private Icon icon;

            @JsonProperty("ud_icon")
            private UdIcon udIcon;

            // Getters and setters for all fields
        }

        /**
         * Represents the main title of the card.
         */
        public static class Title {
            /**
             * Fixed value "plain_text".
             */
            private String tag;

            /**
             * Content of the main title.
             */
            private String content;

            /**
             * Internationalized content for the main title.
             */
            private I18n i18n;

            // Getters and setters for all fields
        }

        /**
         * Represents the subtitle of the card.
         */
        public static class Subtitle {
            /**
             * Fixed value "plain_text".
             */
            private String tag;

            /**
             * Content of the subtitle.
             */
            private String content;

            /**
             * Internationalized content for the subtitle.
             */
            private I18n i18n;

            // Getters and setters for all fields
        }

        /**
         * Represents internationalized text content.
         */
        public static class I18n {
            @JsonProperty("zh_cn")
            private String zhCn;

            @JsonProperty("en_us")
            private String enUs;

            @JsonProperty("ja_jp")
            private String jaJp;

            @JsonProperty("zh_hk")
            private String zhHk;

            @JsonProperty("zh_tw")
            private String zhTw;

            // Getters and setters for all fields
        }

        /**
         * Represents a text tag in the card header.
         */
        public static class TextTag {
            /**
             * Fixed value "text_tag".
             */
            private String tag;

            private Text text;

            /**
             * Color of the tag.
             */
            private String color;

            // Getters and setters for all fields
        }

        /**
         * Represents the text content of a tag.
         */
        public static class Text {
            /**
             * Fixed value "plain_text".
             */
            private String tag;

            /**
             * Content of the tag.
             */
            private String content;

            // Getters and setters for all fields
        }

        /**
         * Represents a custom prefix icon.
         */
        public static class Icon {
            /**
             * The image key used as the prefix icon.
             */
            @JsonProperty("img_key")
            private String imgKey;

            // Getter and setter for imgKey
        }

        /**
         * Represents an icon from the icon library.
         */
        public static class UdIcon {
            /**
             * The token of the icon.
             */
            private String token;

            private Style style;

            // Getters and setters for all fields
        }

        /**
         * Represents the style of an icon.
         */
        public static class Style {
            /**
             * The color of the icon. Supports setting colors for linear and face icons
             * (i.e., icons with tokens ending in 'outlined' or 'filled').
             */
            private String color;

            // Getter and setter for color
        }

        public static class Element implements Serializable {
            private ElementTag tag;

            @JsonProperty("text_size")
            private String textSize;

            private Href href;
            private String content;

            public ElementTag getTag() {
                return tag;
            }

            public void setTag(ElementTag tag) {
                this.tag = tag;
            }

            public String getTextSize() {
                return textSize;
            }

            public void setTextSize(String textSize) {
                this.textSize = textSize;
            }

            public Href getHref() {
                return href;
            }

            public void setHref(Href href) {
                this.href = href;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }

        public static class Href {
            private UrlVal urlVal;

            public UrlVal getUrlVal() {
                return urlVal;
            }

            public void setUrlVal(UrlVal urlVal) {
                this.urlVal = urlVal;
            }
        }

        public static class UrlVal {
            private String url;

            @JsonProperty("pc_url")
            private String pcUrl;

            @JsonProperty("ios_url")
            private String iosUrl;

            @JsonProperty("android_url")
            private String androidUrl;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getPcUrl() {
                return pcUrl;
            }

            public void setPcUrl(String pcUrl) {
                this.pcUrl = pcUrl;
            }

            public String getIosUrl() {
                return iosUrl;
            }

            public void setIosUrl(String iosUrl) {
                this.iosUrl = iosUrl;
            }

            public String getAndroidUrl() {
                return androidUrl;
            }

            public void setAndroidUrl(String androidUrl) {
                this.androidUrl = androidUrl;
            }
        }

        public enum ElementTag {
            @JsonProperty("markdown")
            MARKDOWN,
            @JsonProperty("hr")
            HR,
            @JsonProperty("div")
            DIV,
            @JsonProperty("plain_text")
            PLAIN_TEXT,
            @JsonProperty("lark_md")
            LARK_MD,
            @JsonProperty("action")
            ACTION,
            // 下面三个支持post类型
            @JsonProperty("text")
            TEXT,
            @JsonProperty("a")
            A,
            @JsonProperty("at")
            AT;
        }

    }

    public static class Template implements Serializable {
        @JsonProperty("template_id")
        private String templateId;
        @JsonProperty("template_version_name")
        private String templateVersionName;
        @JsonProperty("template_variable")
        private Map<String, Object> templateVariable;

        public Template() {
        }

        public Template(String templateId, String templateVersionName, Map<String, Object> templateVariable) {
            this.templateId = templateId;
            this.templateVersionName = templateVersionName;
            this.templateVariable = templateVariable;
        }

        public String getTemplateId() {
            return templateId;
        }

        public Template setTemplateId(String templateId) {
            this.templateId = templateId;
            return this;
        }

        public String getTemplateVersionName() {
            return templateVersionName;
        }

        public Template setTemplateVersionName(String templateVersionName) {
            this.templateVersionName = templateVersionName;
            return this;
        }

        public Map<String, Object> getTemplateVariable() {
            return templateVariable;
        }

        public Template setTemplateVariable(Map<String, Object> templateVariable) {
            this.templateVariable = templateVariable;
            return this;
        }
    }

    public String getMsg_type() {
        return msg_type;
    }

    public ByteInteractive setMsg_type(String msg_type) {
        this.msg_type = msg_type;
        return this;
    }

    public Card getCard() {
        return card;
    }

    public ByteInteractive setCard(Card card) {
        this.card = card;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public ByteInteractive setTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getSign() {
        return sign;
    }

    public ByteInteractive setSign(String sign) {
        this.sign = sign;
        return this;
    }


}

    
    