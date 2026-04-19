package com.example.dto;

import com.example.common.model.PageQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CareDTO {

  public record MetricVO(String label, String value, String trend) {
  }

  public record PageReq(
      Long pageNo,
      Long pageSize,
      String keyword,
      String type,
      String status
  ) implements PageQuery {
  }

  public record DashboardVO(
      String role,
      String title,
      List<MetricVO> metrics,
      List<ContentVO> latestContents,
      List<ServiceOrderVO> latestOrders,
      List<HealthAlertVO> latestAlerts,
      List<FamilyBindingVO> latestBindings
  ) {
  }

  public record ContentVO(
      Long id,
      String contentType,
      String title,
      String summary,
      String content,
      String coverUrl,
      Integer status,
      String authorName,
      LocalDateTime publishedAt,
      Long viewCount
  ) {
  }

  public record ContentReq(
      String contentType,
      String title,
      String summary,
      String content,
      String coverUrl,
      Integer status
  ) {
  }

  public record ServiceCategoryVO(
      Long id,
      String name,
      String code,
      String description,
      Integer sortNo,
      Integer status
  ) {
  }

  public record ServiceCategoryReq(
      String name,
      String code,
      String description,
      Integer sortNo,
      Integer status
  ) {
  }

  public record ServiceProjectVO(
      Long id,
      Long categoryId,
      String categoryName,
      String name,
      String description,
      BigDecimal price,
      String unit,
      Integer status,
      String coverUrl,
      Long defaultStaffUserId,
      String defaultStaffName,
      Integer serviceDurationMinutes
  ) {
  }

  public record ServiceProjectReq(
      Long categoryId,
      String name,
      String description,
      BigDecimal price,
      String unit,
      Integer status,
      String coverUrl,
      Long defaultStaffUserId,
      Integer serviceDurationMinutes
  ) {
  }

  public record CreateOrderReq(
      Long elderUserId,
      Long serviceProjectId,
      LocalDateTime serviceTime,
      String serviceAddress,
      String contactName,
      String contactPhone,
      String remark
  ) {
  }

  public record AssignOrderReq(Long staffUserId) {
  }

  public record RateOrderReq(Integer rating, String ratingContent) {
  }

  public record ServiceOrderVO(
      Long id,
      String orderNo,
      Long elderUserId,
      String elderName,
      Long familyUserId,
      String familyName,
      Long staffUserId,
      String staffName,
      Long serviceProjectId,
      String serviceName,
      String categoryName,
      LocalDateTime serviceTime,
      String serviceAddress,
      String contactName,
      String contactPhone,
      String remark,
      BigDecimal amount,
      String payStatus,
      String orderStatus,
      LocalDateTime paymentTime,
      LocalDateTime acceptTime,
      LocalDateTime startTime,
      LocalDateTime finishTime,
      Integer rating,
      String ratingContent,
      LocalDateTime createTime
  ) {
  }

  public record FamilyBindingReq(
      Long elderUserId,
      Long familyUserId,
      String relationLabel,
      Integer healthAccess,
      Integer locationAccess,
      String remark
  ) {
  }

  public record FamilyBindingAuditReq(Boolean approved) {
  }

  public record FamilyBindingVO(
      Long id,
      Long elderUserId,
      String elderName,
      Long familyUserId,
      String familyName,
      String relationLabel,
      String status,
      Integer healthAccess,
      Integer locationAccess,
      String remark,
      LocalDateTime createTime
  ) {
  }

  public record ElderProfileReq(
      String realName,
      String idCard,
      String emergencyContactName,
      String emergencyContactPhone,
      String address,
      String healthNotes,
      Integer familyHealthAuthorized,
      Integer familyLocationAuthorized
  ) {
  }

  public record ElderProfileVO(
      Long id,
      Long userId,
      String username,
      String nickname,
      String phone,
      String realName,
      String idCard,
      String emergencyContactName,
      String emergencyContactPhone,
      String address,
      String healthNotes,
      Integer familyHealthAuthorized,
      Integer familyLocationAuthorized
  ) {
  }

  public record StaffProfileReq(
      String realName,
      String certificateNo,
      String specialty,
      Integer serviceRadiusKm,
      String intro
  ) {
  }

  public record StaffAuditReq(String auditStatus) {
  }

  public record StaffProfileVO(
      Long id,
      Long userId,
      String username,
      String nickname,
      String phone,
      String realName,
      String certificateNo,
      String specialty,
      Integer serviceRadiusKm,
      String intro,
      String auditStatus
  ) {
  }

  public record HealthRecordReq(
      LocalDate recordDate,
      BigDecimal weight,
      Integer systolicBp,
      Integer diastolicBp,
      BigDecimal bloodSugar,
      BigDecimal bloodLipid,
      Integer pulse,
      BigDecimal bodyTemp,
      Integer checkInFlag,
      String note
  ) {
  }

  public record HealthRecordVO(
      Long id,
      Long userId,
      String username,
      String nickname,
      LocalDate recordDate,
      BigDecimal weight,
      Integer systolicBp,
      Integer diastolicBp,
      BigDecimal bloodSugar,
      BigDecimal bloodLipid,
      Integer pulse,
      BigDecimal bodyTemp,
      Integer checkInFlag,
      String note,
      Integer abnormalFlag,
      LocalDateTime createTime
  ) {
  }

  public record HealthAlertVO(
      Long id,
      Long userId,
      String username,
      String nickname,
      Long recordId,
      String alertType,
      String alertLevel,
      String title,
      String content,
      String status,
      LocalDateTime createTime
  ) {
  }

  public record SafeZoneReq(
      BigDecimal centerLatitude,
      BigDecimal centerLongitude,
      Integer radiusMeters,
      String address
  ) {
  }

  public record SafeZoneVO(
      Long id,
      Long userId,
      BigDecimal centerLatitude,
      BigDecimal centerLongitude,
      Integer radiusMeters,
      String address
  ) {
  }

  public record LocationReportReq(
      BigDecimal latitude,
      BigDecimal longitude,
      String address
  ) {
  }

  public record LocationVO(
      Long id,
      Long userId,
      String username,
      String nickname,
      BigDecimal latitude,
      BigDecimal longitude,
      String address,
      Integer withinZone,
      LocalDateTime createTime
  ) {
  }

  public record ExternalCallLogVO(
      Long id,
      String providerType,
      String action,
      String bizType,
      String bizId,
      String reqJson,
      String respJson,
      Integer success,
      LocalDateTime createTime
  ) {
  }

  public record CommunityPostVO(
      Long id,
      Long authorId,
      String authorName,
      String title,
      String content,
      String category,
      String postStatus,
      String auditStatus,
      Integer commentCount,
      Integer likeCount,
      Integer viewCount,
      String lastAction,
      LocalDateTime lastActionTime,
      LocalDateTime createTime
  ) {
  }

  public record OpLogVO(
      Long id,
      Long userId,
      String username,
      String action,
      String method,
      String path,
      Integer success,
      String errorMsg,
      Long costMs,
      LocalDateTime createTime
  ) {
  }
}
