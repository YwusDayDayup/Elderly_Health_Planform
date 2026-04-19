package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("t_external_call_log")
@Getter
@Setter
public class ExternalCallLogEntity extends BaseEntity {

  private String providerType;
  private String action;
  private String bizType;
  private String bizId;
  private String reqJson;
  private String respJson;
  private Integer success;
}
