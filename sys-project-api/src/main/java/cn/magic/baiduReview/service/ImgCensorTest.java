package cn.magic.baiduReview.service;

import cn.magic.baiduReview.utils.HttpUtil;
import com.baidu.aip.util.Base64Util;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.CompletableFuture;

/*
  图像审核接口
 */

//String filePath = "D:\\图片\\壁纸\\mmexport1727609776705.jpg";

/**
 * 图像审核接口
 */
@Component  // 添加 Spring 组件注解，@Component 和 @Service 都是用来标识类为 Spring Bean 的注解，但它们有不同的语义目的。
// @Service 本质上是 @Component 的子类注解，二者在 Spring 容器中的注册行为完全一致。唯一的区别是语义上的明确性。
public class ImgCensorTest {
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

    //    1. 降低调用频率；2. 使用缓存减少请求；3. 申请更高的QPS配额；4. 使用负载均衡分散请求。
    // 添加异步注解
    /**
     * @param filePath 本地图片路径
     */
    @Async("taskExecutor")
//    @Async("censorExecutor") // 强制使用单线程执行器
    public CompletableFuture<Boolean> ImgCensor(String filePath) { //http://localhost:12345/image/11ff457d-3507-4e67-9c89-168bcec94337.png
//        return CompletableFuture.supplyAsync(() -> {},censorExecutor);

        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/img_censor/v2/user_defined";
        try {
            // 由于 API 不支持直接使用本地网络地址作为imgUrl参数值（因为 API 要求图像 URL 地址需能被公网访问 ），
            // 所以需要先将图片读取并进行 Base64 编码，再使用编码后的字符串调用 API。
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
//            String filePath = "D:\\图片\\壁纸\\mmexport1727609776705.jpg";
//            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
            String param = "image=" + imgParam;
            String accessToken = authService.getAuth();  // 通过实例调用
            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            // 被@Async标记后，它会在单独的线程中执行，主线程不会等待它完成。
            // 所以，如果用户在调用异步方法后立即处理返回值，可能会遇到问题，因为此时异步方法可能还没执行完毕，返回值可能还没准备好。

            // 1. 直接在异步线程中处理结果，要确保原子性，高耦合
            // 2. 调用者轮询，会阻塞线程，失去异步优势。
            // 3. 事件异步处理
            JSONObject jsonObject = new JSONObject(result);
            Integer conclusionType = jsonObject.getInt("conclusionType");
            if(conclusionType==1||conclusionType==3){ //审核结果类型，可取值1、2、3、4，分别代表1：合规，2：不合规，3：疑似，4：审核失败
                return CompletableFuture.completedFuture(true); //合格
            }
            else
                return CompletableFuture.completedFuture(false);//不合格
        } catch (Exception e) {
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
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