package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@TableName("t_community_post")
@Getter
@Setter
public class CommunityPostEntity extends BaseEntity {

  private Long authorId;
  private String authorName;
  private String title;
  private String content;
  private String category;
  private String postStatus;
  private String auditStatus;
  private Integer commentCount;
  private Integer likeCount;
  private Integer viewCount;
  private String lastAction;
  private LocalDateTime lastActionTime;
}
