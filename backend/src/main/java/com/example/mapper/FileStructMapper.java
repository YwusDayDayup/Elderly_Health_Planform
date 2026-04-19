
package com.example.mapper;

import com.example.dto.FileDTO;
import com.example.entity.FileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface FileStructMapper {

  @Mappings({
      @Mapping(target = "id", ignore = true),
      @Mapping(target = "createTime", ignore = true),
      @Mapping(target = "updateTime", ignore = true),
      @Mapping(target = "userId", source = "userId"),
      @Mapping(target = "bizType", source = "bizType"),
      @Mapping(target = "originalName", source = "originalName"),
      @Mapping(target = "storedName", source = "storedName"),
      @Mapping(target = "contentType", source = "contentType"),
      @Mapping(target = "size", source = "size"),
      @Mapping(target = "filePath", source = "filePath"),
      @Mapping(target = "fileUrl", source = "fileUrl")
  })
  FileEntity toEntity(Long userId, String bizType, String originalName, String storedName,
      String contentType, Long size, String filePath, String fileUrl);

  @Mapping(target = "url", source = "fileUrl")
  FileDTO.UploadResp toUploadResp(FileEntity entity);
}
