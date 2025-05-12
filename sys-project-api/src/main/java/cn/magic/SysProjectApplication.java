package cn.magic;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.opencv.core.Core.NATIVE_LIBRARY_NAME;
import static org.opencv.highgui.HighGui.*;
import static org.opencv.imgcodecs.Imgcodecs.imread;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class SysProjectApplication {
    public static void main( String[] args ) {

        SpringApplication.run(SysProjectApplication.class,args);
    }
    private static void test(){
        System.out.println("wds");
        System.out.println("Welcome to OpenCV " + Core.VERSION);
        System.loadLibrary(NATIVE_LIBRARY_NAME);
        //读取图片
        Mat imread = imread("E:\\Mycode\\MyGraduationProject\\image\\3615d15a-3e25-4240-865d-171feb6be8a8.jpg");
        //显示窗口
        imshow("demo",imread);
        //按任意键退出程序
        waitKey(0);
        //关闭所有窗口
        destroyAllWindows();
        //保存图片
        imwrite("./out/test1.png",imread);
    }
}
