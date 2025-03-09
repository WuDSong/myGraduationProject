package cn.magic.exception;
//业务全局异常类
public class BusinessException extends RuntimeException{//继承自RuntimeException运行时异常
    private Integer code; // 错误码
    private String msg;  // 错误信息
    public BusinessException(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
}