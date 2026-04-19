package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@TableName("t_location_record")
@Getter
@Setter
public class LocationRecordEntity extends BaseEntity {

  private Long userId;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private String address;
  private Integer withinZone;
}
