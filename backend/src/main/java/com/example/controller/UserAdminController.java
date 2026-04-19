
package com.example.controller;

import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.common.web.DtoCrudController;
import com.example.dto.UserDTO;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user")
public class UserAdminController extends DtoCrudController<UserDTO.AdminUserVO, UserDTO.AdminUserDetailVO,
    UserDTO.AdminCreateReq, UserDTO.AdminUpdateReq> {

  private final UserService userService;

  public UserAdminController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/page")
  public Result<PageResult<UserDTO.AdminUserVO>> pagePost(@RequestBody UserDTO.AdminPageReq req) {
    return Result.ok(userService.adminPage(req));
  }

  @Override
  @GetMapping
  public Result<PageResult<UserDTO.AdminUserVO>> page(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer status,
      @RequestParam(required = false) String role) {
    UserDTO.AdminPageReq req = new UserDTO.AdminPageReq(pageNo, pageSize, keyword, status, role);
    return Result.ok(userService.adminPage(req));
  }

  @Override
  public Result<UserDTO.AdminUserDetailVO> detail(@PathVariable Long id) {
    return Result.ok(userService.adminDetail(id));
  }

  @Override
  public Result<Void> create(@RequestBody UserDTO.AdminCreateReq req) {
    userService.adminCreate(req);
    return Result.ok(null);
  }

  @Override
  public Result<Void> update(@PathVariable Long id, @RequestBody UserDTO.AdminUpdateReq req) {
    userService.adminUpdate(id, req);
    return Result.ok(null);
  }

  @Override
  public Result<Void> delete(@PathVariable Long id) {
    userService.adminDelete(id);
    return Result.ok(null);
  }
}
