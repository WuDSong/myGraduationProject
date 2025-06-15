package cn.magic.baiduReview.service;

import cn.magic.baiduReview.utils.GsonUtils;
import cn.magic.baiduReview.utils.HttpUtil;
import com.baidu.aip.util.Base64Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * 人脸检测与属性分析
 */
public class FaceDetect {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    /**
     * @param filePath 本地图片路径
     */

    public static String faceDetect(String filePath) throws IOException {
        // api请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
        // 图片是url图片
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

        // 图片是本地文件路径
//        byte[] imgData = FileUtil.readFileByBytes(filePath);
//        String imgStr = Base64Util.encode(imgData);

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("image", imgStr);
//            map.put("face_field", "faceshape,facetype"); //选项：默认只返回face_token、人脸框、概率和旋转角度
            map.put("image_type", "BASE64"); // 上传图片的类型BASE64

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthServiceTest.getAuth();

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
//        String filePath = "D:\\图片\\压缩.jpg";
        String filePath = "http://localhost:12345/image/8bfbef78-0c06-4dfc-b531-781de8e235f1.jpg";
        FaceDetect.faceDetect(filePath);
    }
}
