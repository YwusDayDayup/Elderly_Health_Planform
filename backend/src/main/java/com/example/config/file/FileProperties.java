
package com.example.config.file;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.file")
public class FileProperties {

  
  private String localDir = "./uploads";

  
  private String publicPrefix = "/uploads";

  public String getLocalDir() {
    return localDir;
  }

  public void setLocalDir(String localDir) {
    this.localDir = localDir;
  }

  public String getPublicPrefix() {
    return publicPrefix;
  }

  public void setPublicPrefix(String publicPrefix) {
    this.publicPrefix = publicPrefix;
  }
}
