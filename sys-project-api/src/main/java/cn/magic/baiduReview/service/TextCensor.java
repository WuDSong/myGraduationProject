
package cn.magic.baiduReview.service;

import cn.magic.baiduReview.utils.HttpUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.concurrent.CompletableFuture;


/**
 * 文本审核接口
 */
@Component  // 添加 Spring 组件注解
public class TextCensor {

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
    private AuthService authService;
    @Async("taskExecutor")  // 添加异步注解
    public CompletableFuture<Boolean> TextCensor(String str) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined";
        try {
            String param = "text=" + URLEncoder.encode(str, "utf-8");
            String accessToken = authService.getAuth();
            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);//1.合规，2.不合规，3.疑似，4.审核失败
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

    }
}