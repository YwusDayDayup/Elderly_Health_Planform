
package com.example.config;

import com.example.config.file.FileProperties;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final FileProperties fileProperties;

  public WebConfig(FileProperties fileProperties) {
    this.fileProperties = fileProperties;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOriginPatterns("*")
        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(false);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String prefix = fileProperties.getPublicPrefix();
    if (prefix == null || prefix.isBlank()) {
      prefix = "/uploads";
    }
    if (!prefix.startsWith("/")) {
      prefix = "/" + prefix;
    }
    String localDir = fileProperties.getLocalDir();
    if (localDir == null || localDir.isBlank()) {
      localDir = "./uploads";
    }
    Path abs = Paths.get(localDir).toAbsolutePath().normalize();
    String location = abs.toUri().toString();
    if (!location.endsWith("/")) {
      location = location + "/";
    }
    registry.addResourceHandler(prefix + "/**")
        .addResourceLocations(location);
  }
}
