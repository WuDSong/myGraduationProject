package cn.magic.baiduReview.service;

import cn.magic.baiduReview.utils.HttpUtil;
import com.baidu.aip.util.Base64Util;
import com.google.common.util.concurrent.RateLimiter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
  图像审核接口
 */

//String filePath = "D:\\图片\\壁纸\\mmexport1727609776705.jpg";

/**
 * 图像审核接口
 */
@Component  // 添加 Spring 组件注解，@Component 和 @Service 都是用来标识类为 Spring Bean 的注解，但它们有不同的语义目的。
// @Service 本质上是 @Component 的子类注解，二者在 Spring 容器中的注册行为完全一致。唯一的区别是语义上的明确性。
public class ImgCensor {
    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    @Autowired
    private AuthService authService;  // 注入 AuthService 实例

    //百度每每秒只能进行一次检测，那这里就不能异步多线程  1. 降低调用频率；2. 使用缓存减少请求；3. 申请更高的QPS配额；4. 使用负载均衡分散请求。
    private final ExecutorService censorExecutor = Executors.newSingleThreadExecutor();
//    RateLimiter是 Google Guava 库 中提供的一个用于限流（Rate Limiting） 的工具类
    private final RateLimiter rateLimiter = RateLimiter.create(1.0); // 每秒1个许可
    // 添加异步注解
    /**
     * @param filePath 本地图片路径
    */
//    @Async("taskExecutor")
    @Async("censorExecutor") // 强制使用单线程执行器
    public CompletableFuture<Boolean> ImgCensor(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // QPS 控制点
                rateLimiter.acquire(); // 阻塞直到获得许可
                // 原有审核逻辑
                String url = "https://aip.baidubce.com/rest/2.0/solution/v1/img_censor/v2/user_defined";
                // url图片 获取 转换
                URL urlObj = new URL(filePath);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                try (InputStream inputStream = urlObj.openStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
                byte[] imgData = output.toByteArray();
                String imgStr = Base64Util.encode(imgData);
                String imgParam = URLEncoder.encode(imgStr, "UTF-8");
                // 准备数据
                String param = "image=" + imgParam;
                String accessToken = authService.getAuth();
                String result = HttpUtil.post(url, accessToken, param);
                JSONObject jsonObject = new JSONObject(result);

                int conclusionType = jsonObject.getInt("conclusionType");
                return conclusionType == 1 || conclusionType == 3;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }, censorExecutor); // 关键：绑定到单线程执行器
    }

    public static void main(String[] args) {
        String filePath = "D:\\图片\\壁纸\\IMG_20240519_022342.jpg";
        //不合格的返回值
        //{
        //"conclusion":"不合规",
        //"log_id":17469780978820723,
        //"data":[{"msg":"存在卡通色情不合规",
        //         "conclusion":"不合规",
        //         "probability":0.9950225,
        //         "subType":1,
        //         "conclusionType":2,
        //         "type":1}],
        //"phoneRisk":{},
        //"isHitMd5":false,      //是否命中人审违规数据
        //"conclusionType":2     //审核结果类型，可取值1、2、3、4，分别代表1：合规，2：不合规，3：疑似，4：审核失败
        // }
//        ImgCensor.ImgCensor(filePath);
    }
}



//