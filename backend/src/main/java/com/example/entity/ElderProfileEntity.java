package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("t_elder_profile")
@Getter
@Setter
public class ElderProfileEntity extends BaseEntity {

  private Long userId;
  private String realName;
  private String idCard;
  private String emergencyContactName;
  private String emergencyContactPhone;
  private String address;
  private String healthNotes;
  private Integer familyHealthAuthorized;
  private Integer familyLocationAuthorized;
}
