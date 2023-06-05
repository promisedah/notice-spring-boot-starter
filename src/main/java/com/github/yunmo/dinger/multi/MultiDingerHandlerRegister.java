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
package com.github.yunmo.dinger.multi;

import com.github.yunmo.dinger.core.DingerConfig;
import com.github.yunmo.dinger.core.entity.enums.ExceptionEnum;
import com.github.yunmo.dinger.exception.DingerException;
import com.github.yunmo.dinger.exception.MultiDingerRegisterException;
import com.github.yunmo.dinger.multi.algorithm.AlgorithmHandler;
import com.github.yunmo.dinger.multi.entity.MultiDingerConfig;
import com.github.yunmo.dinger.multi.entity.MultiDingerHandlerDefinition;
import com.github.yunmo.dinger.utils.DingerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.util.*;

import static com.github.yunmo.dinger.core.entity.enums.ExceptionEnum.ALGORITHM_FIELD_INJECT_FAILED;

/**
 * MultiDingerHandlerRegister
 *
 * @author ZhangYiHao
 * @date 2023/4/17 18:43
 */
@AutoConfigureBefore(MultiDingerAlgorithmInjectRegister.class)
public class MultiDingerHandlerRegister implements ApplicationContextAware, InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(MultiDingerHandlerRegister.class);
    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (MultiDingerHandlerRegister.applicationContext == null) {
            MultiDingerHandlerRegister.applicationContext = applicationContext;
        } else {
            log.warn("applicationContext is not null.");
        }
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        if (MultiDingerScannerRegistrar.MULTIDINGER_HANDLER_DEFINITION_MAP.isEmpty()) {
            // 当前算法处理容器为空, MultiDinger失效。 可能由于所有的实现处理器中无注入属性信息
            log.info("MultiDingerHandler Container is Empty.");
            return;
        }

        try {
            multiDingerWithInjectAttributeHandler();
        } catch (DingerException ex) {
            throw new MultiDingerRegisterException(ex.getPairs(), ex.getMessage());
        } catch (Exception ex) {
            throw new DingerException(ex, ExceptionEnum.UNKNOWN);
        }
    }

    /**
     * 处理MultiDinger中存在注入字段情况
     */
    private void multiDingerWithInjectAttributeHandler() {
        boolean debugEnabled = log.isDebugEnabled();

        Set<Map.Entry<String, MultiDingerHandlerDefinition>> entries =
                MultiDingerScannerRegistrar.MULTIDINGER_HANDLER_DEFINITION_MAP.entrySet();

        for (Map.Entry<String, MultiDingerHandlerDefinition> entry : entries) {
            // v.getKey() is dingerClassName or MultiDingerConfigContainer#GLOABL_KEY
            String beanName = entry.getKey();
            MultiDingerHandlerDefinition v = entry.getValue();
            Class<? extends DingerConfigHandler> dingerConfigHandler = v.getDingerConfigHandler();
            // 从spring容器中拿到算法处理对象
            DingerConfigHandler bean = applicationContext.getBean(beanName, DingerConfigHandler.class);
            // 字段对象注入
            handlerFieldInjection(dingerConfigHandler, bean);
            List<DingerConfig> dingerConfigs = bean.dingerConfigs();
            // 设置类型
            for (int i = 0; i < dingerConfigs.size(); i++) {
                DingerConfig dingerConfig = dingerConfigs.get(i);
                dingerConfig.setDingerType(v.getDingerType());
                if (DingerUtils.isEmpty(dingerConfig.getTokenId())) {
                    throw new DingerException(ExceptionEnum.DINGER_CONFIG_HANDLER_EXCEPTION, v.getDingerConfigHandlerClassName(), i);
                }
            }
            // 获取算法
            Class<? extends AlgorithmHandler> algorithmHandlerClass = bean.algorithmHandler();

            // 注册算法
            registryAlgorithmBeanDefinition(v.getKey(),algorithmHandlerClass,dingerConfigs);
        }

        MultiDingerScannerRegistrar.MULTIDINGER_HANDLER_DEFINITION_MAP.clear();
    }

    private void registryAlgorithmBeanDefinition(String key, Class<? extends AlgorithmHandler> algorithmHandlerClass, List<DingerConfig> dingerConfigs) {
        // 目前只支持属性方式注入, 可优化支持构造器注入和set注入
        long injectionCnt = Arrays.stream(algorithmHandlerClass.getDeclaredFields()).filter(e -> e.isAnnotationPresent(Autowired.class)).count();
        // 如果无注入对象，直接反射算法处理器对象
        MultiDingerScannerRegistrar.AnalysisEnum mode = MultiDingerScannerRegistrar.AnalysisEnum.REFLECT;
        // 如果无需注入属性，直接采用反射进行实例化并注册到容器
        if (injectionCnt == 0) {
            // create algorithm instance
            AlgorithmHandler algorithmHandler = DingerUtils.newInstance(algorithmHandlerClass);
            // v.getKey() is dingerClassName or MultiDingerConfigContainer#GLOABL_KEY
            MultiDingerConfigContainer.INSTANCE.put(
                    key, new MultiDingerConfig(algorithmHandler, dingerConfigs)
            );
        } else {
            // 这里没经过详细验证可能会有问题
            AlgorithmHandler algorithmHandler = BeanUtils.instantiateClass(algorithmHandlerClass);
            // 设置属性
            algorithmFieldInjection(algorithmHandlerClass,algorithmHandler);
            // v.getKey() is dingerClassName or MultiDingerConfigContainer#GLOABL_KEY
            MultiDingerConfigContainer.INSTANCE.put(
                    key, new MultiDingerConfig(algorithmHandler, dingerConfigs)
            );
            mode = MultiDingerScannerRegistrar.AnalysisEnum.SPRING_CONTAINER;
        }
        if (!MultiDingerConfigContainer.INSTANCE.isEmpty()) {
            MultiDingerProperty.multiDinger = true;
        }
        if (log.isDebugEnabled()) {
            log.debug("beanName={}, algorithm={} analysis through mode {}.",
                    key, algorithmHandlerClass.getSimpleName(), mode);
        }
    }

    /**
     * 处理算法中属性注入
     *
     * @param algorithm
     *          algorithm
     * @param algorithmHandler
     *          algorithmHandler
     */
    private void handlerFieldInjection(Class<? extends DingerConfigHandler> algorithm, DingerConfigHandler algorithmHandler) {
        String algorithmSimpleName = algorithm.getSimpleName();
        OK:
        for (Field declaredField : algorithm.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Autowired.class)) {
                String fieldBeanName = declaredField.getName();
                if (declaredField.isAnnotationPresent(Qualifier.class)) {
                    Qualifier qualifier = declaredField.getAnnotation(Qualifier.class);
                    if (DingerUtils.isNotEmpty(qualifier.value())) {
                        fieldBeanName = qualifier.value();
                    }
                }

                // 从spring容器上下文中获取属性对应的实例
                String[] actualBeanNames = applicationContext.getBeanNamesForType(declaredField.getType());
                int length = actualBeanNames.length;
                if (length == 1) {
                    fieldBeanName = actualBeanNames[0];
                } else if (length > 1) {
                    final String fbn = fieldBeanName;
                    long count = Arrays.stream(actualBeanNames).filter(e -> Objects.equals(e, fbn)).count();
                    if (count == 0) {
                        throw new DingerException(
                                ExceptionEnum.ALGORITHM_FIELD_INSTANCE_NOT_MATCH, algorithmSimpleName, fieldBeanName
                        );
                    }
                } else {
                    throw new DingerException(
                            ExceptionEnum.ALGORITHM_FIELD_INSTANCE_NOT_EXISTS, algorithmSimpleName, fieldBeanName
                    );
                }

                try {
                    declaredField.setAccessible(true);
                    declaredField.set(algorithmHandler, applicationContext.getBean(fieldBeanName));
                } catch (IllegalAccessException e) {
                    throw new DingerException(
                            ALGORITHM_FIELD_INJECT_FAILED, algorithmSimpleName, fieldBeanName
                    );
                }

            }
        }
    }
    /**
     * 处理算法中属性注入
     *
     * @param algorithm
     *          algorithm
     * @param algorithmHandler
     *          algorithmHandler
     */
    private void algorithmFieldInjection(Class<? extends AlgorithmHandler> algorithm, AlgorithmHandler algorithmHandler) {
        String algorithmSimpleName = algorithm.getSimpleName();
        OK:
        for (Field declaredField : algorithm.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(Autowired.class)) {
                String fieldBeanName = declaredField.getName();
                if (declaredField.isAnnotationPresent(Qualifier.class)) {
                    Qualifier qualifier = declaredField.getAnnotation(Qualifier.class);
                    if (DingerUtils.isNotEmpty(qualifier.value())) {
                        fieldBeanName = qualifier.value();
                    }
                }

                // 从spring容器上下文中获取属性对应的实例
                String[] actualBeanNames = applicationContext.getBeanNamesForType(declaredField.getType());
                int length = actualBeanNames.length;
                if (length == 1) {
                    fieldBeanName = actualBeanNames[0];
                } else if (length > 1) {
                    final String fbn = fieldBeanName;
                    long count = Arrays.stream(actualBeanNames).filter(e -> Objects.equals(e, fbn)).count();
                    if (count == 0) {
                        throw new DingerException(
                                ExceptionEnum.ALGORITHM_FIELD_INSTANCE_NOT_MATCH, algorithmSimpleName, fieldBeanName
                        );
                    }
                } else {
                    throw new DingerException(
                            ExceptionEnum.ALGORITHM_FIELD_INSTANCE_NOT_EXISTS, algorithmSimpleName, fieldBeanName
                    );
                }

                try {
                    declaredField.setAccessible(true);
                    declaredField.set(algorithmHandler, applicationContext.getBean(fieldBeanName));
                } catch (IllegalAccessException e) {
                    throw new DingerException(
                            ALGORITHM_FIELD_INJECT_FAILED, algorithmSimpleName, fieldBeanName
                    );
                }

            }
        }
    }
    protected static void clear() {
        MultiDingerHandlerRegister.applicationContext = null;
    }

}