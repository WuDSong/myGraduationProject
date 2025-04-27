package cn.magic.web.image.controller;

import cn.magic.utils.ResultVo;
import cn.magic.web.post.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import com.google.code.kaptcha.impl.DefaultKaptcha;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    @Autowired
    private DefaultKaptcha defaultKaptcha;
    private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);
    //生成验证码
    @GetMapping
    public ResultVo imageCode(HttpServletRequest request) {
        //生成验证码
        String text = defaultKaptcha.createText();
        //验证码存到session
        HttpSession session = request.getSession();
        session.setAttribute("code", text);
        logger.info("Session ID: {}, 存储的验证码: {}", session.getId(), text); // 输出 Session ID 和验证码
        //生成图片,转换为base64
        BufferedImage bufferedImage = defaultKaptcha.createImage(text);
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            Base64.Encoder encoder = Base64.getEncoder();
            //BASE64Encoder encoder = new BASE64Encoder();
            String base64 = encoder.encodeToString(outputStream.toByteArray());
            // String base64 = encoder.encode(outputStream.toByteArray());
            String captchaBase64 = "data:image/jpeg;base64," +
                    base64.replaceAll("\r\n", "");
            ResultVo result = new ResultVo("生成成功", 200, captchaBase64);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
