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
package com.github.yunmo.dinger.core;

import com.github.yunmo.dinger.core.entity.DingerRequest;
import com.github.yunmo.dinger.core.entity.DingerResponse;
import com.github.yunmo.dinger.multi.MultiDingerConfigContainer;
import com.github.yunmo.dinger.multi.entity.MultiDingerConfig;
import com.github.yunmo.dinger.support.client.MediaTypeEnum;
import com.github.yunmo.dinger.core.entity.DingerProperties;
import com.github.yunmo.dinger.core.entity.enums.DingerType;
import com.github.yunmo.dinger.core.entity.enums.MessageSubType;
import com.github.yunmo.dinger.core.entity.enums.DingerResponseCodeEnum;
import com.github.yunmo.dinger.core.entity.MsgType;
import com.github.yunmo.dinger.exception.AsyncCallException;
import com.github.yunmo.dinger.exception.SendMsgException;
import com.github.yunmo.dinger.support.CustomMessage;
import com.github.yunmo.dinger.support.sign.SignBase;
import com.github.yunmo.dinger.utils.DingerUtils;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * DingTalk Robot
 *
 * @author Jaemon
 * @since 1.0
 */
public class DingerRobot extends AbstractDingerSender {

    public DingerRobot(DingerProperties dingerProperties, DingerManagerBuilder dingTalkManagerBuilder) {
        super(dingerProperties, dingTalkManagerBuilder);
    }

    @Override
    public DingerResponse send(MessageSubType messageSubType, DingerRequest request) {
        return send(dingerProperties.getDefaultDinger(), messageSubType, request);
    }

    @Override
    public DingerResponse send(DingerType dingerType, MessageSubType messageSubType, DingerRequest request) {
        if (!messageSubType.isSupport()) {
            return DingerResponse.failed(DingerResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED);
        }
        CustomMessage customMessage = customMessage(messageSubType);
        String msgContent = customMessage.message(
                dingerProperties.getProjectId(), request
        );
        request.setContent(msgContent);

        MsgType msgType = messageSubType.msgType(
                dingerType, request
        );
//        MultiDingerConfig multiDingerConfig =
//                MultiDingerConfigContainer
//                        .INSTANCE.get(useDinger, dingerClassName);
//        DingerConfig dingerConfig = null;
//        if (multiDingerConfig != null) {
//            // 拿到MultiDingerConfig中当前应该使用的DingerConfig
//            DingerProperties.Dinger dinger = dingerProperties.getConfig().get(dingerType);
//            dingerConfig = multiDingerConfig.getAlgorithmHandler()
//                    .dingerConfig(
//                            multiDingerConfig.getDingerConfigs(),
//                            DingerConfig.instance(dingerType,dinger.getTokenId())
//                    );
//        }
        if (dingerConfig != null) {
            DingerHelper.assignDinger(dingerConfig);
        }
        return send(msgType);
    }


    /**
     * @param message
     *          消息内容
     * @param <T>
     *          T
     * @return
     *          响应内容 {@link DingerResponse}
     */
    protected <T extends MsgType> DingerResponse send(T message) {
        DingerType dingerType = message.getDingerType();
        String dkid = dingTalkManagerBuilder.dingerIdGenerator.dingerId();
        Map<DingerType, DingerProperties.Dinger> dingers = dingerProperties.getConfig();
        if (!
                (
                        dingerProperties.isEnabled() &&
                                dingers.containsKey(dingerType)
                )
        ) {
            return DingerResponse.failed(dkid, DingerResponseCodeEnum.DINGER_DISABLED);
        }

        DingerConfig localDinger = getLocalDinger();
        // dinger is null? use global configuration and check whether dinger send
        boolean dingerConfig = localDinger != null;
        try {
            DingerProperties.Dinger dinger;
            if (dingerConfig) {
                dinger = new DingerProperties.Dinger();
                BeanUtils.copyProperties(localDinger, dinger);
                dinger.setAsync(localDinger.getAsyncExecute());
                dinger.setRobotUrl(dingers.get(dingerType).getRobotUrl());
            } else {
                dinger = dingers.get(dingerType);
            }

            StringBuilder webhook = new StringBuilder();
            webhook.append(dinger.getRobotUrl()).append(dinger.getTokenId());

            if (log.isDebugEnabled()) {
                log.debug("dingerId={} send message and use dinger={}, tokenId={}.", dkid, dingerType, dinger.getTokenId());
            }

            // 处理签名问题(只支持DingTalk)
            if (DingerUtils.isNotEmpty((dinger.getSecret()))) {
                SignBase sign = dingTalkManagerBuilder.dingerSignAlgorithm.sign(dinger.getSecret().trim());

                if (dingerType == DingerType.DINGTALK) {
                    webhook.append(sign.transfer());
                } else if (dingerType == DingerType.BYTETALK) {
                    message.signAttributes(sign);
                }
            }

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", MediaTypeEnum.JSON.type());

            // 异步处理, 直接返回标识id
            if (dinger.isAsync()) {
                dingTalkManagerBuilder.dingTalkExecutor.execute(() -> {
                    try {
                        String result = dingTalkManagerBuilder.dingerHttpClient.post(
                                webhook.toString(), headers, message
                        );
                        dingTalkManagerBuilder.dingerAsyncCallback.execute(dkid, result);
                    } catch (Exception e) {
                        exceptionCallback(dkid, message, new AsyncCallException(e));
                    }
                });
                return DingerResponse.success(dkid, dkid);
            }

            String response = dingTalkManagerBuilder.dingerHttpClient.post(
                    webhook.toString(), headers, message
            );
            return DingerResponse.success(dkid, response);
        } catch (Exception e) {
            exceptionCallback(dkid, message, new SendMsgException(e));
            return DingerResponse.failed(dkid, DingerResponseCodeEnum.SEND_MESSAGE_FAILED);
        }
    }

}