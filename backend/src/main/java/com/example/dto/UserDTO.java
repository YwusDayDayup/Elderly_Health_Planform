
package com.example.dto;

import com.example.common.model.PageQuery;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

public class UserDTO {

  
  public interface UserBaseView {

    Long id();

    String username();

    String nickname();

    String avatarUrl();

    String email();

    String phone();

    Integer gender();

    String region();

    String city();
  }

  
  public record LoginReq(
      String username,
      String password
  ) {
  }

  
  public record RegisterReq(
      String username,
      String password,
      String role,
      String nickname,
      String email,
      String phone,
      Integer gender,
      LocalDate birthday,
      String bio,
      String signature,
      String region,
      String city
  ) {
  }

  
  public record LoginResp(String token) {
  }

  
  public record ProfileVO(
      Long id,
      String username,
      String nickname,
      String avatarUrl,
      String email,
      String phone,
      Integer gender,
      LocalDate birthday,
      String bio,
      String signature,
      String region,
      String city
  ) {
  }

  
  public record UpdateProfileReq(
      String nickname,
      String avatarUrl,
      String email,
      String phone,
      Integer gender,
      LocalDate birthday,
      String bio,
      String signature,
      String region,
      String city
  ) {
  }

  
  public record UpdatePasswordReq(
      String oldPassword,
      String newPassword
  ) {
  }

  
  public record RolesVO(String role) {
  }

  
  public record AdminUserVO(Long id, String username, String nickname, String avatarUrl,
                            String email, String phone, Integer gender, String region,
                            String city, Integer status) implements UserBaseView {
  }

  
  public record AdminUserDetailVO(
      Long id,
      String username,
      String nickname,
      String avatarUrl,
      String email,
      String phone,
      Integer gender,
      LocalDate birthday,
      String bio,
      String signature,
      String region,
      String city,
      Integer status,
      String role
  ) implements UserBaseView {
  }

  
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record AdminUpdateReq(
      String nickname,
      String avatarUrl,
      String email,
      String phone,
      Integer gender,
      LocalDate birthday,
      String bio,
      String signature,
      String region,
      String city,
      Integer status,
      String role
  ) {
  }

  
  public record AdminCreateReq(
      String username,
      String password,
      String nickname,
      String email,
      String phone,
      Integer gender,
      LocalDate birthday,
      String bio,
      String signature,
      String region,
      String city,
      Integer status,
      String role
  ) {
  }

  
  public record AdminPageReq(
      Long pageNo,
      Long pageSize,
      String keyword,
      Integer status,
      String role
  ) implements PageQuery {
  }
}
