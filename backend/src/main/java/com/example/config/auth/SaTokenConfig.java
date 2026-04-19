package com.example.config.auth;

import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.example.common.constant.GlobalConstants;
import com.example.service.UserRoleDomainService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

  private final AuthWhitelistProperties authWhitelistProperties;
  private final UserRoleDomainService userRoleDomainService;

  public SaTokenConfig(AuthWhitelistProperties authWhitelistProperties,
      UserRoleDomainService userRoleDomainService) {
    this.authWhitelistProperties = authWhitelistProperties;
    this.userRoleDomainService = userRoleDomainService;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new SaInterceptor(handle -> {
      List<String> exclude = new ArrayList<>();
      if (authWhitelistProperties.getWhitelist() != null) {
        exclude.addAll(authWhitelistProperties.getWhitelist());
      }
      exclude.add("/error");
      exclude.add("/favicon.ico");
      exclude.add("/actuator/**");

      String[] paths = exclude.toArray(new String[0]);
      SaRouter.match("/api/admin/**", r -> {
        StpUtil.checkLogin();
        checkAdminRole();
      }).match("/api/statistics/**", r -> {
        StpUtil.checkLogin();
        checkAdminRole();
      }).match("/**").notMatch(paths).check(StpUtil::checkLogin);
    })).addPathPatterns("/**");
  }

  private void checkAdminRole() {
    long userId = StpUtil.getLoginIdAsLong();
    String role = userRoleDomainService.roleByUserId(userId);
    if (!GlobalConstants.ROLE_ADMIN.equals(role)) {
      throw new NotRoleException(GlobalConstants.ROLE_ADMIN, StpUtil.getLoginIdAsString());
    }
  }
}
