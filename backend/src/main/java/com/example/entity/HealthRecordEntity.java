package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@TableName("t_health_record")
@Getter
@Setter
public class HealthRecordEntity extends BaseEntity {

  private Long userId;
  private LocalDate recordDate;
  private BigDecimal weight;
  private Integer systolicBp;
  private Integer diastolicBp;
  private BigDecimal bloodSugar;
  private BigDecimal bloodLipid;
  private Integer pulse;
  private BigDecimal bodyTemp;
  private Integer checkInFlag;
  private String note;
  private Integer abnormalFlag;
}
