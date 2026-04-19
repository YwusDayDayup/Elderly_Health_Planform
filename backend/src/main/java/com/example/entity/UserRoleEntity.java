
package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("t_user_role")
@Getter
@Setter
public class UserRoleEntity extends BaseEntity {

  private Long userId;
  private String role;
}
