package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("t_staff_profile")
@Getter
@Setter
public class StaffProfileEntity extends BaseEntity {

  private Long userId;
  private String realName;
  private String certificateNo;
  private String specialty;
  private Integer serviceRadiusKm;
  private String intro;
  private String auditStatus;
}
