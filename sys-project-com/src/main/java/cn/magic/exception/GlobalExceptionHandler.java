package cn.magic.exception;
import cn.magic.utils.ResultVo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
@ControllerAdvice //控制器增强
public class GlobalExceptionHandler { // 拦截所有异常
    //自定义业务异常拦截
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public ResultVo businessException(BusinessException e) {
        return ResultVo.error(e.getMsg(), e.getCode(), e.getMsg());
    }
    //未知的运行时异常拦截
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ResponseBody
    public ResultVo notFount(RuntimeException e) {
        return ResultVo.error(e.getMessage(), 500,
                "后端接口报错！！！");
    }
}