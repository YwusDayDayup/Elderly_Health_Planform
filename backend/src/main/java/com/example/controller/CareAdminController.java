package com.example.controller;

import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.dto.CareDTO;
import com.example.service.CareService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/care")
public class CareAdminController {

  private final CareService careService;

  public CareAdminController(CareService careService) {
    this.careService = careService;
  }

  @GetMapping("/dashboard")
  public Result<CareDTO.DashboardVO> dashboard() {
    return Result.ok(careService.adminDashboard());
  }

  @GetMapping("/contents")
  public Result<PageResult<CareDTO.ContentVO>> contents(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String status) {
    return Result.ok(careService.adminContents(new CareDTO.PageReq(pageNo, pageSize, keyword, type, status)));
  }

  @GetMapping("/contents/{id}")
  public Result<CareDTO.ContentVO> contentDetail(@PathVariable Long id) {
    return Result.ok(careService.contentDetail(id, true));
  }

  @PostMapping("/contents")
  public Result<Void> createContent(@RequestBody CareDTO.ContentReq req) {
    careService.saveContent(req, null);
    return Result.ok(null);
  }

  @PutMapping("/contents/{id}")
  public Result<Void> updateContent(@PathVariable Long id, @RequestBody CareDTO.ContentReq req) {
    careService.saveContent(req, id);
    return Result.ok(null);
  }

  @DeleteMapping("/contents/{id}")
  public Result<Void> deleteContent(@PathVariable Long id) {
    careService.deleteContent(id);
    return Result.ok(null);
  }

  @GetMapping("/service-categories")
  public Result<List<CareDTO.ServiceCategoryVO>> serviceCategories() {
    return Result.ok(careService.serviceCategories(true));
  }

  @PostMapping("/service-categories")
  public Result<Void> createServiceCategory(@RequestBody CareDTO.ServiceCategoryReq req) {
    careService.saveServiceCategory(req, null);
    return Result.ok(null);
  }

  @PutMapping("/service-categories/{id}")
  public Result<Void> updateServiceCategory(@PathVariable Long id, @RequestBody CareDTO.ServiceCategoryReq req) {
    careService.saveServiceCategory(req, id);
    return Result.ok(null);
  }

  @DeleteMapping("/service-categories/{id}")
  public Result<Void> deleteServiceCategory(@PathVariable Long id) {
    careService.deleteServiceCategory(id);
    return Result.ok(null);
  }

  @GetMapping("/service-projects")
  public Result<PageResult<CareDTO.ServiceProjectVO>> serviceProjects(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) String status) {
    return Result.ok(careService.serviceProjects(new CareDTO.PageReq(pageNo, pageSize, keyword, null, status), categoryId, true));
  }

  @PostMapping("/service-projects")
  public Result<Void> createServiceProject(@RequestBody CareDTO.ServiceProjectReq req) {
    careService.saveServiceProject(req, null);
    return Result.ok(null);
  }

  @PutMapping("/service-projects/{id}")
  public Result<Void> updateServiceProject(@PathVariable Long id, @RequestBody CareDTO.ServiceProjectReq req) {
    careService.saveServiceProject(req, id);
    return Result.ok(null);
  }

  @DeleteMapping("/service-projects/{id}")
  public Result<Void> deleteServiceProject(@PathVariable Long id) {
    careService.deleteServiceProject(id);
    return Result.ok(null);
  }

  @GetMapping("/service-orders")
  public Result<PageResult<CareDTO.ServiceOrderVO>> serviceOrders(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String status) {
    return Result.ok(careService.adminOrders(new CareDTO.PageReq(pageNo, pageSize, keyword, null, status)));
  }

  @PostMapping("/service-orders/{id}/assign")
  public Result<CareDTO.ServiceOrderVO> assignOrder(@PathVariable Long id, @RequestBody CareDTO.AssignOrderReq req) {
    return Result.ok(careService.assignOrder(id, req));
  }

  @GetMapping("/elder-profiles")
  public Result<PageResult<CareDTO.ElderProfileVO>> elderProfiles(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize) {
    return Result.ok(careService.adminElderProfiles(new CareDTO.PageReq(pageNo, pageSize, null, null, null)));
  }

  @GetMapping("/staff-profiles")
  public Result<PageResult<CareDTO.StaffProfileVO>> staffProfiles(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String status) {
    return Result.ok(careService.adminStaffProfiles(new CareDTO.PageReq(pageNo, pageSize, null, null, status)));
  }

  @PostMapping("/staff-profiles/{id}/audit")
  public Result<Void> auditStaff(@PathVariable Long id, @RequestBody CareDTO.StaffAuditReq req) {
    careService.auditStaffProfile(id, req);
    return Result.ok(null);
  }

  @GetMapping("/family-bindings")
  public Result<PageResult<CareDTO.FamilyBindingVO>> familyBindings(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String status) {
    return Result.ok(careService.adminBindings(new CareDTO.PageReq(pageNo, pageSize, null, null, status)));
  }

  @GetMapping("/health-alerts")
  public Result<PageResult<CareDTO.HealthAlertVO>> healthAlerts(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String status) {
    return Result.ok(careService.adminHealthAlerts(new CareDTO.PageReq(pageNo, pageSize, null, null, status)));
  }

  @GetMapping("/external-call-logs")
  public Result<PageResult<CareDTO.ExternalCallLogVO>> externalCallLogs(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String type) {
    return Result.ok(careService.externalCallLogs(new CareDTO.PageReq(pageNo, pageSize, null, type, null)));
  }

  @GetMapping("/community-posts")
  public Result<PageResult<CareDTO.CommunityPostVO>> communityPosts(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String status) {
    return Result.ok(careService.communityPosts(new CareDTO.PageReq(pageNo, pageSize, keyword, type, status)));
  }

  @GetMapping("/op-logs")
  public Result<PageResult<CareDTO.OpLogVO>> opLogs(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) String type) {
    return Result.ok(careService.opLogs(new CareDTO.PageReq(pageNo, pageSize, keyword, type, null)));
  }
}
