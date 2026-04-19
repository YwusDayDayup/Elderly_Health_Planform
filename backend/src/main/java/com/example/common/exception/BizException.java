
package com.example.common.exception;

public class BizException extends RuntimeException {

  public enum Code {
    OK("0", "操作成功"),
    BAD_REQUEST("400", "请求参数错误"),
    UNAUTHORIZED("401", "未授权"),
    FORBIDDEN("403", "拒绝访问"),
    NOT_FOUND("404", "资源不存在"),
    BIZ_ERROR("BIZ", "业务异常"),
    INTERNAL_ERROR("500", "系统内部异常");

    private final String code;
    private final String message;

    Code(String code, String message) {
      this.code = code;
      this.message = message;
    }

    public String getCode() {
      return code;
    }

    public String getMessage() {
      return message;
    }
  }

  private final String code;

  
  public BizException(Code errorCode) {
    super(errorCode.getMessage());
    this.code = errorCode.getCode();
  }

  
  public BizException(Code errorCode, String message) {
    super(message);
    this.code = errorCode.getCode();
  }

  
  public BizException(String code, String message) {
    super(message);
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
