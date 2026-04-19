
package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.constant.GlobalConstants;
import com.example.entity.UserRoleEntity;
import com.example.mapper.UserRoleMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserRoleDomainService {

  private final UserRoleMapper userRoleMapper;

  public UserRoleDomainService(UserRoleMapper userRoleMapper) {
    this.userRoleMapper = userRoleMapper;
  }

  
  @Transactional(readOnly = true)
  public String roleByUserId(Long userId) {
    List<String> roles = userRoleMapper.selectRolesByUserId(userId);
    if (roles == null || roles.isEmpty()) {
      return GlobalConstants.ROLE_ELDER;
    }
    if (roles.contains(GlobalConstants.ROLE_ADMIN)) {
      return GlobalConstants.ROLE_ADMIN;
    }
    if (roles.contains(GlobalConstants.ROLE_STAFF)) {
      return GlobalConstants.ROLE_STAFF;
    }
    if (roles.contains(GlobalConstants.ROLE_FAMILY)) {
      return GlobalConstants.ROLE_FAMILY;
    }
    if (roles.contains("USER")) {
      return GlobalConstants.ROLE_ELDER;
    }
    return roles.get(0);
  }

  
  @Transactional(readOnly = true)
  public List<Long> userIdsByRole(String role) {
    if (!StringUtils.hasText(role)) {
      return List.of();
    }
    return userRoleMapper.selectList(new LambdaQueryWrapper<UserRoleEntity>()
            .eq(UserRoleEntity::getRole, role))
        .stream()
        .map(UserRoleEntity::getUserId)
        .toList();
  }

  
  @Transactional
  public void replaceRole(Long userId, String role) {
    userRoleMapper.deleteByUserId(userId);
    if (!StringUtils.hasText(role)) {
      role = GlobalConstants.ROLE_ELDER;
    }
    if ("USER".equals(role)) {
      role = GlobalConstants.ROLE_ELDER;
    }
    UserRoleEntity entity = new UserRoleEntity();
    entity.setUserId(userId);
    entity.setRole(role);
    userRoleMapper.insert(entity);
  }

  
  @Transactional
  public void clearRoles(Long userId) {
    userRoleMapper.deleteByUserId(userId);
  }
}
