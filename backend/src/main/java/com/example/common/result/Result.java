
package com.example.common.result;

import java.io.Serializable;

public class Result<T> implements Serializable {

  private boolean success;
  private String code;
  private String message;
  private T data;

  
  public static <T> Result<T> ok(T data) {
    Result<T> r = new Result<>();
    r.success = true;
    r.code = "0";
    r.message = "操作成功";
    r.data = data;
    return r;
  }

  
  public static <T> Result<T> fail(String code, String message) {
    Result<T> r = new Result<>();
    r.success = false;
    r.code = code;
    r.message = message;
    r.data = null;
    return r;
  }

  
  public boolean isSuccess() {
    return success;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public T getData() {
    return data;
  }
}
