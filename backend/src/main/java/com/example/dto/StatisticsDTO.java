
package com.example.dto;

import java.util.List;

public class StatisticsDTO {

  
  public static class NameCountRow {

    private String name;
    private Long count;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Long getCount() {
      return count;
    }

    public void setCount(Long count) {
      this.count = count;
    }
  }

  
  public static class DayCountRow {

    private String day;
    private Long count;

    public String getDay() {
      return day;
    }

    public void setDay(String day) {
      this.day = day;
    }

    public Long getCount() {
      return count;
    }

    public void setCount(Long count) {
      this.count = count;
    }
  }

  
  public record SystemVO(
      Double systemCpuLoad,
      long jvmUsedMemory,
      long jvmMaxMemory,
      long uptimeMs,
      Integer dbActive,
      Integer dbIdle,
      Integer dbTotal,
      Integer dbThreadsAwaiting,
      Boolean redisOk
  ) {
  }

  
  public record OverviewVO(
      long totalUsers,
      long todayNewUsers,
      List<NameCountRow> roleDistribution,
      long totalFiles,
      long totalFileSize,
      List<NameCountRow> fileTypeDistribution,
      long totalMsgs,
      long totalUnreadMsgs,
      SystemVO system
  ) {
  }

  
  public record TrendDayVO(
      String day,
      long userCount,
      long fileCount,
      long msgCount
  ) {
  }

  public record TrendVO(int days, List<TrendDayVO> list) {
  }
}
