
package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@TableName("t_file")
@Getter
@Setter
public class FileEntity extends BaseEntity {

  private Long userId;
  private String bizType;
  private String originalName;
  private String storedName;
  private String contentType;
  private Long size;
  private String filePath;
  private String fileUrl;
}
