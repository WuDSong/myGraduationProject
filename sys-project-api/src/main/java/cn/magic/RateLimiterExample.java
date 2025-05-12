package cn.magic;

import com.google.common.util.concurrent.RateLimiter;

public class RateLimiterExample {
    public static void main(String[] args) {
        RateLimiter limiter = RateLimiter.create(1.0); // 每秒不超过1个请求
        for (int i = 1; i <= 10; i++) {
            limiter.acquire(); // 尝试获取令牌
            System.out.println("call execute.." + i);
        }
    }
}