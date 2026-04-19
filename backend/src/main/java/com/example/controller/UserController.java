
package com.example.controller;

import com.example.common.result.Result;
import com.example.dto.UserDTO;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  
  @PostMapping("/api/public/login")
  public Result<UserDTO.LoginResp> login(@RequestBody UserDTO.LoginReq req) {
    return Result.ok(userService.login(req));
  }

  
  @PostMapping("/api/public/register")
  public Result<UserDTO.LoginResp> register(@RequestBody UserDTO.RegisterReq req) {
    return Result.ok(userService.register(req));
  }

  
  @PostMapping("/api/public/logout")
  public Result<Void> logout(jakarta.servlet.http.HttpServletRequest request) {
    String auth = request.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7).trim();
      if (!token.isEmpty()) {
        cn.dev33.satoken.stp.StpUtil.logoutByTokenValue(token);
      }
    }
    return Result.ok(null);
  }

  
  @GetMapping("/api/me/profile")
  public Result<UserDTO.ProfileVO> profile() {
    return Result.ok(userService.profile());
  }

  
  @PutMapping("/api/me/profile")
  public Result<Void> updateProfile(@RequestBody UserDTO.UpdateProfileReq req) {
    userService.updateProfile(req);
    return Result.ok(null);
  }

  
  @PutMapping("/api/me/password")
  public Result<Void> updatePassword(@RequestBody UserDTO.UpdatePasswordReq req) {
    userService.updatePassword(req);
    return Result.ok(null);
  }

  
  @GetMapping("/api/me/roles")
  public Result<UserDTO.RolesVO> myRoles() {
    return Result.ok(userService.myRoles());
  }
}
