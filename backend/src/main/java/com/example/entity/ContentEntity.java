package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@TableName("t_content")
@Getter
@Setter
public class ContentEntity extends BaseEntity {

  private String contentType;
  private String title;
  private String summary;
  private String content;
  private String coverUrl;
  private Integer status;
  private Long authorId;
  private String authorName;
  private LocalDateTime publishedAt;
  private Long viewCount;
}
