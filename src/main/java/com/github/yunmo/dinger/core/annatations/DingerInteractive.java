package com.github.yunmo.dinger.core.annatations;

import com.github.yunmo.dinger.bytetalk.entity.ByteInteractive;
import com.github.yunmo.dinger.core.entity.enums.AsyncExecuteType;

import java.lang.annotation.*;

/**
 * DingerInteractive
 *
 * @author ZhangYiHao
 * @version v1.0
 * @package com.github.yunmo.dinger.core.annatations
 * @date 2024/10/16 17:59
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface DingerInteractive {

    /**
     * atAll. either atAll or phones
     *
     * @return whether &#064;all members
     */
    boolean atAll() default false;

    /**
     * tokenId
     *
     * @return token info
     */
    DingerTokenId tokenId() default @DingerTokenId("");

    /**
     * asyncExecute
     *
     * @return async execute send
     */
    AsyncExecuteType asyncExecute() default AsyncExecuteType.NONE;

    Class<?> clazz = ByteInteractive.Card.class;
}

