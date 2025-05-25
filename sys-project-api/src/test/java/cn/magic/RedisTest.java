package cn.magic;

import cn.magic.redis.service.RedisService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisService redisService;

    @Test
    public void testRedis() {
        String key = "testKey";
        String value = "Hello Redis!";

        redisService.setValue(key, value);
        String result = (String) redisService.getValue(key);

        assert result.equals(value);
    }
}
