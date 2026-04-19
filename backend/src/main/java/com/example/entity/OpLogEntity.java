package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("t_op_log")
@Getter
@Setter
public class OpLogEntity extends BaseEntity {

  private Long userId;
  private String username;
  private String traceId;
  private String action;
  private String method;
  private String path;
  private String reqJson;
  private String respJson;
  private Integer success;
  private String errorMsg;
  private Long costMs;
}
