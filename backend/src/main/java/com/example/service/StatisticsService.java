
package com.example.service;

import com.example.dto.StatisticsDTO;
import com.example.mapper.StatisticsMapper;
import java.lang.management.ManagementFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsService {

  private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final StatisticsMapper statisticsMapper;
  private final DataSource dataSource;
  private final RedisConnectionFactory redisConnectionFactory;

  public StatisticsService(StatisticsMapper statisticsMapper, DataSource dataSource, RedisConnectionFactory redisConnectionFactory) {
    this.statisticsMapper = statisticsMapper;
    this.dataSource = dataSource;
    this.redisConnectionFactory = redisConnectionFactory;
  }

  
  @Transactional(readOnly = true)
  public StatisticsDTO.OverviewVO overview() {
    long totalUsers = nvl(statisticsMapper.totalUsers());
    long todayNewUsers = nvl(statisticsMapper.todayNewUsers());

    List<StatisticsDTO.NameCountRow> roleDistribution = safeList(statisticsMapper.roleDistribution());

    long totalFiles = nvl(statisticsMapper.totalFiles());
    long totalFileSize = nvl(statisticsMapper.totalFileSize());
    List<StatisticsDTO.NameCountRow> fileTypeDistribution = safeList(statisticsMapper.fileTypeDistribution());

    long totalMsgs = nvl(statisticsMapper.totalMsgs());
    long totalUnreadMsgs = nvl(statisticsMapper.totalUnreadMsgs());

    return new StatisticsDTO.OverviewVO(
        totalUsers,
        todayNewUsers,
        roleDistribution,
        totalFiles,
        totalFileSize,
        fileTypeDistribution,
        totalMsgs,
        totalUnreadMsgs,
        systemStats());
  }

  
  @Transactional(readOnly = true)
  public StatisticsDTO.TrendVO trend(int days) {
    int d = days <= 0 ? 7 : Math.min(days, 90);

    Map<String, Long> userMap = toDayMap(statisticsMapper.userTrend(d));
    Map<String, Long> fileMap = toDayMap(statisticsMapper.fileTrend(d));
    Map<String, Long> msgMap = toDayMap(statisticsMapper.msgTrend(d));

    List<StatisticsDTO.TrendDayVO> list = new ArrayList<>();
    LocalDate start = LocalDate.now().minusDays(d - 1L);
    for (int i = 0; i < d; i++) {
      String day = start.plusDays(i).format(DAY_FMT);
      list.add(new StatisticsDTO.TrendDayVO(
          day,
          userMap.getOrDefault(day, 0L),
          fileMap.getOrDefault(day, 0L),
          msgMap.getOrDefault(day, 0L)));
    }

    return new StatisticsDTO.TrendVO(d, list);
  }

  private StatisticsDTO.SystemVO systemStats() {
    Double cpuLoad = null;
    try {
      java.lang.management.OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
      if (os instanceof com.sun.management.OperatingSystemMXBean osm) {
        double v = osm.getSystemCpuLoad();
        cpuLoad = v < 0 ? null : v;
      }
    } catch (Exception ignored) {
      cpuLoad = null;
    }

    Runtime rt = Runtime.getRuntime();
    long used = rt.totalMemory() - rt.freeMemory();
    long max = rt.maxMemory();
    long uptime = ManagementFactory.getRuntimeMXBean().getUptime();

    Integer dbActive = null;
    Integer dbIdle = null;
    Integer dbTotal = null;
    Integer dbAwait = null;
    try {
      if (dataSource != null && dataSource.getClass().getName().contains("Hikari")) {
        Object hk = dataSource;
        java.lang.reflect.Method mGet = hk.getClass().getMethod("getHikariPoolMXBean");
        Object bean = mGet.invoke(hk);
        if (bean != null) {
          dbActive = (Integer) bean.getClass().getMethod("getActiveConnections").invoke(bean);
          dbIdle = (Integer) bean.getClass().getMethod("getIdleConnections").invoke(bean);
          dbTotal = (Integer) bean.getClass().getMethod("getTotalConnections").invoke(bean);
          dbAwait = (Integer) bean.getClass().getMethod("getThreadsAwaitingConnection").invoke(bean);
        }
      }
    } catch (Exception ignored) {
      dbActive = null;
      dbIdle = null;
      dbTotal = null;
      dbAwait = null;
    }

    Boolean redisOk = null;
    try (RedisConnection conn = redisConnectionFactory.getConnection()) {
      String pong = conn.ping();
      redisOk = pong != null;
    } catch (Exception ignored) {
      redisOk = null;
    }

    return new StatisticsDTO.SystemVO(cpuLoad, used, max, uptime, dbActive, dbIdle, dbTotal, dbAwait, redisOk);
  }

  private long nvl(Long v) {
    return v == null ? 0L : v;
  }

  private <T> List<T> safeList(List<T> list) {
    return list == null ? List.of() : list;
  }

  private Map<String, Long> toDayMap(List<StatisticsDTO.DayCountRow> rows) {
    Map<String, Long> map = new HashMap<>();
    if (rows == null) {
      return map;
    }
    for (StatisticsDTO.DayCountRow r : rows) {
      if (r == null || r.getDay() == null) {
        continue;
      }
      map.put(r.getDay(), r.getCount() == null ? 0L : r.getCount());
    }
    return map;
  }
}
