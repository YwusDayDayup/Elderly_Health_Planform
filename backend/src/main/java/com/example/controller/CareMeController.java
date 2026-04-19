package com.example.controller;

import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.dto.CareDTO;
import com.example.service.CareService;
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
@RequestMapping("/api/me")
public class CareMeController {

  private final CareService careService;

  public CareMeController(CareService careService) {
    this.careService = careService;
  }

  @GetMapping("/dashboard")
  public Result<CareDTO.DashboardVO> dashboard() {
    return Result.ok(careService.myDashboard());
  }

  @GetMapping("/service-orders")
  public Result<PageResult<CareDTO.ServiceOrderVO>> myOrders(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String status) {
    return Result.ok(careService.myOrders(new CareDTO.PageReq(pageNo, pageSize, null, null, status)));
  }

  @PostMapping("/service-orders")
  public Result<CareDTO.ServiceOrderVO> createOrder(@RequestBody CareDTO.CreateOrderReq req) {
    return Result.ok(careService.createOrder(req));
  }

  @PostMapping("/service-orders/{id}/pay")
  public Result<CareDTO.ServiceOrderVO> payOrder(@PathVariable Long id) {
    return Result.ok(careService.payOrder(id));
  }

  @PostMapping("/service-orders/{id}/cancel")
  public Result<CareDTO.ServiceOrderVO> cancelOrder(@PathVariable Long id) {
    return Result.ok(careService.cancelOrder(id));
  }

  @PostMapping("/service-orders/{id}/accept")
  public Result<CareDTO.ServiceOrderVO> acceptOrder(@PathVariable Long id) {
    return Result.ok(careService.acceptOrder(id));
  }

  @PostMapping("/service-orders/{id}/start")
  public Result<CareDTO.ServiceOrderVO> startOrder(@PathVariable Long id) {
    return Result.ok(careService.startOrder(id));
  }

  @PostMapping("/service-orders/{id}/complete")
  public Result<CareDTO.ServiceOrderVO> completeOrder(@PathVariable Long id) {
    return Result.ok(careService.completeOrder(id));
  }

  @PostMapping("/service-orders/{id}/rate")
  public Result<CareDTO.ServiceOrderVO> rateOrder(@PathVariable Long id, @RequestBody CareDTO.RateOrderReq req) {
    return Result.ok(careService.rateOrder(id, req));
  }

  @GetMapping("/elder-profile")
  public Result<CareDTO.ElderProfileVO> myElderProfile() {
    return Result.ok(careService.myElderProfile());
  }

  @PutMapping("/elder-profile")
  public Result<CareDTO.ElderProfileVO> saveMyElderProfile(@RequestBody CareDTO.ElderProfileReq req) {
    return Result.ok(careService.saveMyElderProfile(req));
  }

  @GetMapping("/staff-profile")
  public Result<CareDTO.StaffProfileVO> myStaffProfile() {
    return Result.ok(careService.myStaffProfile());
  }

  @PutMapping("/staff-profile")
  public Result<CareDTO.StaffProfileVO> saveMyStaffProfile(@RequestBody CareDTO.StaffProfileReq req) {
    return Result.ok(careService.saveMyStaffProfile(req));
  }

  @GetMapping("/family-bindings")
  public Result<PageResult<CareDTO.FamilyBindingVO>> myBindings(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) String status) {
    return Result.ok(careService.myBindings(new CareDTO.PageReq(pageNo, pageSize, null, null, status)));
  }

  @PostMapping("/family-bindings")
  public Result<CareDTO.FamilyBindingVO> createBinding(@RequestBody CareDTO.FamilyBindingReq req) {
    return Result.ok(careService.createBinding(req));
  }

  @PostMapping("/family-bindings/{id}/audit")
  public Result<CareDTO.FamilyBindingVO> auditBinding(@PathVariable Long id, @RequestBody CareDTO.FamilyBindingAuditReq req) {
    return Result.ok(careService.auditBinding(id, req));
  }

  @DeleteMapping("/family-bindings/{id}")
  public Result<Void> deleteBinding(@PathVariable Long id) {
    careService.deleteBinding(id);
    return Result.ok(null);
  }

  @GetMapping("/health-records")
  public Result<PageResult<CareDTO.HealthRecordVO>> myHealthRecords(
      @RequestParam(defaultValue = "1") Long pageNo,
      @RequestParam(defaultValue = "10") Long pageSize,
      @RequestParam(required = false) Long targetUserId) {
    return Result.ok(careService.myHealthRecords(new CareDTO.PageReq(pageNo, pageSize, null, null, null), targetUserId));
  }

  @PostMapping("/health-records")
  public Result<CareDTO.HealthRecordVO> createHealthRecord(@RequestBody CareDTO.HealthRecordReq req) {
    return Result.ok(careService.createHealthRecord(req));
  }

  @GetMapping("/safe-zone")
  public Result<CareDTO.SafeZoneVO> safeZone(@RequestParam(required = false) Long targetUserId) {
    return Result.ok(careService.safeZone(targetUserId));
  }

  @PutMapping("/safe-zone")
  public Result<CareDTO.SafeZoneVO> saveSafeZone(@RequestBody CareDTO.SafeZoneReq req) {
    return Result.ok(careService.saveMySafeZone(req));
  }

  @PostMapping("/locations")
  public Result<CareDTO.LocationVO> reportLocation(@RequestBody CareDTO.LocationReportReq req) {
    return Result.ok(careService.reportLocation(req));
  }

  @GetMapping("/locations/latest")
  public Result<CareDTO.LocationVO> latestLocation(@RequestParam(required = false) Long targetUserId) {
    return Result.ok(careService.latestLocation(targetUserId));
  }
}
