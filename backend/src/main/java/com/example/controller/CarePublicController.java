package com.example.controller;

import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.dto.CareDTO;
import com.example.service.CareService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class CarePublicController {

  private final CareService careService;

  public CarePublicController(CareService careService) {
    this.careService = careService;
  }

  @GetMapping("/contents")
  public Result<PageResult<CareDTO.ContentVO>> contents(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String type) {
    return Result.ok(careService.publicContents(new CareDTO.PageReq(pageNo, pageSize, keyword, type, null)));
  }

  @GetMapping("/contents/{id}")
  public Result<CareDTO.ContentVO> contentDetail(@PathVariable Long id) {
    return Result.ok(careService.contentDetail(id, false));
  }

  @GetMapping("/service-categories")
  public Result<List<CareDTO.ServiceCategoryVO>> serviceCategories() {
    return Result.ok(careService.serviceCategories(false));
  }

  @GetMapping("/service-projects")
  public Result<PageResult<CareDTO.ServiceProjectVO>> serviceProjects(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long categoryId) {
    return Result.ok(careService.serviceProjects(new CareDTO.PageReq(pageNo, pageSize, keyword, null, null), categoryId, false));
  }

  @GetMapping("/service-projects/{id}")
  public Result<CareDTO.ServiceProjectVO> serviceProjectDetail(@PathVariable Long id) {
    return Result.ok(careService.serviceProjectDetail(id));
  }
}
