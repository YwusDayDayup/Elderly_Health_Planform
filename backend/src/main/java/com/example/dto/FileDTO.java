
package com.example.dto;

public class FileDTO {

  public record UploadResp(Long id, String url, String originalName, Long size) {
  }
}
