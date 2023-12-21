# Notice

# deploy
>  mvn deploy -Dgpg.skip=true -s /usr/local/apache-maven-3.8.4/conf/settings_itiaoling_now.xml

## 通知介绍

支持企业微信、钉钉等通知..

支持@成员通知..

支持多机器人通知..

支持同步、异步通知..

### 全局默认通知

- 默认配置
    - <font color = 'red'> 默认DingerSender暂不支持多机器人配置，仅支持默认机器人配置</font>

- 支持@成员通知
    - 企业微信目前仅支持TEXT类型通过手机号/userid @成员。（MARKDOWN类型消息企业微信暂不支持@成员

### 全局多机器人通知

#### 背景：

企业微信机器人通知频率受限 60次/min

叮叮机器人通知频率受限 20次/min。

由于高发情况下可能会出现单个机器人通知超频率受限无法发送通知，所以就有了多机器人配置。

#### 功能说明：

- 支持多机器人通知配置
- 支持分业务线通知配置
    - 接口级别定义（推荐
    - 代码级别注入（不推荐
- 支持全局开启多机器人配置
    - 配合接口级别定义使用
    - <font color = 'red'> 默认DingerSender暂不支持多机器人配置，仅支持默认机器人配置</font>
- 支持@成员通知
    - 企业微信目前仅支持TEXT类型通过手机号/userid @成员。（MARKDOWN类型消息企业微信暂不支持@成员

## 单机器人全局通知

此类型通知采用默认通知配置，不支持多机器人配置

1. 配置默认机器人信息

   ``` yaml
   notice:
   	# 默认通知配置必须
     default:
       enable: true
       project-id: ${spring.application.name}
       config:
        	# 配置开启企业微信通知
         we-talk:
           token-id: e74e267e-c0f3-4c06-8e7c-d8796c4bd0a9
   ```

2. 调用通知

   ``` java
        // ...
        @Autowired
        DingerSender dingerSender;
        
        public void send (){
          // 调用通知
          dingerSender.send(MessageSubType.TEXT, DingerRequest.request("EfNoticeWebApplication"+ LocalDateTime.now()));
        }  
        // ...
   ```

## 多机器人通知配置

### 全局多机器人

1. 配置多机器人信息

   ``` yaml
   notice:
   	# 默认通知配置必须
     default:
       enable: true
       project-id: ${spring.application.name}
       config:
         we-talk:
           token-id: e74e267e-c0f3-4c06-8e7c-d8796c4bd0a9
     multi:
       enable: true
       we-talk:
       	# 同步触发
         token-ids:
           - e3605089-7fcc-4eb1-a1a2-2b6c513d36d6
           - d03c94f9-b3f5-4d3b-965a-a5fe8526bd72
           - ee8e81b8-d6b3-4a2b-822b-8aa27df4805d
         # 异步
         # async-token-ids:
   			# 	- e3605089-7fcc-4eb1-a1a2-2b6c513d36d6
   ```

2. 启动类添加@EnableMultiDinger注解，并指定对应多机器人配置信息（采用轮训算法

   ``` java
   @SpringBootApplication
   @EnableMultiDinger(@MultiDinger(dinger = DingerType.WETALK,handler = WeTalkMultiHandler.class)) //全局开启企业微信多机器人通知配置
   public class EfNoticeWebApplication {
     // ...
   }
   ```

3. 定义通知接口

   ``` java
   public interface Sender {
     @DingerText(value = "恭喜用户${loginName}登录成功!")
     DingerResponse success(@Parameter("loginName") String userName);
   }
   ```

4. 调用

   ``` java
    // ...
    @Autowired
    private Sender sender;
    
    public void send (){
      sender.success("1");
      sender.success("2");
      sender.success("3");
      sender.success("4");
    }
    
    // ...
   ```

采用默认handler实现（三机器人配置+轮询算法）实现效果如下：

![image-20230417130620498](/Users/yunmo/Library/Application Support/typora-user-images/image-20230417130620498.png)

### 接口级别自定义

1. 配置多机器人信息

``` yaml
notice:
	# 默认通知配置必须
  default:
    enable: true
    project-id: ${spring.application.name}
    config:
      we-talk:
        token-id: e74e267e-c0f3-4c06-8e7c-d8796c4bd0a9
  multi:
    enable: true
    we-talk:
    	# 同步触发
      token-ids:
        - e3605089-7fcc-4eb1-a1a2-2b6c513d36d6
        - d03c94f9-b3f5-4d3b-965a-a5fe8526bd72
        - ee8e81b8-d6b3-4a2b-822b-8aa27df4805d
      # 异步
      # async-token-ids:
			# 	- e3605089-7fcc-4eb1-a1a2-2b6c513d36d6
```

2. 开启配置
    1. 启动类添加@EnableMultiDinger注解
    2. 增加包扫描配置

``` java
@SpringBootApplication
@EnableMultiDinger // 启用多机器人
@DingerScan(basePackages = {"com.itiaoling.ef.notice.*"}) // basePackages改为你的项目路径
public class EfNoticeWebApplication {
  // ...
}
```

3. 定义通知接口

``` java
// 指定类型以及处理实现
@MultiHandler(@MultiDinger(dinger = DingerType.WETALK,handler = WeTalkMultiHandler.class))
public interface Sender {
  @DingerText(value = "恭喜用户${loginName}登录成功!",phones = {"********"}) //phones 指定需要@的用户手机号
  DingerResponse success(@Parameter("loginName") String userName);
}
```

4. 调用

``` java
  // ...
  @Autowired
  private Sender sender;

  public void send (){
    sender.success("1");
    sender.success("2");
    sender.success("3");
    sender.success("4");
  }

// ...
```

采用默认handler实现@成员通知（三机器人配置+轮询算法）实现效果如下：

![image-20230417132820863](/Users/yunmo/Library/Application Support/typora-user-images/image-20230417132820863.png)