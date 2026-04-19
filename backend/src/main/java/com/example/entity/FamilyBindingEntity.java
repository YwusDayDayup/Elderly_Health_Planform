package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("t_family_binding")
@Getter
@Setter
public class FamilyBindingEntity extends BaseEntity {

  private Long elderUserId;
  private Long familyUserId;
  private String relationLabel;
  private String status;
  private Integer healthAccess;
  private Integer locationAccess;
  private String remark;
}
