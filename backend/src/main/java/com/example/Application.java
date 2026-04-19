
package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.example.config")
@MapperScan(
        value = "com.example.mapper",
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
public class Application {

  
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
