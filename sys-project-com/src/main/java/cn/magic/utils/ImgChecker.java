package cn.magic.utils;

import io.micrometer.common.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class ImgChecker {
    //只是验证是否是图片的URL
    public static boolean isValidImageUrl(String url) {
        if (StringUtils.isBlank(url)) return false;

        // 1. 格式验证
        if (!url.matches("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$")) {
            return false;
        }

        // 2. 域名白名单验证（示例配置）
        List<String> allowedDomains = Arrays.asList(
                "oss.example.com",
                "cdn.yourplatform.com",
                "thirdparty-cdn.com",
                "good.com",
                "localhost"
        );

        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (!allowedDomains.contains(host)) {
                return false;
            }
        } catch (URISyntaxException e) {
            return false;
        }

        // 3. 扩展名过滤
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".webp"};
        String lowerUrl = url.toLowerCase();
        return Arrays.stream(allowedExtensions).anyMatch(lowerUrl::endsWith);
    }

    public static void main(String[] args) {
        System.out.println(isValidImageUrl("http://evil.com/exploit.jpg"));
        System.out.println(isValidImageUrl("ftp://x.com/1.png"));
        System.out.println(isValidImageUrl("https://good.com/1.html"));

        System.out.println(isValidImageUrl("https://oss.example.com/image.jpg"));
        System.out.println(isValidImageUrl("https://cdn.yourplatform.com/picture.jpeg"));
        System.out.println(isValidImageUrl("https://thirdparty-cdn.com/photo.png"));
        System.out.println(isValidImageUrl("https://good.com/image.webp"));
        //
        System.out.println(isValidImageUrl("http://localhost:12345/image/default-board-icon.png"));
    }
}
