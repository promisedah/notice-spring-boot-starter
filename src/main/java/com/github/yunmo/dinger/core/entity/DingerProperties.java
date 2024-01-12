/*
 * Copyright ©2015-2023 Jaemon. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.yunmo.dinger.core.entity;

import com.github.yunmo.dinger.core.entity.enums.DingerType;
import com.github.yunmo.dinger.exception.InvalidPropertiesFormatException;
import com.github.yunmo.dinger.utils.ConfigTools;
import com.github.yunmo.dinger.utils.DingerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.github.yunmo.dinger.constant.DingerConstant.DINGER_PROP_PREFIX;


/**
 * 属性配置类
 *
 * @author Jaemon
 * @since 1.0
 */
@ConfigurationProperties(prefix = DINGER_PROP_PREFIX)
public class DingerProperties implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(DingerProperties.class);

    /**
     * 是否启用DingTalk, 默认true, 选填
     */
    private boolean enabled = true;

    /**
     * title前缀，只生效于 {@link com.github.yunmo.dinger.DingerSender}
     */
    private String titlePrefix;

    /**
     * dinger类型 <code>key={@link DingerType}, value={@link Dinger}</code>, 必填
     */
    private Map<DingerType, Dinger> config = new LinkedHashMap<>();

    /**
     * 项目名称, 必填 <code>eg: ${spring.application.name}</code>
     * */
    private String projectId;

    /**
     * dinger xml配置路径(需要配置xml方式Dinger时必填), 选填
     *
     * <blockquote>
     *     notice.default.dinger-locations: classpath*:dinger/*.xml
     *     notice.default.dinger-locations: classpath*:dinger/*\/*.xml
     * </blockquote>
     * */
    private String dingerLocations;

    /** 默认的Dinger, 不指定则使用{@link DingerProperties#config}中的第一个, 选填 */
    private DingerType defaultDinger;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Map<DingerType, Dinger> getConfig() {
        return config;
    }

    public void setConfig(Map<DingerType, Dinger> config) {
        this.config = config;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getDingerLocations() {
        return dingerLocations;
    }

    public void setDingerLocations(String dingerLocations) {
        this.dingerLocations = dingerLocations;
    }

    public DingerType getDefaultDinger() {
        return defaultDinger;
    }

    public void setDefaultDinger(DingerType defaultDinger) {
        this.defaultDinger = defaultDinger;
    }

    public String getTitlePrefix() {
        return titlePrefix;
    }

    public DingerProperties setTitlePrefix(String titlePrefix) {
        this.titlePrefix = titlePrefix;
        return this;
    }

    public static class Dinger {
        /**
         * 请求地址前缀-选填
         * */
        private String robotUrl;
        /**
         * 获取 access_token, 必填
         *
         * <blockquote>
         *     填写Dinger机器人设置中 webhook access_token | key后面的值
         *      <br /><br />
         *
         *      <ul>
         *          <li>DingTalk： https://oapi.dingtalk.com/robot/send?access_token=c60d4824e0ba4a30544e81212256789331d68b0085ed1a5b2279715741355fbc</li>
         *          <li>tokenId=c60d4824e0ba4a30544e81212256789331d68b0085ed1a5b2279715741355fbc</li>
         *          <li>WeTalk： https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=20201220-7082-46d5-8a39-2ycy23b6df89</li>
         *          <li>tokenId=20201220-7082-46d5-8a39-2ycy23b6df89</li>
         *      </ul>
         * </blockquote>
         * */
        private String tokenId;
        /**
         * 选填, 签名秘钥。 需要验签时必填(钉钉机器人提供)
         */
        private String secret;

        /**
         * 选填, 是否需要对tokenId进行解密, 默认false
         */
        private boolean decrypt = false;

        /**
         * 选填(当decrypt=true时, 必填), 解密密钥
         *
         * <br /><br />
         *
         * <b>解密密钥获取方式</b>
         * <ul>
         *     <li>java -jar dinger-spring-boot-starter-[1.0.0].jar [tokenId]</li>
         *     <li>ConfigTools.encrypt(tokenId)</li>
         * </ul>
         */
        private String decryptKey;

        /**
         * 选填, 是否开启异步处理, 默认： false
         */
        private boolean async = false;

        public String getRobotUrl() {
            return robotUrl;
        }

        public void setRobotUrl(String robotUrl) {
            this.robotUrl = robotUrl;
        }

        public String getTokenId() {
            return tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public boolean isDecrypt() {
            return decrypt;
        }

        public void setDecrypt(boolean decrypt) {
            this.decrypt = decrypt;
        }

        public String getDecryptKey() {
            return decryptKey;
        }

        public void setDecryptKey(String decryptKey) {
            this.decryptKey = decryptKey;
        }

        public boolean isAsync() {
            return async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Map.Entry<DingerType, Dinger> entry : config.entrySet()) {
            DingerType dingerType = entry.getKey();
            if (!dingerType.isEnabled()) {
                throw new InvalidPropertiesFormatException(
                        String.format("dinger=%s is disabled", dingerType)
                );
            }
            Dinger dinger = entry.getValue();

            String tokenId = dinger.getTokenId();
            {
                if (DingerUtils.isEmpty(tokenId)) {
                    throw new InvalidPropertiesFormatException(
                            "notice.default.token-id is empty."
                    );
                }
            }

            if (DingerUtils.isEmpty(dinger.robotUrl)) {
                dinger.robotUrl = dingerType.getRobotUrl();
            }

            if (dingerType == DingerType.WETALK) {
                dinger.secret = null;
            }

            boolean check = dinger.decrypt
                    && DingerUtils.isEmpty(dinger.decryptKey);
            if (check) {
                throw new InvalidPropertiesFormatException(
                        "notice.default.decrypt is true but notice.default.decrypt-key is empty."
                );
            }

            if (dinger.decrypt) {
                dinger.tokenId = ConfigTools.decrypt(dinger.decryptKey, dinger.tokenId);
            } else {
                dinger.decryptKey = null;
            }

            if (defaultDinger == null) {
                defaultDinger = dingerType;
                if (log.isDebugEnabled()) {
                    log.debug("defaultDinger undeclared and use first dingers dingerType, defaultDinger={}.", defaultDinger);
                }
            }
        }

        if (config.isEmpty()) {
            throw new InvalidPropertiesFormatException(
                    "notice.default.config is empty."
            );
        }

        if (!defaultDinger.isEnabled()) {
            throw new InvalidPropertiesFormatException(
                    "notice.default.default-dinger is disabled."
            );
        }

        {
            if (DingerUtils.isEmpty(this.projectId)) {
                throw new InvalidPropertiesFormatException(
                        "notice.default.project-id is empty."
                );
            }
        }

    }
}