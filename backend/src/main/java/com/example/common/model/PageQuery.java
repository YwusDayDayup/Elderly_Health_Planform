package com.example.common.model;

public interface PageQuery {
  Long pageNo();
  Long pageSize();

  static long pageNo(Long pageNo) {
    long resolved = pageNo == null ? 1L : pageNo;
    return Math.max(1L, resolved);
  }

  static long pageSize(Long pageSize) {
    long resolved = pageSize == null ? 10L : pageSize;
    if (resolved < 1L) {
      return 1L;
    }
    return Math.min(resolved, 100L);
  }
}
