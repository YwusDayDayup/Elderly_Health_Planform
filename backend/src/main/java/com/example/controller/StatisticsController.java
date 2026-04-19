
package com.example.controller;

import com.example.common.result.Result;
import com.example.dto.StatisticsDTO;
import com.example.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

  private final StatisticsService statisticsService;

  public StatisticsController(StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  
  @GetMapping("/api/statistics/overview")
  public Result<StatisticsDTO.OverviewVO> overview() {
    return Result.ok(statisticsService.overview());
  }

  
  @GetMapping("/api/statistics/trend")
  public Result<StatisticsDTO.TrendVO> trend(@RequestParam(value = "days", required = false, defaultValue = "7") int days) {
    return Result.ok(statisticsService.trend(days));
  }
}
