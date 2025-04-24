package cn.magic.web.image.controller;

import cn.magic.utils.ResultVo;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {
    //图片上传的路径
    @Value("${web.upload-path}")
    private String webUploadpath;
    @RequestMapping("/uploadImage")
    public ResultVo uploadImage(@RequestParam("file") MultipartFile file){
        //todo:需要检查是否为图片类型，前端不可信
        String Url = "";
        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取文件扩展名
        String fileExtenionName = fileName.substring(fileName.indexOf("."));
        //新的文件名
        String newName = UUID.randomUUID().toString()+fileExtenionName;
        String path = webUploadpath;
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.mkdirs();
            //设置权限
            fileDir.setWritable(true);
        }
        File targetFile = new File(path,newName);
        try{
            file.transferTo(targetFile); //保存文件
            Url = "/" + targetFile.getName();
        }catch (Exception e){
            return  null;
        }
        return ResultVo.success("成功", "/image" + Url);//返回CDN地址
    }
    //审核图片
    @RequestMapping("/checkImage")
    public boolean checkImage(@RequestParam("file") MultipartFile file) throws IOException {
        File imageFile = File.createTempFile("upload", ".tmp");//创建一个临时文件，文件名以 upload 开头，扩展名是 .tmp。
        file.transferTo(imageFile);//磁盘占用
        try{
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);//加载 OpenCV 的本地库
            Mat image = Imgcodecs.imread(imageFile.getPath());
            // 示例：检测肤色区域比例（需完善逻辑）
            Mat hsv = new Mat();
            Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

            Core.inRange(hsv, new Scalar(0, 50, 50), new Scalar(20, 255, 255), hsv);
            double ratio = Core.countNonZero(hsv) / (double)(hsv.rows() * hsv.cols());

            return ratio < 0.3; // 假设阈值判断
        }finally {
            if (imageFile != null && imageFile.exists()) {
                imageFile.delete(); // 确保最终删除
                System.out.println("---一次图片检测完成");
            }
        }

    }
}
