
package com.example.controller;

import com.example.common.result.Result;
import com.example.dto.FileDTO;
import com.example.service.FileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

  private final FileService fileService;

  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  
  @PostMapping("/api/me/files")
  public Result<FileDTO.UploadResp> upload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "bizType", required = false) String bizType) {
    return Result.ok(fileService.upload(bizType, file));
  }
}
