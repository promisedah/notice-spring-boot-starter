package com.github.yunmo.dinger.multi.entity;

import com.github.yunmo.dinger.core.entity.enums.DingerType;
import com.github.yunmo.dinger.multi.DingerConfigHandler;

import static com.github.yunmo.dinger.constant.DingerConstant.SPOT_SEPERATOR;

/**
 * MultiDingerHandlerDefinition
 *
 * @author ZhangYiHao
 * @version v1.0
 * @package com.github.yunmo.dinger.multi.entity
 * @date 2023/4/11 15:26
 */
public class MultiDingerHandlerDefinition{

    /**
     * dingerType + DingerConstant.SPOT_SEPERATOR + GLOABL_KEY or
     * dingerType + dingerConfigHandler classpath
     *
     * @author ZhangYiHao
     * @date 2023/4/14 10:27
     */
    private String key;
    private String dingerConfigHandlerClassName;
    private String algorithmBeanName;
    private DingerType dingerType;
    private Class<? extends DingerConfigHandler> dingerConfigHandler;

    public MultiDingerHandlerDefinition(String key, String dingerConfigHandlerClassName, DingerType dingerType, Class<? extends DingerConfigHandler> dingerConfigHandler) {
        this.key = key;
        this.dingerConfigHandlerClassName = dingerConfigHandlerClassName;
        this.dingerType = dingerType;
        this.dingerConfigHandler = dingerConfigHandler;
    }
    public MultiDingerHandlerDefinition setAlgorithmBeanName(String algorithmBeanName) {
        this.algorithmBeanName = algorithmBeanName;
        return this;
    }

    public String getAlgorithmBeanName() {
        return key + SPOT_SEPERATOR + algorithmBeanName;
    }

    public String getKey() {
        return key;
    }

    public MultiDingerHandlerDefinition setKey(String key) {
        this.key = key;
        return this;
    }

    public String getDingerConfigHandlerClassName() {
        return dingerConfigHandlerClassName;
    }

    public MultiDingerHandlerDefinition setDingerConfigHandlerClassName(String dingerConfigHandlerClassName) {
        this.dingerConfigHandlerClassName = dingerConfigHandlerClassName;
        return this;
    }

    public DingerType getDingerType() {
        return dingerType;
    }

    public MultiDingerHandlerDefinition setDingerType(DingerType dingerType) {
        this.dingerType = dingerType;
        return this;
    }

    public Class<? extends DingerConfigHandler> getDingerConfigHandler() {
        return dingerConfigHandler;
    }

    public MultiDingerHandlerDefinition setDingerConfigHandler(Class<? extends DingerConfigHandler> dingerConfigHandler) {
        this.dingerConfigHandler = dingerConfigHandler;
        return this;
    }
}

    
    