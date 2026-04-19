
package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@TableName("t_msg")
@Getter
@Setter
public class MsgEntity extends BaseEntity {

  private Long senderId;
  private Long receiverId;
  private String msgType;
  private String title;
  private String content;
  private String bizId;
  private Integer readFlag;
  private LocalDateTime readTime;
}
