
package com.example.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.context.UserContextHolder;
import com.example.common.exception.BizException;
import com.example.config.file.FileProperties;
import com.example.dto.FileDTO;
import com.example.entity.FileEntity;
import com.example.mapper.FileMapper;
import com.example.mapper.FileStructMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService extends ServiceImpl<FileMapper, FileEntity> {

  private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final FileStructMapper fileStructMapper;
  private final FileProperties fileProperties;

  public FileService(FileMapper fileMapper, FileStructMapper fileStructMapper,
      FileProperties fileProperties) {
    this.fileStructMapper = fileStructMapper;
    this.fileProperties = fileProperties;
  }

  
  @Transactional
  public FileDTO.UploadResp upload(String bizType, MultipartFile file) {
    Long userId = UserContextHolder.requireUserId();
    if (file == null || file.isEmpty()) {
      throw new BizException(BizException.Code.BAD_REQUEST, "文件不能为空");
    }

    String originalName = file.getOriginalFilename();
    String safeOriginalName = StringUtils.hasText(originalName) ? originalName : "file";
    if (safeOriginalName == null) {
      safeOriginalName = "file";
    }

    String ext = "";
    int dot = safeOriginalName.lastIndexOf('.');
    if (dot > -1 && dot < safeOriginalName.length() - 1) {
      ext = safeOriginalName.substring(dot);
    }

    String day = LocalDate.now().format(DAY_FMT);
    String storedName = UUID.randomUUID().toString().replace("-", "") + ext;

    Path baseDir = Paths.get(fileProperties.getLocalDir()).toAbsolutePath().normalize();
    Path dir = baseDir.resolve(day);
    try {
      Files.createDirectories(dir);
    } catch (IOException e) {
      throw new BizException(BizException.Code.INTERNAL_ERROR, "创建上传目录失败");
    }

    Path dest = dir.resolve(storedName).toAbsolutePath().normalize();
    try {
      file.transferTo(dest);
    } catch (IOException e) {
      throw new BizException(BizException.Code.INTERNAL_ERROR, "保存文件失败");
    }

    String prefix = fileProperties.getPublicPrefix();
    if (!StringUtils.hasText(prefix)) {
      prefix = "/uploads";
    }
    if (!prefix.startsWith("/")) {
      prefix = "/" + prefix;
    }
    String url = prefix + "/" + day + "/" + storedName;

    FileEntity m = fileStructMapper.toEntity(
        userId,
        bizType,
        safeOriginalName,
        storedName,
        file.getContentType(),
        file.getSize(),
        dest.toString(),
        url);
    save(m);

    return fileStructMapper.toUploadResp(m);
  }
}
