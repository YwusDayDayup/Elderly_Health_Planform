
package com.example.config.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestLogFilter extends OncePerRequestFilter {

  public static final String ERROR_SUMMARY_ATTR = "request.error.summary";
  private static final Logger log = LoggerFactory.getLogger(RequestLogFilter.class);

  public static void markError(HttpServletRequest request, String summary) {
    if (request != null && summary != null && !summary.isBlank()) {
      request.setAttribute(ERROR_SUMMARY_ATTR, summary);
    }
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long startNanos = System.nanoTime();
    String traceId = UUID.randomUUID().toString().replace("-", "");
    MDC.put("traceId", traceId);
    try {
      filterChain.doFilter(request, response);
    } finally {
      long costMs = (System.nanoTime() - startNanos) / 1_000_000;
      String uri = request.getRequestURI();
      if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
        uri = uri + "?" + request.getQueryString();
      }
      int status = response.getStatus();
      Object marked = request.getAttribute(ERROR_SUMMARY_ATTR);
      if (marked != null) {
        if (status >= 500) {
          log.error("{} {} -> {} ({} ms) | {}", request.getMethod(), uri, status, costMs, marked);
        } else {
          log.warn("{} {} -> {} ({} ms) | {}", request.getMethod(), uri, status, costMs, marked);
        }
      } else if (status >= 500) {
        log.error("{} {} -> {} ({} ms) | 核心原因=请求失败", request.getMethod(), uri, status, costMs);
      } else if (status >= 400) {
        log.warn("{} {} -> {} ({} ms) | 核心原因=请求失败", request.getMethod(), uri, status, costMs);
      } else {
        log.info("{} {} -> {} ({} ms)", request.getMethod(), uri, status, costMs);
      }
      MDC.remove("traceId");
    }
  }
}
