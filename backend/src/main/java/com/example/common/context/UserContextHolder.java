
package com.example.common.context;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.example.common.exception.BizException;

public class UserContextHolder {

  private static final String BEARER_PREFIX = "Bearer ";

  public static Long requireUserId() {
    try {
      return StpUtil.getLoginIdAsLong();
    } catch (Exception ignored) {
      Long userId = tryResolveUserIdFromAuthHeader();
      if (userId != null) {
        return userId;
      }
      throw new BizException(BizException.Code.UNAUTHORIZED, "未登录或登录已过期");
    }
  }

  
  private static Long tryResolveUserIdFromAuthHeader() {
    try {
      String auth = SaHolder.getRequest().getHeader("Authorization");
      if (auth == null || auth.isBlank()) {
        auth = SaHolder.getRequest().getHeader("authorization");
      }
      if (auth == null || auth.isBlank()) {
        return null;
      }

      String token = auth.trim();
      if (token.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
        token = token.substring(BEARER_PREFIX.length()).trim();
      }
      if (token.isEmpty()) {
        return null;
      }

      Object loginId = StpUtil.getLoginIdByToken(token);
      if (loginId == null) {
        return null;
      }
      if (loginId instanceof Number number) {
        return number.longValue();
      }
      return Long.parseLong(loginId.toString());
    } catch (Exception ignored) {
      return null;
    }
  }
}
