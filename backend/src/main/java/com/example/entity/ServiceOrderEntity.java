package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@TableName("t_service_order")
@Getter
@Setter
public class ServiceOrderEntity extends BaseEntity {

  private String orderNo;
  private Long elderUserId;
  private Long familyUserId;
  private Long staffUserId;
  private Long serviceProjectId;
  private String serviceName;
  private String categoryName;
  private LocalDateTime serviceTime;
  private String serviceAddress;
  private String contactName;
  private String contactPhone;
  private String remark;
  private BigDecimal amount;
  private String payStatus;
  private String orderStatus;
  private LocalDateTime paymentTime;
  private LocalDateTime acceptTime;
  private LocalDateTime startTime;
  private LocalDateTime finishTime;
  private Integer rating;
  private String ratingContent;
}
