package com.example.config.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.example.service.UserRoleDomainService;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StpInterfaceImpl implements StpInterface {

  private final UserRoleDomainService userRoleDomainService;

  public StpInterfaceImpl(UserRoleDomainService userRoleDomainService) {
    this.userRoleDomainService = userRoleDomainService;
  }

  @Override
  public List<String> getPermissionList(Object loginId, String loginType) {
    return Collections.emptyList();
  }

  @Override
  public List<String> getRoleList(Object loginId, String loginType) {
    List<String> roles = doGetRoleList(loginId);
    return roles;
  }

  private List<String> doGetRoleList(Object loginId) {
    try {
      String role = StpUtil.getSessionByLoginId(loginId).getString("role");
      if (role != null && !role.isBlank()) {
        return List.of(role);
      }
    } catch (Exception e) {
    }
    Long userId = parseUserId(loginId);
    if (userId != null) {
      String role = userRoleDomainService.roleByUserId(userId);
      if (role != null && !role.isBlank()) {
        return List.of(role);
      }
    }
    return Collections.emptyList();
  }

  private Long parseUserId(Object loginId) {
    if (loginId instanceof Number n) {
      return n.longValue();
    }
    if (loginId != null) {
      String s = loginId.toString();
      if (!s.isBlank()) {
        try {
          return Long.parseLong(s);
        } catch (NumberFormatException ignored) {
        }
      }
    }
    return null;
  }
}
