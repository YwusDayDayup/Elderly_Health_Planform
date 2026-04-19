package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@TableName("t_service_project")
@Getter
@Setter
public class ServiceProjectEntity extends BaseEntity {

  private Long categoryId;
  private String name;
  private String description;
  private BigDecimal price;
  private String unit;
  private Integer status;
  private String coverUrl;
  private Long defaultStaffUserId;
  private Integer serviceDurationMinutes;
}
