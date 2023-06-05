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

import java.util.ArrayList;
import java.util.List;

/**
 * Dinger请求体
 *
 * @author Jaemon
 * @since 1.0
 */
public class DingerRequest {
    /**
     * 消息内容
     */
    private String content;
    /**
     * 标题(dingtalk-markdown)
     */
    private String title;
    /**
     * 艾特成员信息
     */
    private List<String> phones = new ArrayList<>();
    /**
     * 艾特成员
     */
    private boolean atAll = false;
    /**
     * 通知级别
     */
    private NoticeLevel level = NoticeLevel.INFO;

    private DingerRequest(String content) {
        this.content = content;
    }

    private DingerRequest(String content, NoticeLevel level) {
        this.content = content;
        this.level = level;
    }

    private DingerRequest(String content, String title) {
        this(content);
        this.title = title;
    }

    private DingerRequest(String content, String title, NoticeLevel level) {
        this(content, title);
        this.level = level;
    }

    private DingerRequest(String content, List<String> phones) {
        this.content = content;
        this.phones = phones;
    }

    private DingerRequest(String content, List<String> phones, NoticeLevel level) {
        this(content, level);
        this.phones = phones;
    }

    private DingerRequest(String content, boolean atAll) {
        this.content = content;
        this.atAll = atAll;
    }

    private DingerRequest(String content, boolean atAll, NoticeLevel level) {
        this(content, atAll);
        this.level = level;
    }

    private DingerRequest(String content, String title, List<String> phones) {
        this(content, title);
        this.phones = phones;
    }

    private DingerRequest(String content, String title, List<String> phones, NoticeLevel level) {
        this(content, title, phones);
        this.level = level;
    }

    private DingerRequest(String content, String title, boolean atAll) {
        this(content, atAll);
        this.title = title;
    }

    /**
     * @param content 内容
     * @param title   标题
     * @param atAll   @所有人
     * @param level   级别{@link NoticeLevel}
     * @return {@link DingerRequest}
     * @throws
     * @author ZhangYiHao
     * @date 2023/4/13 14:41
     **/
    private DingerRequest(String content, String title, boolean atAll, NoticeLevel level) {
        this(content, atAll, level);
        this.title = title;
    }


    /**
     * 构建Dinger请求体
     *
     * @param content 具体消息内容
     * @return Dinger请求体实例
     */
    public static DingerRequest request(String content) {
        return new DingerRequest(content);
    }

    /**
     * @param content 内容
     * @param level   级别{@link NoticeLevel}
     * @return {@link DingerRequest}
     * @throws
     * @author ZhangYiHao
     * @date 2023/4/13 14:41
     **/
    public static DingerRequest request(String content, NoticeLevel level) {
        return new DingerRequest(content, level);
    }

    /**
     * 构建Dinger请求体
     *
     * @param content 具体消息内容
     * @param title   标题， 仅限钉钉markdown消息使用
     * @return Dinger请求体实例
     */
    public static DingerRequest request(String content, String title) {
        return new DingerRequest(content, title);
    }

    /**
     * @param content 内容
     * @param title   标题
     * @param level   级别{@link NoticeLevel}
     * @return {@link DingerRequest}
     * @throws
     * @author ZhangYiHao
     * @date 2023/4/13 14:41
     **/
    public static DingerRequest request(String content, String title, NoticeLevel level) {
        return new DingerRequest(content, title, level);
    }

    /**
     * 构建Dinger请求体
     *
     * @param content 具体消息内容
     * @param phones  需要@的成员列表
     * @return Dinger请求体实例
     */
    public static DingerRequest request(String content, List<String> phones) {
        return new DingerRequest(content, phones);
    }

    /**
     * @param content 内容
     * @param level   级别{@link NoticeLevel}
     * @return {@link DingerRequest}
     * @throws
     * @author ZhangYiHao
     * @date 2023/4/13 14:41
     **/
    public static DingerRequest request(String content, List<String> phones, NoticeLevel level) {
        return new DingerRequest(content, phones, level);
    }

    /**
     * 构建Dinger请求体
     *
     * @param content 具体消息内容
     * @param atAll   是否需要@全部成员
     * @return Dinger请求体实例
     */
    public static DingerRequest request(String content, boolean atAll) {
        return new DingerRequest(content, atAll);
    }

    /**
     * @param content 内容
     * @param atAll   @所有人
     * @param level   级别{@link NoticeLevel}
     * @return {@link DingerRequest}
     * @throws
     * @author ZhangYiHao
     * @date 2023/4/13 14:41
     **/
    public static DingerRequest request(String content, boolean atAll, NoticeLevel level) {
        return new DingerRequest(content, atAll, level);
    }

    /**
     * 构建Dinger请求体
     *
     * @param content 具体消息内容
     * @param title   标题， 仅限钉钉markdown消息使用
     * @param phones  需要@的成员列表
     * @return Dinger请求体实例
     */
    public static DingerRequest request(String content, String title, List<String> phones) {
        return new DingerRequest(content, title, phones);
    }

    /**
     * @param content 内容
     * @param title   标题
     * @param level   级别{@link NoticeLevel}
     * @return {@link DingerRequest}
     * @throws
     * @author ZhangYiHao
     * @date 2023/4/13 14:41
     **/
    public static DingerRequest request(String content, String title, List<String> phones, NoticeLevel level) {
        return new DingerRequest(content, title, phones, level);
    }


    /**
     * 构建Dinger请求体
     *
     * @param content 具体消息内容
     * @param title   标题， 仅限钉钉markdown消息使用
     * @param atAll   是否需要@全部成员
     * @return Dinger请求体实例
     */
    public static DingerRequest request(String content, String title, boolean atAll) {
        return new DingerRequest(content, title, atAll);
    }

    /**
     * @param content 内容
     * @param title   标题
     * @param atAll   @所有人
     * @param level   级别{@link NoticeLevel}
     * @return {@link DingerRequest}
     * @author ZhangYiHao
     * @date 2023/4/13 14:41
     **/
    public static DingerRequest request(String content, String title, boolean atAll, NoticeLevel level) {
        return new DingerRequest(content, title, atAll, level);
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public boolean isAtAll() {
        return atAll;
    }

    public void setAtAll(boolean atAll) {
        this.atAll = atAll;
    }

    public NoticeLevel getNoticeLevel() {
        return level;
    }

    public DingerRequest setNoticeLevel(NoticeLevel level) {
        this.level = level;
        return this;
    }
}