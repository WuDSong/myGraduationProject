package cn.magic.web.webSocket.entity;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

// SimpleAuthHandshakeInterceptor.java
public class SimpleAuthHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        // 从查询参数获取userId
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String queryString = servletRequest.getServletRequest().getQueryString();

            if (queryString != null) {
                // 简单解析查询参数
                String[] params = queryString.split("&");
                for (String param : params) {
                    if (param.startsWith("userId=")) {
                        String userIdStr = param.substring(7);
                        try {
                            Long userId = Long.parseLong(userIdStr);
                            attributes.put("userId", userId);
                            System.out.println("WebSocket 用户认证成功: " + userId);
                            return true;
                        } catch (NumberFormatException e) {
                            System.err.println("无效的用户ID格式: " + userIdStr);
                        }
                    }
                }
            }
        }
        System.err.println("WebSocket 用户认证失败");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
    }
}
