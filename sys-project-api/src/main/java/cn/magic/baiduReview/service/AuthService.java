package cn.magic.baiduReview.service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * 获取token类
 */
@Component  // 添加 Spring 组件注解
public class AuthService {
    private static String cachedAccessToken;
    private static long expirationTime;
    private static final Object lock = new Object();  // 同步锁

    /**
     * 检查 access_token 是否过期
     * @return 如果过期返回 true，否则返回 false
     */
    private static boolean isTokenExpired() {
        return cachedAccessToken == null || System.currentTimeMillis() >= expirationTime;
    }

    /**
     * 获取权限token（改造为实例方法）
     */
    public String getAuth() {  // 移除 static
        String clientId = "DG22CX7cUsNWI9yZWTE0SRaJ";
        String clientSecret = "i0FhJ2mZ6aUgO52wfuU1zxiOZsPvi2xS";
        synchronized (lock) {  // 同步块保证线程安全
            if (isTokenExpired()) {
                System.out.println("重新获取 access_token");
                cachedAccessToken = getAuth(clientId, clientSecret);
            }
        }
        return cachedAccessToken;
    }

    // 其他代码保持原样（移除 static）
    public String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            long expiresIn = jsonObject.getLong("expires_in");
            // 计算过期时间
            expirationTime = System.currentTimeMillis() + (expiresIn * 1000);
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;

    }

    public static void main(String[] args) {
        // 测试代码需调整为通过 Spring 容器调用
    }
}