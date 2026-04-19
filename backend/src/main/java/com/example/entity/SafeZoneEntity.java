package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@TableName("t_safe_zone")
@Getter
@Setter
public class SafeZoneEntity extends BaseEntity {

  private Long userId;
  private BigDecimal centerLatitude;
  private BigDecimal centerLongitude;
  private Integer radiusMeters;
  private String address;
}
