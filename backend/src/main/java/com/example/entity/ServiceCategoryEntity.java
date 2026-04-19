package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("t_service_category")
@Getter
@Setter
public class ServiceCategoryEntity extends BaseEntity {

  private String name;
  private String code;
  private String description;
  private Integer sortNo;
  private Integer status;
}
