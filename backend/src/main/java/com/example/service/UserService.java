
package com.example.service;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.dev33.satoken.stp.StpUtil;
import com.example.common.constant.GlobalConstants;
import com.example.common.context.UserContextHolder;
import com.example.common.exception.BizException;
import com.example.common.model.PageQuery;
import com.example.common.result.PageResult;
import com.example.common.util.PasswordUtils;
import com.example.dto.UserDTO;
import com.example.entity.UserEntity;
import com.example.mapper.UserMapper;
import com.example.mapper.UserRoleMapper;
import com.example.mapper.UserStructMapper;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class UserService extends ServiceImpl<UserMapper, UserEntity> {

  private static final String USER_NOT_FOUND_MSG = "用户不存在";
  private static final String USERNAME_EXISTS_MSG = "用户名已存在";
  private static final String USERNAME_OR_PASSWORD_ERROR_MSG = "用户名或密码错误";

  private final UserMapper userMapper;
  private final UserStructMapper userStructMapper;
  private final UserRoleDomainService userRoleDomainService;
  private final UserRoleMapper userRoleMapper;

  public UserService(UserMapper userMapper, UserStructMapper userStructMapper,
      UserRoleDomainService userRoleDomainService, UserRoleMapper userRoleMapper) {
    this.userMapper = userMapper;
    this.userStructMapper = userStructMapper;
    this.userRoleDomainService = userRoleDomainService;
    this.userRoleMapper = userRoleMapper;
  }

  
  @Transactional
  public UserDTO.LoginResp login(UserDTO.LoginReq req) {
    UserEntity user = findByUsername(req.username());
    if (user == null || !PasswordUtils.matches(req.password(), user.getPasswordHash())) {
      throw new BizException(BizException.Code.UNAUTHORIZED, USERNAME_OR_PASSWORD_ERROR_MSG);
    }
    if (user.getStatus() != null && user.getStatus() == GlobalConstants.STATUS_DISABLED) {
      throw new BizException(BizException.Code.FORBIDDEN, "账号已被禁用");
    }
    String role = userRoleDomainService.roleByUserId(user.getId());
    StpUtil.login(user.getId());
    StpUtil.getSession().set("username", user.getUsername());
    StpUtil.getSession().set("role", role);
    return new UserDTO.LoginResp(StpUtil.getTokenValue());
  }

  
  @Transactional
  public UserDTO.LoginResp register(UserDTO.RegisterReq req) {
    assertUsernameAvailable(req.username());
    UserEntity user = userStructMapper.toEntity(req);
    user.setPasswordHash(PasswordUtils.hash(req.password()));
    user.setGender(req.gender() == null ? 0 : req.gender());
    user.setStatus(GlobalConstants.STATUS_ENABLED);
    save(user);

    String role = StringUtils.hasText(req.role()) ? req.role() : GlobalConstants.ROLE_ELDER;
    userRoleDomainService.replaceRole(user.getId(), role);

    StpUtil.login(user.getId());
    StpUtil.getSession().set("username", user.getUsername());
    StpUtil.getSession().set("role", role);
    return new UserDTO.LoginResp(StpUtil.getTokenValue());
  }

  
  @Transactional(readOnly = true)
  public UserDTO.ProfileVO profile() {
    Long userId = UserContextHolder.requireUserId();
    UserEntity user = requireUserById(userId);
    return userStructMapper.toProfileVO(user);
  }

  
  @Transactional
  public void updateProfile(UserDTO.UpdateProfileReq req) {
    Long userId = UserContextHolder.requireUserId();
    UserEntity user = requireUserById(userId);
    userStructMapper.updateByProfile(req, user);
    updateById(user);
  }

  
  @Transactional
  public void updatePassword(UserDTO.UpdatePasswordReq req) {
    Long userId = UserContextHolder.requireUserId();
    UserEntity user = requireUserById(userId);
    if (!PasswordUtils.matches(req.oldPassword(), user.getPasswordHash())) {
      throw new BizException(BizException.Code.BIZ_ERROR, "旧密码不正确");
    }
    user.setPasswordHash(PasswordUtils.hash(req.newPassword()));
    updateById(user);
  }

  
  @Transactional(readOnly = true)
  public UserDTO.RolesVO myRoles() {
    Long userId = UserContextHolder.requireUserId();
    return new UserDTO.RolesVO(userRoleDomainService.roleByUserId(userId));
  }

  
  @Transactional
  public void adminCreate(UserDTO.AdminCreateReq req) {
    assertUsernameAvailable(req.username());
    UserEntity user = userStructMapper.toEntity(req);
    user.setPasswordHash(PasswordUtils.hash(req.password()));
    user.setGender(req.gender() == null ? 0 : req.gender());
    user.setStatus(req.status() == null ? GlobalConstants.STATUS_ENABLED : req.status());
    save(user);

    userRoleDomainService.replaceRole(user.getId(),
        StringUtils.hasText(req.role()) ? req.role() : GlobalConstants.ROLE_ELDER);
  }

  
  @Transactional(readOnly = true)
  public PageResult<UserDTO.AdminUserVO> adminPage(UserDTO.AdminPageReq req) {
    LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<UserEntity>()
        .like(StringUtils.hasText(req.keyword()), UserEntity::getUsername, req.keyword())
        .eq(req.status() != null, UserEntity::getStatus, req.status())
        .orderByDesc(UserEntity::getId);
    if (StringUtils.hasText(req.role())) {
      List<Long> userIds = userRoleMapper.selectUserIdsByRole(req.role());
      if (userIds.isEmpty()) {
        return PageResult.of(0L, List.of());
      }
      wrapper.in(UserEntity::getId, userIds);
    }
    IPage<UserEntity> page = new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize()));
    IPage<UserEntity> result = userMapper.selectPage(page, wrapper);
    List<UserDTO.AdminUserVO> list = userStructMapper.toAdminUserVOList(result.getRecords());
    return PageResult.of(result.getTotal(), list);
  }

  
  @Transactional(readOnly = true)
  public UserDTO.AdminUserDetailVO adminDetail(Long id) {
    UserEntity user = requireUserById(id);
    String role = userRoleDomainService.roleByUserId(id);
    return userStructMapper.toAdminUserDetailVO(user, role);
  }

  
  @Transactional
  public void adminUpdate(Long id, UserDTO.AdminUpdateReq req) {
    UserEntity user = requireUserById(id);
    userStructMapper.updateByAdmin(req, user);
    updateById(user);

    if (req.role() != null) {
      userRoleDomainService.replaceRole(id, req.role());
    }
  }

  
  @Transactional
  public void adminDelete(Long id) {
    userRoleDomainService.clearRoles(id);
    removeById(id);
  }

  
  private UserEntity findByUsername(String username) {
    return userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
        .eq(UserEntity::getUsername, username));
  }

  
  private void assertUsernameAvailable(String username) {
    if (findByUsername(username) != null) {
      throw new BizException(BizException.Code.BIZ_ERROR, USERNAME_EXISTS_MSG);
    }
  }

  
  private UserEntity requireUserById(Long userId) {
    UserEntity user = getById(userId);
    Assert.notNull(user, () -> new BizException(BizException.Code.NOT_FOUND, USER_NOT_FOUND_MSG));
    return user;
  }

}
