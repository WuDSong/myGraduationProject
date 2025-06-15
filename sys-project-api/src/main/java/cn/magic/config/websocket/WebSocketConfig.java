package cn.magic.config.websocket;


import cn.magic.web.webSocket.entity.SimpleAuthHandshakeInterceptor;
import cn.magic.web.webSocket.entity.WxAuthHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的内存消息代理，处理以"/topic"开头的消息  也是用户订阅
        config.enableSimpleBroker("/topic","/queue");// "/queue" 1-1
        // 客户端发送消息的前缀
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");//设置用户目标前缀
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点，客户端将通过这里连接 // 微信小程序使用原生WebSocket，不需要SockJS
        registry.addEndpoint("/websocket-endpoint").setAllowedOriginPatterns("*") // 允许跨域
                .addInterceptors(new SimpleAuthHandshakeInterceptor()); // 启用简单认证拦截器
//                .addInterceptors(new WxAuthHandshakeInterceptor()); // 添加认证拦截器
//        .withSockJS();
    }
    // WebSocketConfig.java
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(128 * 1024); // 128KB
    }
}
