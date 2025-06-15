package cn.magic.web.upload.controller;

import cn.magic.utils.ResultVo;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    //图片参数
    @Value("${web.image.upload-path}")
    private String imageUploadPath;
    @Value("${web.image.max-size}")
    private DataSize imageMaxSize;
    // 视频相关的参数
    @Value("${web.video.max-size}")
    private DataSize videoMaxSize;
    @Value("${web.video.allowed-extensions}")
    private String[] allowedVideoExtensions;
    @Value("${web.video.upload-path}")
    private String videoUploadPath;
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @RequestMapping("/uploadImage")
    public ResultVo uploadImage(@RequestParam("file") MultipartFile file) {
        //检测是否符合大小
        if (file.getSize() > imageMaxSize.toBytes()) {
            return ResultVo.error("图片不能超过10MB");
        }
        String Url = "";
        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取文件扩展名
        String fileExtenionName = fileName.substring(fileName.indexOf("."));
        //新的文件名
        String newName = UUID.randomUUID().toString() + fileExtenionName;
        // 保存路径
        String path = imageUploadPath;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
            //设置权限
            fileDir.setWritable(true);
        }
        File targetFile = new File(path, newName);
        try {
            file.transferTo(targetFile); //保存文件
            Url = "/" + targetFile.getName();
        } catch (Exception e) {
            return ResultVo.error(String.valueOf(e));
        }
        return ResultVo.success("成功", "/image" + Url);//返回CDN地址
    }

    /**
     * 视频上传接口
     * @param file 上传的视频文件
     * @return 上传结果
     */
    @RequestMapping("/uploadVideo")
    public ResultVo uploadVideo(@RequestParam("file") MultipartFile file) {
        System.out.println("上传视频事件");
        // 检查文件是否为空
        if (file.isEmpty()) {
            return ResultVo.error("请选择要上传的视频文件");
        }
        // 检查文件大小
        if (file.getSize() > videoMaxSize.toBytes()) {
            return ResultVo.error("视频大小不能超过 " + videoMaxSize.toMegabytes() + "MB");
        }
        // 获取原始文件名并验证扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return ResultVo.error("无效的文件名");
        }
        //扩展名
        String fileExtensionName = StringUtils.getFilenameExtension(originalFilename);
        if (fileExtensionName == null || !isValidVideoExtension(fileExtensionName)) {
            return ResultVo.error("只允许上传MP4格式的视频");
        }
        String Url = "";
        //新的文件名
        String newVideoName = UUID.randomUUID().toString() +"."+ fileExtensionName;
        // 保存路径
        System.out.println("保存视频");
        String path = videoUploadPath;
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
            //设置权限
            fileDir.setWritable(true);
        }
        File targetFile = new File(path, newVideoName);
        try {
            file.transferTo(targetFile); //保存文件
            Url = "/" + targetFile.getName();
        } catch (Exception e) {
            return ResultVo.error(String.valueOf(e));
        }
        return ResultVo.success("成功", "/video" + Url);//返回CDN地址
    }
    /**
     * 验证文件扩展名是否为允许的视频格式
     * @param extension 文件扩展名
     * @return 是否有效
     */
    private boolean isValidVideoExtension(String extension) {
        if (extension == null) return false;
        String lowerCaseExt = extension.toLowerCase();
        return Arrays.stream(allowedVideoExtensions)
                .map(String::toLowerCase)
                .anyMatch(ext -> ext.equals("." + lowerCaseExt) || ext.equals(lowerCaseExt));
    }

    //审核图片(测试函数，将废弃)
    @RequestMapping("/checkImage")
    public boolean checkImage(@RequestParam("file") MultipartFile file) throws IOException {
        File imageFile = File.createTempFile("upload", ".tmp");//创建一个临时文件，文件名以 upload 开头，扩展名是 .tmp。
        file.transferTo(imageFile);//磁盘占用
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);//加载 OpenCV 的本地库
            Mat image = Imgcodecs.imread(imageFile.getPath());
            // 示例：检测肤色区域比例（需完善逻辑）
            Mat hsv = new Mat();
            Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

            Core.inRange(hsv, new Scalar(0, 50, 50), new Scalar(20, 255, 255), hsv);
            double ratio = Core.countNonZero(hsv) / (double) (hsv.rows() * hsv.cols());

            return ratio < 0.3; // 假设阈值判断
        } finally {
            if (imageFile != null && imageFile.exists()) {
                imageFile.delete(); // 确保最终删除
                System.out.println("---一次图片检测完成");
            }
        }

    }
}
