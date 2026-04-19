
package com.example.config.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import com.example.common.exception.BizException;
import com.example.common.result.Result;
import com.example.config.web.RequestLogFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<Result<Void>> handleNoResourceFound(NoResourceFoundException e, HttpServletRequest request) {
    String msg = "接口不存在: " + e.getResourcePath();
    markError(request, msg, e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(Result.fail(BizException.Code.NOT_FOUND.getCode(), msg));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Result<Void>> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
      HttpServletRequest request) {
    String msg = e.getBindingResult().getAllErrors().isEmpty()
        ? BizException.Code.BAD_REQUEST.getMessage()
        : e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    markError(request, msg, e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Result.fail(BizException.Code.BAD_REQUEST.getCode(), msg));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Result<Void>> handleConstraintViolation(ConstraintViolationException e,
      HttpServletRequest request) {
    markError(request, e.getMessage(), e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(Result.fail(BizException.Code.BAD_REQUEST.getCode(), e.getMessage()));
  }

  @ExceptionHandler(NotLoginException.class)
  public ResponseEntity<Result<Void>> handleNotLogin(NotLoginException e, HttpServletRequest request) {
    markError(request, "未登录或登录已过期", e);
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(Result.fail(BizException.Code.UNAUTHORIZED.getCode(), "未登录或登录已过期"));
  }

  @ExceptionHandler(NotRoleException.class)
  public ResponseEntity<Result<Void>> handleNotRole(NotRoleException e, HttpServletRequest request) {
    markError(request, "无权限", e);
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(Result.fail(BizException.Code.FORBIDDEN.getCode(), "无权限"));
  }

  @ExceptionHandler(BizException.class)
  public ResponseEntity<Result<Void>> handleBiz(BizException e, HttpServletRequest request) {
    markError(request, e.getMessage(), e);
    return ResponseEntity.status(resolveHttpStatus(e.getCode()))
        .body(Result.fail(e.getCode(), e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Result<Void>> handleUnknown(Exception e, HttpServletRequest request) {
    markError(request, BizException.Code.INTERNAL_ERROR.getMessage(), e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Result.fail(BizException.Code.INTERNAL_ERROR.getCode(), BizException.Code.INTERNAL_ERROR.getMessage()));
  }

  private HttpStatus resolveHttpStatus(String code) {
    if (BizException.Code.BAD_REQUEST.getCode().equals(code)) {
      return HttpStatus.BAD_REQUEST;
    }
    if (BizException.Code.UNAUTHORIZED.getCode().equals(code)) {
      return HttpStatus.UNAUTHORIZED;
    }
    if (BizException.Code.FORBIDDEN.getCode().equals(code)) {
      return HttpStatus.FORBIDDEN;
    }
    if (BizException.Code.NOT_FOUND.getCode().equals(code)) {
      return HttpStatus.NOT_FOUND;
    }
    if (BizException.Code.INTERNAL_ERROR.getCode().equals(code)) {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    return HttpStatus.BAD_REQUEST;
  }

  private void markError(HttpServletRequest request, String coreReason, Throwable throwable) {
    String subReason = findSubReason(throwable);
    String summary = "核心原因=" + safe(coreReason);
    if (subReason != null && !subReason.isBlank()) {
      summary = summary + "; 子原因=" + subReason;
    }
    RequestLogFilter.markError(request, summary);
  }

  private String findSubReason(Throwable throwable) {
    if (throwable == null) {
      return null;
    }
    Throwable root = throwable;
    while (root.getCause() != null && root.getCause() != root) {
      root = root.getCause();
    }
    if (root == throwable) {
      return null;
    }
    String msg = root.getMessage();
    if (msg == null || msg.isBlank()) {
      return root.getClass().getSimpleName();
    }
    return msg;
  }

  private String safe(String value) {
    if (value == null || value.isBlank()) {
      return "未知错误";
    }
    return value;
  }
}
