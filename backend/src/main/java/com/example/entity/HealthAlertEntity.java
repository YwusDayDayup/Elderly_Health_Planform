package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("t_health_alert")
@Getter
@Setter
public class HealthAlertEntity extends BaseEntity {

  private Long userId;
  private Long recordId;
  private String alertType;
  private String alertLevel;
  private String title;
  private String content;
  private String status;
}
