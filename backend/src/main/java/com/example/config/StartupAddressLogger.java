package com.example.config;

import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class StartupAddressLogger implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger log = LoggerFactory.getLogger(StartupAddressLogger.class);

  private final Environment environment;

  public StartupAddressLogger(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    if (!(event.getApplicationContext() instanceof WebServerApplicationContext webCtx)) {
      return;
    }
    int port = webCtx.getWebServer().getPort();
    String contextPath = environment.getProperty("server.servlet.context-path", "");
    if (contextPath == null || contextPath.isBlank()) {
      contextPath = "/";
    }
    if (!contextPath.startsWith("/")) {
      contextPath = "/" + contextPath;
    }
    String scheme = Boolean.TRUE.equals(environment.getProperty("server.ssl.enabled", Boolean.class))
        ? "https" : "http";

    String localUrl = scheme + "://localhost:" + port + contextPath;
    String hostAddress = resolveHostAddress();
    String externalUrl = scheme + "://" + hostAddress + ":" + port + contextPath;

    log.info("应用已启动: local={} external={}", localUrl, externalUrl);
  }

  private String resolveHostAddress() {
    try {
      return InetAddress.getLocalHost().getHostAddress();
    } catch (Exception ignored) {
      return "127.0.0.1";
    }
  }
}
