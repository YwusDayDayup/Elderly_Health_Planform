package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.constant.CareConstants;
import com.example.common.constant.GlobalConstants;
import com.example.common.context.UserContextHolder;
import com.example.common.exception.BizException;
import com.example.common.model.PageQuery;
import com.example.common.result.PageResult;
import com.example.dto.CareDTO;
import com.example.entity.ContentEntity;
import com.example.entity.CommunityPostEntity;
import com.example.entity.ElderProfileEntity;
import com.example.entity.FamilyBindingEntity;
import com.example.entity.HealthAlertEntity;
import com.example.entity.HealthRecordEntity;
import com.example.entity.LocationRecordEntity;
import com.example.entity.MsgEntity;
import com.example.entity.OpLogEntity;
import com.example.entity.SafeZoneEntity;
import com.example.entity.ServiceCategoryEntity;
import com.example.entity.ServiceOrderEntity;
import com.example.entity.ServiceProjectEntity;
import com.example.entity.StaffProfileEntity;
import com.example.entity.UserEntity;
import com.example.mapper.ContentMapper;
import com.example.mapper.CommunityPostMapper;
import com.example.mapper.ElderProfileMapper;
import com.example.mapper.ExternalCallLogMapper;
import com.example.mapper.FamilyBindingMapper;
import com.example.mapper.HealthAlertMapper;
import com.example.mapper.HealthRecordMapper;
import com.example.mapper.LocationRecordMapper;
import com.example.mapper.MsgMapper;
import com.example.mapper.OpLogMapper;
import com.example.mapper.SafeZoneMapper;
import com.example.mapper.ServiceCategoryMapper;
import com.example.mapper.ServiceOrderMapper;
import com.example.mapper.ServiceProjectMapper;
import com.example.mapper.StaffProfileMapper;
import com.example.mapper.UserMapper;
import com.example.service.external.MapGateway;
import com.example.service.external.PaymentGateway;
import com.example.service.external.SmsGateway;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CareService {

  private static final DateTimeFormatter ORDER_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

  private final UserMapper userMapper;
  private final MsgMapper msgMapper;
  private final ContentMapper contentMapper;
  private final CommunityPostMapper communityPostMapper;
  private final ElderProfileMapper elderProfileMapper;
  private final StaffProfileMapper staffProfileMapper;
  private final FamilyBindingMapper familyBindingMapper;
  private final ServiceCategoryMapper serviceCategoryMapper;
  private final ServiceProjectMapper serviceProjectMapper;
  private final ServiceOrderMapper serviceOrderMapper;
  private final HealthRecordMapper healthRecordMapper;
  private final HealthAlertMapper healthAlertMapper;
  private final SafeZoneMapper safeZoneMapper;
  private final LocationRecordMapper locationRecordMapper;
  private final ExternalCallLogMapper externalCallLogMapper;
  private final OpLogMapper opLogMapper;
  private final UserRoleDomainService userRoleDomainService;
  private final PaymentGateway paymentGateway;
  private final SmsGateway smsGateway;
  private final MapGateway mapGateway;

  public CareService(
      UserMapper userMapper,
      MsgMapper msgMapper,
      ContentMapper contentMapper,
      CommunityPostMapper communityPostMapper,
      ElderProfileMapper elderProfileMapper,
      StaffProfileMapper staffProfileMapper,
      FamilyBindingMapper familyBindingMapper,
      ServiceCategoryMapper serviceCategoryMapper,
      ServiceProjectMapper serviceProjectMapper,
      ServiceOrderMapper serviceOrderMapper,
      HealthRecordMapper healthRecordMapper,
      HealthAlertMapper healthAlertMapper,
      SafeZoneMapper safeZoneMapper,
      LocationRecordMapper locationRecordMapper,
      ExternalCallLogMapper externalCallLogMapper,
      OpLogMapper opLogMapper,
      UserRoleDomainService userRoleDomainService,
      PaymentGateway paymentGateway,
      SmsGateway smsGateway,
      MapGateway mapGateway) {
    this.userMapper = userMapper;
    this.msgMapper = msgMapper;
    this.contentMapper = contentMapper;
    this.communityPostMapper = communityPostMapper;
    this.elderProfileMapper = elderProfileMapper;
    this.staffProfileMapper = staffProfileMapper;
    this.familyBindingMapper = familyBindingMapper;
    this.serviceCategoryMapper = serviceCategoryMapper;
    this.serviceProjectMapper = serviceProjectMapper;
    this.serviceOrderMapper = serviceOrderMapper;
    this.healthRecordMapper = healthRecordMapper;
    this.healthAlertMapper = healthAlertMapper;
    this.safeZoneMapper = safeZoneMapper;
    this.locationRecordMapper = locationRecordMapper;
    this.externalCallLogMapper = externalCallLogMapper;
    this.opLogMapper = opLogMapper;
    this.userRoleDomainService = userRoleDomainService;
    this.paymentGateway = paymentGateway;
    this.smsGateway = smsGateway;
    this.mapGateway = mapGateway;
  }

  @Transactional(readOnly = true)
  public CareDTO.DashboardVO myDashboard() {
    Long userId = currentUserId();
    String role = currentRole();
    List<CareDTO.MetricVO> metrics = switch (role) {
      case GlobalConstants.ROLE_ADMIN -> adminMetrics();
      case GlobalConstants.ROLE_STAFF -> staffMetrics(userId);
      case GlobalConstants.ROLE_FAMILY -> familyMetrics(userId);
      default -> elderMetrics(userId);
    };
    List<CareDTO.ContentVO> latestContents = contentMapper.selectList(new LambdaQueryWrapper<ContentEntity>()
            .eq(ContentEntity::getStatus, GlobalConstants.STATUS_ENABLED)
            .orderByDesc(ContentEntity::getPublishedAt)
            .last("limit 4"))
        .stream()
        .map(this::toContentVO)
        .toList();
    List<CareDTO.ServiceOrderVO> latestOrders = switch (role) {
      case GlobalConstants.ROLE_ADMIN -> mapOrders(serviceOrderMapper.selectList(new LambdaQueryWrapper<ServiceOrderEntity>()
          .orderByDesc(ServiceOrderEntity::getId).last("limit 5")));
      case GlobalConstants.ROLE_STAFF -> mapOrders(serviceOrderMapper.selectList(new LambdaQueryWrapper<ServiceOrderEntity>()
          .eq(ServiceOrderEntity::getStaffUserId, userId)
          .orderByDesc(ServiceOrderEntity::getId).last("limit 5")));
      case GlobalConstants.ROLE_FAMILY -> mapOrders(serviceOrderMapper.selectList(new LambdaQueryWrapper<ServiceOrderEntity>()
          .and(w -> w.eq(ServiceOrderEntity::getFamilyUserId, userId)
              .or().in(ServiceOrderEntity::getElderUserId, safeUserIds(boundElderIds(userId))))
          .orderByDesc(ServiceOrderEntity::getId).last("limit 5")));
      default -> mapOrders(serviceOrderMapper.selectList(new LambdaQueryWrapper<ServiceOrderEntity>()
          .eq(ServiceOrderEntity::getElderUserId, userId)
          .orderByDesc(ServiceOrderEntity::getId).last("limit 5")));
    };
    List<CareDTO.HealthAlertVO> latestAlerts = switch (role) {
      case GlobalConstants.ROLE_ADMIN -> mapAlerts(healthAlertMapper.selectList(new LambdaQueryWrapper<HealthAlertEntity>()
          .orderByDesc(HealthAlertEntity::getId).last("limit 5")));
      case GlobalConstants.ROLE_FAMILY -> mapAlerts(healthAlertMapper.selectList(new LambdaQueryWrapper<HealthAlertEntity>()
          .in(HealthAlertEntity::getUserId, safeUserIds(boundElderIds(userId)))
          .orderByDesc(HealthAlertEntity::getId).last("limit 5")));
      default -> mapAlerts(healthAlertMapper.selectList(new LambdaQueryWrapper<HealthAlertEntity>()
          .eq(HealthAlertEntity::getUserId, userId)
          .orderByDesc(HealthAlertEntity::getId).last("limit 5")));
    };
    List<CareDTO.FamilyBindingVO> latestBindings = switch (role) {
      case GlobalConstants.ROLE_ADMIN -> mapBindings(familyBindingMapper.selectList(new LambdaQueryWrapper<FamilyBindingEntity>()
          .orderByDesc(FamilyBindingEntity::getId).last("limit 5")));
      case GlobalConstants.ROLE_FAMILY -> mapBindings(familyBindingMapper.selectList(new LambdaQueryWrapper<FamilyBindingEntity>()
          .eq(FamilyBindingEntity::getFamilyUserId, userId)
          .orderByDesc(FamilyBindingEntity::getId).last("limit 5")));
      default -> mapBindings(familyBindingMapper.selectList(new LambdaQueryWrapper<FamilyBindingEntity>()
          .eq(FamilyBindingEntity::getElderUserId, userId)
          .orderByDesc(FamilyBindingEntity::getId).last("limit 5")));
    };
    return new CareDTO.DashboardVO(role, dashboardTitle(role), metrics, latestContents, latestOrders, latestAlerts,
        latestBindings);
  }

  @Transactional(readOnly = true)
  public CareDTO.DashboardVO adminDashboard() {
    requireRole(GlobalConstants.ROLE_ADMIN);
    return myDashboard();
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.ContentVO> publicContents(CareDTO.PageReq req) {
    return pageContents(req, false);
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.ContentVO> adminContents(CareDTO.PageReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    return pageContents(req, true);
  }

  @Transactional(readOnly = true)
  public CareDTO.ContentVO contentDetail(Long id, boolean adminAccess) {
    ContentEntity entity = requireContent(id);
    if (!adminAccess && !Objects.equals(entity.getStatus(), GlobalConstants.STATUS_ENABLED)) {
      throw new BizException(BizException.Code.NOT_FOUND, "内容不存在");
    }
    entity.setViewCount(entity.getViewCount() == null ? 1L : entity.getViewCount() + 1L);
    contentMapper.updateById(entity);
    return toContentVO(entity);
  }

  @Transactional
  public void saveContent(CareDTO.ContentReq req, Long id) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    ContentEntity entity = id == null ? new ContentEntity() : requireContent(id);
    entity.setContentType(req.contentType());
    entity.setTitle(req.title());
    entity.setSummary(req.summary());
    entity.setContent(req.content());
    entity.setCoverUrl(req.coverUrl());
    entity.setStatus(req.status() == null ? GlobalConstants.STATUS_ENABLED : req.status());
    entity.setAuthorId(currentUserId());
    entity.setAuthorName(displayName(requireUser(currentUserId())));
    entity.setPublishedAt(LocalDateTime.now());
    if (id == null) {
      entity.setViewCount(0L);
      contentMapper.insert(entity);
    } else {
      contentMapper.updateById(entity);
    }
  }

  @Transactional
  public void deleteContent(Long id) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    contentMapper.deleteById(id);
  }

  @Transactional(readOnly = true)
  public List<CareDTO.ServiceCategoryVO> serviceCategories(boolean adminAccess) {
    List<ServiceCategoryEntity> list = serviceCategoryMapper.selectList(new LambdaQueryWrapper<ServiceCategoryEntity>()
        .eq(!adminAccess, ServiceCategoryEntity::getStatus, GlobalConstants.STATUS_ENABLED)
        .orderByAsc(ServiceCategoryEntity::getSortNo)
        .orderByAsc(ServiceCategoryEntity::getId));
    return list.stream().map(this::toCategoryVO).toList();
  }

  @Transactional
  public void saveServiceCategory(CareDTO.ServiceCategoryReq req, Long id) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    ServiceCategoryEntity entity = id == null ? new ServiceCategoryEntity() : requireCategory(id);
    entity.setName(req.name());
    entity.setCode(req.code());
    entity.setDescription(req.description());
    entity.setSortNo(req.sortNo() == null ? 0 : req.sortNo());
    entity.setStatus(req.status() == null ? GlobalConstants.STATUS_ENABLED : req.status());
    if (id == null) {
      serviceCategoryMapper.insert(entity);
    } else {
      serviceCategoryMapper.updateById(entity);
    }
  }

  @Transactional
  public void deleteServiceCategory(Long id) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    serviceCategoryMapper.deleteById(id);
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.ServiceProjectVO> serviceProjects(CareDTO.PageReq req, Long categoryId, boolean adminAccess) {
    LambdaQueryWrapper<ServiceProjectEntity> wrapper = new LambdaQueryWrapper<ServiceProjectEntity>()
        .eq(categoryId != null, ServiceProjectEntity::getCategoryId, categoryId)
        .like(StringUtils.hasText(req.keyword()), ServiceProjectEntity::getName, req.keyword())
        .eq(!adminAccess, ServiceProjectEntity::getStatus, GlobalConstants.STATUS_ENABLED)
        .eq(adminAccess && parseStatus(req.status()) != null, ServiceProjectEntity::getStatus, parseStatus(req.status()))
        .orderByAsc(ServiceProjectEntity::getCategoryId)
        .orderByDesc(ServiceProjectEntity::getId);
    IPage<ServiceProjectEntity> page = serviceProjectMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())), wrapper);
    return PageResult.of(page.getTotal(), page.getRecords().stream().map(this::toProjectVO).toList());
  }

  @Transactional(readOnly = true)
  public CareDTO.ServiceProjectVO serviceProjectDetail(Long id) {
    return toProjectVO(requireProject(id));
  }

  @Transactional
  public void saveServiceProject(CareDTO.ServiceProjectReq req, Long id) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    ServiceProjectEntity entity = id == null ? new ServiceProjectEntity() : requireProject(id);
    entity.setCategoryId(req.categoryId());
    entity.setName(req.name());
    entity.setDescription(req.description());
    entity.setPrice(req.price() == null ? BigDecimal.ZERO : req.price());
    entity.setUnit(StringUtils.hasText(req.unit()) ? req.unit() : "次");
    entity.setStatus(req.status() == null ? GlobalConstants.STATUS_ENABLED : req.status());
    entity.setCoverUrl(req.coverUrl());
    entity.setDefaultStaffUserId(req.defaultStaffUserId());
    entity.setServiceDurationMinutes(req.serviceDurationMinutes() == null ? 60 : req.serviceDurationMinutes());
    if (id == null) {
      serviceProjectMapper.insert(entity);
    } else {
      serviceProjectMapper.updateById(entity);
    }
  }

  @Transactional
  public void deleteServiceProject(Long id) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    serviceProjectMapper.deleteById(id);
  }

  @Transactional
  public CareDTO.ServiceOrderVO createOrder(CareDTO.CreateOrderReq req) {
    String role = currentRole();
    if (!Set.of(GlobalConstants.ROLE_ELDER, GlobalConstants.ROLE_FAMILY).contains(role)) {
      throw new BizException(BizException.Code.FORBIDDEN, "当前角色不能预约服务");
    }
    Long operatorId = currentUserId();
    Long elderUserId = resolveOrderElderUserId(role, operatorId, req.elderUserId());
    ServiceProjectEntity project = requireProject(req.serviceProjectId());
    ServiceCategoryEntity category = requireCategory(project.getCategoryId());
    ServiceOrderEntity entity = new ServiceOrderEntity();
    entity.setOrderNo("SO" + LocalDateTime.now().format(ORDER_FMT) + operatorId);
    entity.setElderUserId(elderUserId);
    entity.setFamilyUserId(GlobalConstants.ROLE_FAMILY.equals(role) ? operatorId : null);
    entity.setStaffUserId(project.getDefaultStaffUserId());
    entity.setServiceProjectId(project.getId());
    entity.setServiceName(project.getName());
    entity.setCategoryName(category.getName());
    entity.setServiceTime(req.serviceTime());
    entity.setServiceAddress(req.serviceAddress());
    entity.setContactName(req.contactName());
    entity.setContactPhone(req.contactPhone());
    entity.setRemark(req.remark());
    entity.setAmount(project.getPrice());
    entity.setPayStatus(CareConstants.PAY_UNPAID);
    entity.setOrderStatus(CareConstants.ORDER_PENDING_PAYMENT);
    serviceOrderMapper.insert(entity);
    sendSiteMsg(elderUserId, "SERVICE_ORDER", "新服务预约已创建", "订单 " + entity.getOrderNo() + " 已创建，待支付。", entity.getOrderNo());
    return toOrderVO(entity);
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.ServiceOrderVO> myOrders(CareDTO.PageReq req) {
    Long userId = currentUserId();
    String role = currentRole();
    LambdaQueryWrapper<ServiceOrderEntity> wrapper = new LambdaQueryWrapper<ServiceOrderEntity>()
        .eq(StringUtils.hasText(req.status()), ServiceOrderEntity::getOrderStatus, req.status())
        .orderByDesc(ServiceOrderEntity::getId);
    if (GlobalConstants.ROLE_STAFF.equals(role)) {
      wrapper.eq(ServiceOrderEntity::getStaffUserId, userId);
    } else if (GlobalConstants.ROLE_FAMILY.equals(role)) {
      List<Long> elderIds = boundElderIds(userId);
      wrapper.and(w -> w.eq(ServiceOrderEntity::getFamilyUserId, userId)
          .or().in(!elderIds.isEmpty(), ServiceOrderEntity::getElderUserId, elderIds));
    } else {
      wrapper.eq(ServiceOrderEntity::getElderUserId, userId);
    }
    IPage<ServiceOrderEntity> page = serviceOrderMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())), wrapper);
    return PageResult.of(page.getTotal(), mapOrders(page.getRecords()));
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.ServiceOrderVO> adminOrders(CareDTO.PageReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    LambdaQueryWrapper<ServiceOrderEntity> wrapper = new LambdaQueryWrapper<ServiceOrderEntity>()
        .like(StringUtils.hasText(req.keyword()), ServiceOrderEntity::getServiceName, req.keyword())
        .eq(StringUtils.hasText(req.status()), ServiceOrderEntity::getOrderStatus, req.status())
        .orderByDesc(ServiceOrderEntity::getId);
    IPage<ServiceOrderEntity> page = serviceOrderMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())), wrapper);
    return PageResult.of(page.getTotal(), mapOrders(page.getRecords()));
  }

  @Transactional
  public CareDTO.ServiceOrderVO payOrder(Long id) {
    ServiceOrderEntity order = requireOrder(id);
    ensureCanManageOrder(order, true);
    if (CareConstants.PAY_PAID.equals(order.getPayStatus())) {
      return toOrderVO(order);
    }
    paymentGateway.pay(order.getOrderNo(), order.getAmount());
    order.setPayStatus(CareConstants.PAY_PAID);
    order.setPaymentTime(LocalDateTime.now());
    order.setOrderStatus(order.getStaffUserId() == null
        ? CareConstants.ORDER_PENDING_ASSIGN
        : CareConstants.ORDER_PENDING_ACCEPT);
    serviceOrderMapper.updateById(order);
    notifyOrderParticipants(order, "订单已支付", "订单 " + order.getOrderNo() + " 已完成支付。");
    return toOrderVO(order);
  }

  @Transactional
  public CareDTO.ServiceOrderVO cancelOrder(Long id) {
    ServiceOrderEntity order = requireOrder(id);
    ensureCanManageOrder(order, false);
    if (Set.of(CareConstants.ORDER_COMPLETED, CareConstants.ORDER_CANCELLED, CareConstants.ORDER_REFUNDED)
        .contains(order.getOrderStatus())) {
      return toOrderVO(order);
    }
    if (CareConstants.PAY_PAID.equals(order.getPayStatus())) {
      paymentGateway.refund(order.getOrderNo(), order.getAmount());
      order.setPayStatus(CareConstants.PAY_REFUNDED);
      order.setOrderStatus(CareConstants.ORDER_REFUNDED);
    } else {
      order.setOrderStatus(CareConstants.ORDER_CANCELLED);
    }
    serviceOrderMapper.updateById(order);
    notifyOrderParticipants(order, "订单已取消", "订单 " + order.getOrderNo() + " 已取消。");
    return toOrderVO(order);
  }

  @Transactional
  public CareDTO.ServiceOrderVO assignOrder(Long id, CareDTO.AssignOrderReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    ServiceOrderEntity order = requireOrder(id);
    ensureUserRole(req.staffUserId(), GlobalConstants.ROLE_STAFF);
    order.setStaffUserId(req.staffUserId());
    order.setOrderStatus(CareConstants.PAY_PAID.equals(order.getPayStatus())
        ? CareConstants.ORDER_PENDING_ACCEPT
        : CareConstants.ORDER_PENDING_ASSIGN);
    serviceOrderMapper.updateById(order);
    sendSiteMsg(req.staffUserId(), "SERVICE_ORDER", "收到新派单", "订单 " + order.getOrderNo() + " 已派发给您。", order.getOrderNo());
    return toOrderVO(order);
  }

  @Transactional
  public CareDTO.ServiceOrderVO acceptOrder(Long id) {
    ServiceOrderEntity order = requireOrder(id);
    requireStaffOwner(order);
    if (!CareConstants.ORDER_PENDING_ACCEPT.equals(order.getOrderStatus())) {
      throw new BizException(BizException.Code.BIZ_ERROR, "当前订单不可接单");
    }
    order.setOrderStatus(CareConstants.ORDER_PENDING_SERVICE);
    order.setAcceptTime(LocalDateTime.now());
    serviceOrderMapper.updateById(order);
    notifyOrderParticipants(order, "服务人员已接单", "订单 " + order.getOrderNo() + " 已由服务人员接单。");
    return toOrderVO(order);
  }

  @Transactional
  public CareDTO.ServiceOrderVO startOrder(Long id) {
    ServiceOrderEntity order = requireOrder(id);
    requireStaffOwner(order);
    if (!CareConstants.ORDER_PENDING_SERVICE.equals(order.getOrderStatus())) {
      throw new BizException(BizException.Code.BIZ_ERROR, "当前订单不可开始服务");
    }
    order.setOrderStatus(CareConstants.ORDER_IN_SERVICE);
    order.setStartTime(LocalDateTime.now());
    serviceOrderMapper.updateById(order);
    notifyOrderParticipants(order, "服务已开始", "订单 " + order.getOrderNo() + " 已开始服务。");
    return toOrderVO(order);
  }

  @Transactional
  public CareDTO.ServiceOrderVO completeOrder(Long id) {
    ServiceOrderEntity order = requireOrder(id);
    requireStaffOwner(order);
    if (!CareConstants.ORDER_IN_SERVICE.equals(order.getOrderStatus())) {
      throw new BizException(BizException.Code.BIZ_ERROR, "当前订单不可完成");
    }
    order.setOrderStatus(CareConstants.ORDER_COMPLETED);
    order.setFinishTime(LocalDateTime.now());
    serviceOrderMapper.updateById(order);
    notifyOrderParticipants(order, "服务已完成", "订单 " + order.getOrderNo() + " 已完成，可进行评价。");
    return toOrderVO(order);
  }

  @Transactional
  public CareDTO.ServiceOrderVO rateOrder(Long id, CareDTO.RateOrderReq req) {
    ServiceOrderEntity order = requireOrder(id);
    ensureCanManageOrder(order, false);
    if (!CareConstants.ORDER_COMPLETED.equals(order.getOrderStatus())) {
      throw new BizException(BizException.Code.BIZ_ERROR, "仅已完成订单可评价");
    }
    order.setRating(req.rating());
    order.setRatingContent(req.ratingContent());
    serviceOrderMapper.updateById(order);
    return toOrderVO(order);
  }

  @Transactional(readOnly = true)
  public CareDTO.ElderProfileVO myElderProfile() {
    requireRole(GlobalConstants.ROLE_ELDER);
    return toElderProfileVO(findOrCreateElderProfile(currentUserId()));
  }

  @Transactional
  public CareDTO.ElderProfileVO saveMyElderProfile(CareDTO.ElderProfileReq req) {
    requireRole(GlobalConstants.ROLE_ELDER);
    ElderProfileEntity entity = findOrCreateElderProfile(currentUserId());
    entity.setRealName(req.realName());
    entity.setIdCard(req.idCard());
    entity.setEmergencyContactName(req.emergencyContactName());
    entity.setEmergencyContactPhone(req.emergencyContactPhone());
    entity.setAddress(req.address());
    entity.setHealthNotes(req.healthNotes());
    entity.setFamilyHealthAuthorized(req.familyHealthAuthorized() == null ? 1 : req.familyHealthAuthorized());
    entity.setFamilyLocationAuthorized(req.familyLocationAuthorized() == null ? 0 : req.familyLocationAuthorized());
    if (entity.getId() == null) {
      elderProfileMapper.insert(entity);
    } else {
      elderProfileMapper.updateById(entity);
    }
    return toElderProfileVO(entity);
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.ElderProfileVO> adminElderProfiles(CareDTO.PageReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    IPage<ElderProfileEntity> page = elderProfileMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())),
        new LambdaQueryWrapper<ElderProfileEntity>().orderByDesc(ElderProfileEntity::getId));
    return PageResult.of(page.getTotal(), page.getRecords().stream().map(this::toElderProfileVO).toList());
  }

  @Transactional(readOnly = true)
  public CareDTO.StaffProfileVO myStaffProfile() {
    requireRole(GlobalConstants.ROLE_STAFF);
    return toStaffProfileVO(findOrCreateStaffProfile(currentUserId()));
  }

  @Transactional
  public CareDTO.StaffProfileVO saveMyStaffProfile(CareDTO.StaffProfileReq req) {
    requireRole(GlobalConstants.ROLE_STAFF);
    StaffProfileEntity entity = findOrCreateStaffProfile(currentUserId());
    entity.setRealName(req.realName());
    entity.setCertificateNo(req.certificateNo());
    entity.setSpecialty(req.specialty());
    entity.setServiceRadiusKm(req.serviceRadiusKm() == null ? 5 : req.serviceRadiusKm());
    entity.setIntro(req.intro());
    if (entity.getId() == null) {
      entity.setAuditStatus(CareConstants.STAFF_AUDIT_PENDING);
      staffProfileMapper.insert(entity);
    } else {
      staffProfileMapper.updateById(entity);
    }
    return toStaffProfileVO(entity);
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.StaffProfileVO> adminStaffProfiles(CareDTO.PageReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    LambdaQueryWrapper<StaffProfileEntity> wrapper = new LambdaQueryWrapper<StaffProfileEntity>()
        .eq(StringUtils.hasText(req.status()), StaffProfileEntity::getAuditStatus, req.status())
        .orderByDesc(StaffProfileEntity::getId);
    IPage<StaffProfileEntity> page = staffProfileMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())), wrapper);
    return PageResult.of(page.getTotal(), page.getRecords().stream().map(this::toStaffProfileVO).toList());
  }

  @Transactional
  public void auditStaffProfile(Long id, CareDTO.StaffAuditReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    StaffProfileEntity entity = requireStaffProfile(id);
    entity.setAuditStatus(req.auditStatus());
    staffProfileMapper.updateById(entity);
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.FamilyBindingVO> myBindings(CareDTO.PageReq req) {
    Long userId = currentUserId();
    String role = currentRole();
    LambdaQueryWrapper<FamilyBindingEntity> wrapper = new LambdaQueryWrapper<FamilyBindingEntity>()
        .eq(StringUtils.hasText(req.status()), FamilyBindingEntity::getStatus, req.status())
        .orderByDesc(FamilyBindingEntity::getId);
    if (GlobalConstants.ROLE_FAMILY.equals(role)) {
      wrapper.eq(FamilyBindingEntity::getFamilyUserId, userId);
    } else if (GlobalConstants.ROLE_ELDER.equals(role)) {
      wrapper.eq(FamilyBindingEntity::getElderUserId, userId);
    } else {
      throw new BizException(BizException.Code.FORBIDDEN, "当前角色不能查看家属绑定");
    }
    IPage<FamilyBindingEntity> page = familyBindingMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())), wrapper);
    return PageResult.of(page.getTotal(), mapBindings(page.getRecords()));
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.FamilyBindingVO> adminBindings(CareDTO.PageReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    IPage<FamilyBindingEntity> page = familyBindingMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())),
        new LambdaQueryWrapper<FamilyBindingEntity>()
            .eq(StringUtils.hasText(req.status()), FamilyBindingEntity::getStatus, req.status())
            .orderByDesc(FamilyBindingEntity::getId));
    return PageResult.of(page.getTotal(), mapBindings(page.getRecords()));
  }

  @Transactional
  public CareDTO.FamilyBindingVO createBinding(CareDTO.FamilyBindingReq req) {
    String role = currentRole();
    Long userId = currentUserId();
    FamilyBindingEntity entity = new FamilyBindingEntity();
    if (GlobalConstants.ROLE_FAMILY.equals(role)) {
      entity.setFamilyUserId(userId);
      entity.setElderUserId(req.elderUserId());
    } else if (GlobalConstants.ROLE_ELDER.equals(role)) {
      entity.setElderUserId(userId);
      entity.setFamilyUserId(req.familyUserId());
    } else {
      throw new BizException(BizException.Code.FORBIDDEN, "当前角色不能发起绑定");
    }
    requireUser(entity.getElderUserId());
    requireUser(entity.getFamilyUserId());
    entity.setRelationLabel(req.relationLabel());
    entity.setStatus(CareConstants.BINDING_PENDING);
    entity.setHealthAccess(req.healthAccess() == null ? 1 : req.healthAccess());
    entity.setLocationAccess(req.locationAccess() == null ? 0 : req.locationAccess());
    entity.setRemark(req.remark());
    familyBindingMapper.insert(entity);
    sendSiteMsg(entity.getElderUserId(), "FAMILY_BINDING", "收到家属绑定申请", "请审核新的家属绑定申请。", String.valueOf(entity.getId()));
    return toBindingVO(entity);
  }

  @Transactional
  public CareDTO.FamilyBindingVO auditBinding(Long id, CareDTO.FamilyBindingAuditReq req) {
    FamilyBindingEntity entity = requireBinding(id);
    String role = currentRole();
    Long userId = currentUserId();
    if (!GlobalConstants.ROLE_ADMIN.equals(role) && !Objects.equals(entity.getElderUserId(), userId)) {
      throw new BizException(BizException.Code.FORBIDDEN, "无权限审核该绑定");
    }
    entity.setStatus(Boolean.TRUE.equals(req.approved()) ? CareConstants.BINDING_ACTIVE : CareConstants.BINDING_REJECTED);
    familyBindingMapper.updateById(entity);
    sendSiteMsg(entity.getFamilyUserId(), "FAMILY_BINDING", "家属绑定审核结果",
        Boolean.TRUE.equals(req.approved()) ? "家属绑定已通过。" : "家属绑定已被拒绝。", String.valueOf(entity.getId()));
    return toBindingVO(entity);
  }

  @Transactional
  public void deleteBinding(Long id) {
    FamilyBindingEntity entity = requireBinding(id);
    String role = currentRole();
    Long userId = currentUserId();
    if (!GlobalConstants.ROLE_ADMIN.equals(role)
        && !Objects.equals(entity.getElderUserId(), userId)
        && !Objects.equals(entity.getFamilyUserId(), userId)) {
      throw new BizException(BizException.Code.FORBIDDEN, "无权限解绑");
    }
    familyBindingMapper.deleteById(id);
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.HealthRecordVO> myHealthRecords(CareDTO.PageReq req, Long targetUserId) {
    Long resolvedUserId = resolveHealthTargetUserId(targetUserId);
    IPage<HealthRecordEntity> page = healthRecordMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())),
        new LambdaQueryWrapper<HealthRecordEntity>()
            .eq(HealthRecordEntity::getUserId, resolvedUserId)
            .orderByDesc(HealthRecordEntity::getRecordDate)
            .orderByDesc(HealthRecordEntity::getId));
    return PageResult.of(page.getTotal(), page.getRecords().stream().map(this::toHealthRecordVO).toList());
  }

  @Transactional
  public CareDTO.HealthRecordVO createHealthRecord(CareDTO.HealthRecordReq req) {
    requireRole(GlobalConstants.ROLE_ELDER);
    HealthRecordEntity entity = new HealthRecordEntity();
    entity.setUserId(currentUserId());
    entity.setRecordDate(req.recordDate() == null ? LocalDate.now() : req.recordDate());
    entity.setWeight(req.weight());
    entity.setSystolicBp(req.systolicBp());
    entity.setDiastolicBp(req.diastolicBp());
    entity.setBloodSugar(req.bloodSugar());
    entity.setBloodLipid(req.bloodLipid());
    entity.setPulse(req.pulse());
    entity.setBodyTemp(req.bodyTemp());
    entity.setCheckInFlag(req.checkInFlag() == null ? 1 : req.checkInFlag());
    entity.setNote(req.note());
    entity.setAbnormalFlag(isAbnormal(req) ? 1 : 0);
    healthRecordMapper.insert(entity);
    if (Objects.equals(entity.getAbnormalFlag(), 1)) {
      createHealthAlert(entity);
    }
    return toHealthRecordVO(entity);
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.HealthAlertVO> adminHealthAlerts(CareDTO.PageReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    IPage<HealthAlertEntity> page = healthAlertMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())),
        new LambdaQueryWrapper<HealthAlertEntity>()
            .eq(StringUtils.hasText(req.status()), HealthAlertEntity::getStatus, req.status())
            .orderByDesc(HealthAlertEntity::getId));
    return PageResult.of(page.getTotal(), mapAlerts(page.getRecords()));
  }

  @Transactional
  public CareDTO.SafeZoneVO saveMySafeZone(CareDTO.SafeZoneReq req) {
    requireRole(GlobalConstants.ROLE_ELDER);
    SafeZoneEntity entity = safeZoneMapper.selectOne(new LambdaQueryWrapper<SafeZoneEntity>()
        .eq(SafeZoneEntity::getUserId, currentUserId()));
    if (entity == null) {
      entity = new SafeZoneEntity();
      entity.setUserId(currentUserId());
    }
    entity.setCenterLatitude(req.centerLatitude());
    entity.setCenterLongitude(req.centerLongitude());
    entity.setRadiusMeters(req.radiusMeters() == null ? 500 : req.radiusMeters());
    entity.setAddress(req.address());
    if (entity.getId() == null) {
      safeZoneMapper.insert(entity);
    } else {
      safeZoneMapper.updateById(entity);
    }
    return toSafeZoneVO(entity);
  }

  @Transactional(readOnly = true)
  public CareDTO.SafeZoneVO safeZone(Long targetUserId) {
    Long resolved = resolveLocationTargetUserId(targetUserId);
    SafeZoneEntity entity = safeZoneMapper.selectOne(new LambdaQueryWrapper<SafeZoneEntity>()
        .eq(SafeZoneEntity::getUserId, resolved));
    return entity == null ? null : toSafeZoneVO(entity);
  }

  @Transactional
  public CareDTO.LocationVO reportLocation(CareDTO.LocationReportReq req) {
    requireRole(GlobalConstants.ROLE_ELDER);
    SafeZoneEntity zone = safeZoneMapper.selectOne(new LambdaQueryWrapper<SafeZoneEntity>()
        .eq(SafeZoneEntity::getUserId, currentUserId()));
    String address = mapGateway.resolveAddress(req.latitude(), req.longitude(), req.address());
    boolean within = zone == null || mapGateway.withinZone(req.latitude(), req.longitude(),
        zone.getCenterLatitude(), zone.getCenterLongitude(), zone.getRadiusMeters());
    LocationRecordEntity entity = new LocationRecordEntity();
    entity.setUserId(currentUserId());
    entity.setLatitude(req.latitude());
    entity.setLongitude(req.longitude());
    entity.setAddress(address);
    entity.setWithinZone(within ? 1 : 0);
    locationRecordMapper.insert(entity);
    if (!within) {
      createLocationAlert(entity);
    }
    return toLocationVO(entity);
  }

  @Transactional(readOnly = true)
  public CareDTO.LocationVO latestLocation(Long targetUserId) {
    Long resolved = resolveLocationTargetUserId(targetUserId);
    LocationRecordEntity entity = locationRecordMapper.selectOne(new LambdaQueryWrapper<LocationRecordEntity>()
        .eq(LocationRecordEntity::getUserId, resolved)
        .orderByDesc(LocationRecordEntity::getId)
        .last("limit 1"));
    return entity == null ? null : toLocationVO(entity);
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.ExternalCallLogVO> externalCallLogs(CareDTO.PageReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    IPage<com.example.entity.ExternalCallLogEntity> page = externalCallLogMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())),
        new LambdaQueryWrapper<com.example.entity.ExternalCallLogEntity>()
            .eq(StringUtils.hasText(req.type()), com.example.entity.ExternalCallLogEntity::getProviderType, req.type())
            .orderByDesc(com.example.entity.ExternalCallLogEntity::getId));
    List<CareDTO.ExternalCallLogVO> list = page.getRecords().stream()
        .map(it -> new CareDTO.ExternalCallLogVO(it.getId(), it.getProviderType(), it.getAction(), it.getBizType(),
            it.getBizId(), it.getReqJson(), it.getRespJson(), it.getSuccess(), it.getCreateTime()))
        .toList();
    return PageResult.of(page.getTotal(), list);
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.CommunityPostVO> communityPosts(CareDTO.PageReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    IPage<CommunityPostEntity> page = communityPostMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())),
        new LambdaQueryWrapper<CommunityPostEntity>()
            .like(StringUtils.hasText(req.keyword()), CommunityPostEntity::getTitle, req.keyword())
            .eq(StringUtils.hasText(req.type()), CommunityPostEntity::getCategory, req.type())
            .eq(StringUtils.hasText(req.status()), CommunityPostEntity::getAuditStatus, req.status())
            .orderByDesc(CommunityPostEntity::getId));
    return PageResult.of(page.getTotal(), page.getRecords().stream().map(this::toCommunityPostVO).toList());
  }

  @Transactional(readOnly = true)
  public PageResult<CareDTO.OpLogVO> opLogs(CareDTO.PageReq req) {
    requireRole(GlobalConstants.ROLE_ADMIN);
    IPage<OpLogEntity> page = opLogMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())),
        new LambdaQueryWrapper<OpLogEntity>()
            .like(StringUtils.hasText(req.keyword()), OpLogEntity::getUsername, req.keyword())
            .eq(StringUtils.hasText(req.type()), OpLogEntity::getAction, req.type())
            .orderByDesc(OpLogEntity::getId));
    return PageResult.of(page.getTotal(), page.getRecords().stream().map(this::toOpLogVO).toList());
  }

  private PageResult<CareDTO.ContentVO> pageContents(CareDTO.PageReq req, boolean adminAccess) {
    LambdaQueryWrapper<ContentEntity> wrapper = new LambdaQueryWrapper<ContentEntity>()
        .like(StringUtils.hasText(req.keyword()), ContentEntity::getTitle, req.keyword())
        .eq(StringUtils.hasText(req.type()), ContentEntity::getContentType, req.type())
        .eq(!adminAccess, ContentEntity::getStatus, GlobalConstants.STATUS_ENABLED)
        .eq(adminAccess && parseStatus(req.status()) != null, ContentEntity::getStatus, parseStatus(req.status()))
        .orderByDesc(ContentEntity::getPublishedAt)
        .orderByDesc(ContentEntity::getId);
    IPage<ContentEntity> page = contentMapper.selectPage(
        new Page<>(PageQuery.pageNo(req.pageNo()), PageQuery.pageSize(req.pageSize())), wrapper);
    return PageResult.of(page.getTotal(), page.getRecords().stream().map(this::toContentVO).toList());
  }

  private List<CareDTO.MetricVO> adminMetrics() {
    long totalUsers = userMapper.selectCount(new LambdaQueryWrapper<>());
    long elderCount = countUsersByRole(GlobalConstants.ROLE_ELDER);
    long staffCount = countUsersByRole(GlobalConstants.ROLE_STAFF);
    long familyCount = countUsersByRole(GlobalConstants.ROLE_FAMILY);
    long orderCount = serviceOrderMapper.selectCount(new LambdaQueryWrapper<>());
    long alertCount = healthAlertMapper.selectCount(new LambdaQueryWrapper<HealthAlertEntity>()
        .eq(HealthAlertEntity::getStatus, "OPEN"));
    return List.of(
        new CareDTO.MetricVO("平台用户", String.valueOf(totalUsers), "老人 " + elderCount + " / 家属 " + familyCount),
        new CareDTO.MetricVO("服务人员", String.valueOf(staffCount), "待审核 " + countStaffAuditPending()),
        new CareDTO.MetricVO("服务订单", String.valueOf(orderCount), "待派单 " + countByOrderStatus(CareConstants.ORDER_PENDING_ASSIGN)),
        new CareDTO.MetricVO("健康预警", String.valueOf(alertCount), "待跟进异常提醒")
    );
  }

  private List<CareDTO.MetricVO> elderMetrics(Long userId) {
    long orders = serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .eq(ServiceOrderEntity::getElderUserId, userId));
    long unpaid = serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .eq(ServiceOrderEntity::getElderUserId, userId)
        .eq(ServiceOrderEntity::getOrderStatus, CareConstants.ORDER_PENDING_PAYMENT));
    long records = healthRecordMapper.selectCount(new LambdaQueryWrapper<HealthRecordEntity>()
        .eq(HealthRecordEntity::getUserId, userId));
    long bindings = familyBindingMapper.selectCount(new LambdaQueryWrapper<FamilyBindingEntity>()
        .eq(FamilyBindingEntity::getElderUserId, userId)
        .eq(FamilyBindingEntity::getStatus, CareConstants.BINDING_ACTIVE));
    return List.of(
        new CareDTO.MetricVO("我的订单", String.valueOf(orders), "待支付 " + unpaid),
        new CareDTO.MetricVO("健康记录", String.valueOf(records), "持续记录有助于异常预警"),
        new CareDTO.MetricVO("家属绑定", String.valueOf(bindings), "可共享健康与位置状态"),
        new CareDTO.MetricVO("未读消息", String.valueOf(unreadMsgCount(userId)), "查看最新提醒")
    );
  }

  private List<CareDTO.MetricVO> staffMetrics(Long userId) {
    long pending = serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .eq(ServiceOrderEntity::getStaffUserId, userId)
        .eq(ServiceOrderEntity::getOrderStatus, CareConstants.ORDER_PENDING_ACCEPT));
    long serving = serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .eq(ServiceOrderEntity::getStaffUserId, userId)
        .eq(ServiceOrderEntity::getOrderStatus, CareConstants.ORDER_IN_SERVICE));
    long finished = serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .eq(ServiceOrderEntity::getStaffUserId, userId)
        .eq(ServiceOrderEntity::getOrderStatus, CareConstants.ORDER_COMPLETED));
    StaffProfileEntity profile = findOrCreateStaffProfile(userId);
    return List.of(
        new CareDTO.MetricVO("待接订单", String.valueOf(pending), "尽快确认服务时间"),
        new CareDTO.MetricVO("服务中", String.valueOf(serving), "实时更新服务状态"),
        new CareDTO.MetricVO("已完成", String.valueOf(finished), "累计完成服务"),
        new CareDTO.MetricVO("资质审核", profile.getAuditStatus(), "请保持资料完整")
    );
  }

  private List<CareDTO.MetricVO> familyMetrics(Long userId) {
    List<Long> elderIds = boundElderIds(userId);
    long bindings = familyBindingMapper.selectCount(new LambdaQueryWrapper<FamilyBindingEntity>()
        .eq(FamilyBindingEntity::getFamilyUserId, userId)
        .eq(FamilyBindingEntity::getStatus, CareConstants.BINDING_ACTIVE));
    long alerts = elderIds.isEmpty() ? 0L : healthAlertMapper.selectCount(new LambdaQueryWrapper<HealthAlertEntity>()
        .in(HealthAlertEntity::getUserId, elderIds));
    long orders = elderIds.isEmpty() ? 0L : serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .in(ServiceOrderEntity::getElderUserId, elderIds));
    return List.of(
        new CareDTO.MetricVO("绑定老人", String.valueOf(bindings), "已授权家庭协同"),
        new CareDTO.MetricVO("健康预警", String.valueOf(alerts), "关注异常健康变化"),
        new CareDTO.MetricVO("服务订单", String.valueOf(orders), "同步查看老人预约进度"),
        new CareDTO.MetricVO("未读消息", String.valueOf(unreadMsgCount(userId)), "查看最新提醒")
    );
  }

  private String dashboardTitle(String role) {
    return switch (role) {
      case GlobalConstants.ROLE_ADMIN -> "社区老人服务系统总览";
      case GlobalConstants.ROLE_STAFF -> "服务人员工作台";
      case GlobalConstants.ROLE_FAMILY -> "家属协同中心";
      default -> "老年用户服务首页";
    };
  }

  private long unreadMsgCount(Long userId) {
    return msgMapper.selectCount(new LambdaQueryWrapper<MsgEntity>()
        .eq(MsgEntity::getReceiverId, userId)
        .eq(MsgEntity::getReadFlag, 0));
  }

  private long countUsersByRole(String role) {
    return userRoleDomainService.userIdsByRole(role).size();
  }

  private long countStaffAuditPending() {
    return staffProfileMapper.selectCount(new LambdaQueryWrapper<StaffProfileEntity>()
        .eq(StaffProfileEntity::getAuditStatus, CareConstants.STAFF_AUDIT_PENDING));
  }

  private long countByOrderStatus(String status) {
    return serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .eq(ServiceOrderEntity::getOrderStatus, status));
  }

  private Long resolveOrderElderUserId(String role, Long operatorId, Long reqElderUserId) {
    if (GlobalConstants.ROLE_ELDER.equals(role)) {
      return operatorId;
    }
    if (reqElderUserId == null) {
      throw new BizException(BizException.Code.BAD_REQUEST, "家属代预约时必须指定老人账号");
    }
    if (!isActiveBinding(reqElderUserId, operatorId)) {
      throw new BizException(BizException.Code.FORBIDDEN, "未建立有效老人绑定关系");
    }
    return reqElderUserId;
  }

  private Long resolveHealthTargetUserId(Long targetUserId) {
    Long currentUserId = currentUserId();
    String role = currentRole();
    if (GlobalConstants.ROLE_ELDER.equals(role)) {
      return currentUserId;
    }
    if (GlobalConstants.ROLE_ADMIN.equals(role)) {
      return targetUserId == null ? currentUserId : targetUserId;
    }
    if (GlobalConstants.ROLE_FAMILY.equals(role)) {
      if (targetUserId == null || !isActiveBinding(targetUserId, currentUserId)) {
        throw new BizException(BizException.Code.FORBIDDEN, "未授权查看健康记录");
      }
      ElderProfileEntity profile = findOrCreateElderProfile(targetUserId);
      if (!Objects.equals(profile.getFamilyHealthAuthorized(), 1)) {
        throw new BizException(BizException.Code.FORBIDDEN, "老人未授权家属查看健康记录");
      }
      return targetUserId;
    }
    throw new BizException(BizException.Code.FORBIDDEN, "当前角色不能查看健康记录");
  }

  private Long resolveLocationTargetUserId(Long targetUserId) {
    Long currentUserId = currentUserId();
    String role = currentRole();
    if (GlobalConstants.ROLE_ELDER.equals(role)) {
      return currentUserId;
    }
    if (GlobalConstants.ROLE_ADMIN.equals(role)) {
      return targetUserId == null ? currentUserId : targetUserId;
    }
    if (GlobalConstants.ROLE_FAMILY.equals(role)) {
      if (targetUserId == null || !isActiveBinding(targetUserId, currentUserId)) {
        throw new BizException(BizException.Code.FORBIDDEN, "未授权查看位置信息");
      }
      ElderProfileEntity profile = findOrCreateElderProfile(targetUserId);
      if (!Objects.equals(profile.getFamilyLocationAuthorized(), 1)) {
        throw new BizException(BizException.Code.FORBIDDEN, "老人未授权家属查看位置信息");
      }
      return targetUserId;
    }
    throw new BizException(BizException.Code.FORBIDDEN, "当前角色不能查看位置信息");
  }

  private boolean isAbnormal(CareDTO.HealthRecordReq req) {
    return (req.systolicBp() != null && req.systolicBp() >= 140)
        || (req.diastolicBp() != null && req.diastolicBp() >= 90)
        || (req.bloodSugar() != null && req.bloodSugar().compareTo(new BigDecimal("7.0")) >= 0)
        || (req.bodyTemp() != null && req.bodyTemp().compareTo(new BigDecimal("37.3")) >= 0);
  }

  private void createHealthAlert(HealthRecordEntity record) {
    HealthAlertEntity alert = new HealthAlertEntity();
    alert.setUserId(record.getUserId());
    alert.setRecordId(record.getId());
    alert.setAlertType(CareConstants.ALERT_HEALTH);
    alert.setAlertLevel("HIGH");
    alert.setTitle("健康数据异常提醒");
    alert.setContent("检测到老人健康指标异常，请及时关注。");
    alert.setStatus("OPEN");
    healthAlertMapper.insert(alert);
    sendSiteMsg(record.getUserId(), "HEALTH_ALERT", alert.getTitle(), alert.getContent(), String.valueOf(alert.getId()));
    notifyFamilyForAlert(record.getUserId(), alert);
  }

  private void createLocationAlert(LocationRecordEntity record) {
    HealthAlertEntity alert = new HealthAlertEntity();
    alert.setUserId(record.getUserId());
    alert.setRecordId(record.getId());
    alert.setAlertType(CareConstants.ALERT_LOCATION);
    alert.setAlertLevel("MEDIUM");
    alert.setTitle("位置越界提醒");
    alert.setContent("老人当前位置超出安全区域，请及时确认。");
    alert.setStatus("OPEN");
    healthAlertMapper.insert(alert);
    notifyFamilyForAlert(record.getUserId(), alert);
    sendSiteMsg(record.getUserId(), "LOCATION_ALERT", alert.getTitle(), alert.getContent(), String.valueOf(alert.getId()));
  }

  private void notifyFamilyForAlert(Long elderUserId, HealthAlertEntity alert) {
    ElderProfileEntity profile = findOrCreateElderProfile(elderUserId);
    List<FamilyBindingEntity> bindings = familyBindingMapper.selectList(new LambdaQueryWrapper<FamilyBindingEntity>()
        .eq(FamilyBindingEntity::getElderUserId, elderUserId)
        .eq(FamilyBindingEntity::getStatus, CareConstants.BINDING_ACTIVE));
    for (FamilyBindingEntity binding : bindings) {
      boolean canNotify = CareConstants.ALERT_LOCATION.equals(alert.getAlertType())
          ? Objects.equals(binding.getLocationAccess(), 1) && Objects.equals(profile.getFamilyLocationAuthorized(), 1)
          : Objects.equals(binding.getHealthAccess(), 1) && Objects.equals(profile.getFamilyHealthAuthorized(), 1);
      if (!canNotify) {
        continue;
      }
      sendSiteMsg(binding.getFamilyUserId(), alert.getAlertType() + "_ALERT", alert.getTitle(), alert.getContent(),
          String.valueOf(alert.getId()));
      UserEntity family = requireUser(binding.getFamilyUserId());
      if (StringUtils.hasText(family.getPhone())) {
        smsGateway.send(family.getPhone(), alert.getAlertType() + "_ALERT", alert.getContent(),
            String.valueOf(alert.getId()));
      }
    }
    for (Long adminId : userRoleDomainService.userIdsByRole(GlobalConstants.ROLE_ADMIN)) {
      sendSiteMsg(adminId, alert.getAlertType() + "_ALERT", alert.getTitle(), alert.getContent(), String.valueOf(alert.getId()));
    }
  }

  private void notifyOrderParticipants(ServiceOrderEntity order, String title, String content) {
    sendSiteMsg(order.getElderUserId(), "SERVICE_ORDER", title, content, order.getOrderNo());
    if (order.getFamilyUserId() != null) {
      sendSiteMsg(order.getFamilyUserId(), "SERVICE_ORDER", title, content, order.getOrderNo());
    }
    if (order.getStaffUserId() != null) {
      sendSiteMsg(order.getStaffUserId(), "SERVICE_ORDER", title, content, order.getOrderNo());
      UserEntity staff = requireUser(order.getStaffUserId());
      if (StringUtils.hasText(staff.getPhone())) {
        smsGateway.send(staff.getPhone(), "SERVICE_ORDER", content, order.getOrderNo());
      }
    }
  }

  private void sendSiteMsg(Long receiverId, String msgType, String title, String content, String bizId) {
    if (receiverId == null) {
      return;
    }
    MsgEntity entity = new MsgEntity();
    entity.setSenderId(currentUserId());
    entity.setReceiverId(receiverId);
    entity.setMsgType(msgType);
    entity.setTitle(title);
    entity.setContent(content);
    entity.setBizId(bizId);
    entity.setReadFlag(0);
    msgMapper.insert(entity);
  }

  private void ensureCanManageOrder(ServiceOrderEntity order, boolean includeFamily) {
    String role = currentRole();
    Long userId = currentUserId();
    if (GlobalConstants.ROLE_ADMIN.equals(role)) {
      return;
    }
    if (GlobalConstants.ROLE_ELDER.equals(role) && Objects.equals(order.getElderUserId(), userId)) {
      return;
    }
    if (includeFamily && GlobalConstants.ROLE_FAMILY.equals(role)
        && (Objects.equals(order.getFamilyUserId(), userId) || isActiveBinding(order.getElderUserId(), userId))) {
      return;
    }
    if (!includeFamily && GlobalConstants.ROLE_FAMILY.equals(role)
        && (Objects.equals(order.getFamilyUserId(), userId) || isActiveBinding(order.getElderUserId(), userId))) {
      return;
    }
    throw new BizException(BizException.Code.FORBIDDEN, "无权限操作该订单");
  }

  private void requireStaffOwner(ServiceOrderEntity order) {
    requireRole(GlobalConstants.ROLE_STAFF);
    if (!Objects.equals(order.getStaffUserId(), currentUserId())) {
      throw new BizException(BizException.Code.FORBIDDEN, "该订单未分配给当前服务人员");
    }
  }

  private boolean isActiveBinding(Long elderUserId, Long familyUserId) {
    if (elderUserId == null || familyUserId == null) {
      return false;
    }
    return familyBindingMapper.selectCount(new LambdaQueryWrapper<FamilyBindingEntity>()
        .eq(FamilyBindingEntity::getElderUserId, elderUserId)
        .eq(FamilyBindingEntity::getFamilyUserId, familyUserId)
        .eq(FamilyBindingEntity::getStatus, CareConstants.BINDING_ACTIVE)) > 0;
  }

  private List<Long> boundElderIds(Long familyUserId) {
    return familyBindingMapper.selectList(new LambdaQueryWrapper<FamilyBindingEntity>()
            .eq(FamilyBindingEntity::getFamilyUserId, familyUserId)
            .eq(FamilyBindingEntity::getStatus, CareConstants.BINDING_ACTIVE))
        .stream()
        .map(FamilyBindingEntity::getElderUserId)
        .toList();
  }

  private List<Long> safeUserIds(List<Long> ids) {
    return ids == null || ids.isEmpty() ? List.of(-1L) : ids;
  }

  private Integer parseStatus(String raw) {
    if (!StringUtils.hasText(raw)) {
      return null;
    }
    try {
      return Integer.parseInt(raw);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private void requireRole(String role) {
    if (!role.equals(currentRole())) {
      throw new BizException(BizException.Code.FORBIDDEN, "当前角色无权限");
    }
  }

  private void ensureUserRole(Long userId, String role) {
    if (userId == null) {
      throw new BizException(BizException.Code.BAD_REQUEST, "参数不能为空");
    }
    String actualRole = userRoleDomainService.roleByUserId(userId);
    if (!role.equals(actualRole)) {
      throw new BizException(BizException.Code.BIZ_ERROR, "目标用户角色不匹配");
    }
  }

  private Long currentUserId() {
    return UserContextHolder.requireUserId();
  }

  private String currentRole() {
    return userRoleDomainService.roleByUserId(currentUserId());
  }

  private ContentEntity requireContent(Long id) {
    ContentEntity entity = contentMapper.selectById(id);
    if (entity == null) {
      throw new BizException(BizException.Code.NOT_FOUND, "内容不存在");
    }
    return entity;
  }

  private ServiceCategoryEntity requireCategory(Long id) {
    ServiceCategoryEntity entity = serviceCategoryMapper.selectById(id);
    if (entity == null) {
      throw new BizException(BizException.Code.NOT_FOUND, "服务分类不存在");
    }
    return entity;
  }

  private ServiceProjectEntity requireProject(Long id) {
    ServiceProjectEntity entity = serviceProjectMapper.selectById(id);
    if (entity == null) {
      throw new BizException(BizException.Code.NOT_FOUND, "服务项目不存在");
    }
    return entity;
  }

  private ServiceOrderEntity requireOrder(Long id) {
    ServiceOrderEntity entity = serviceOrderMapper.selectById(id);
    if (entity == null) {
      throw new BizException(BizException.Code.NOT_FOUND, "订单不存在");
    }
    return entity;
  }

  private FamilyBindingEntity requireBinding(Long id) {
    FamilyBindingEntity entity = familyBindingMapper.selectById(id);
    if (entity == null) {
      throw new BizException(BizException.Code.NOT_FOUND, "绑定记录不存在");
    }
    return entity;
  }

  private StaffProfileEntity requireStaffProfile(Long id) {
    StaffProfileEntity entity = staffProfileMapper.selectById(id);
    if (entity == null) {
      throw new BizException(BizException.Code.NOT_FOUND, "服务人员档案不存在");
    }
    return entity;
  }

  private UserEntity requireUser(Long id) {
    UserEntity entity = userMapper.selectById(id);
    if (entity == null) {
      throw new BizException(BizException.Code.NOT_FOUND, "用户不存在");
    }
    return entity;
  }

  private ElderProfileEntity findOrCreateElderProfile(Long userId) {
    ElderProfileEntity entity = elderProfileMapper.selectOne(new LambdaQueryWrapper<ElderProfileEntity>()
        .eq(ElderProfileEntity::getUserId, userId));
    if (entity != null) {
      return entity;
    }
    entity = new ElderProfileEntity();
    entity.setUserId(userId);
    entity.setFamilyHealthAuthorized(1);
    entity.setFamilyLocationAuthorized(0);
    return entity;
  }

  private StaffProfileEntity findOrCreateStaffProfile(Long userId) {
    StaffProfileEntity entity = staffProfileMapper.selectOne(new LambdaQueryWrapper<StaffProfileEntity>()
        .eq(StaffProfileEntity::getUserId, userId));
    if (entity != null) {
      return entity;
    }
    entity = new StaffProfileEntity();
    entity.setUserId(userId);
    entity.setServiceRadiusKm(5);
    entity.setAuditStatus(CareConstants.STAFF_AUDIT_PENDING);
    return entity;
  }

  private CareDTO.ContentVO toContentVO(ContentEntity entity) {
    return new CareDTO.ContentVO(entity.getId(), entity.getContentType(), entity.getTitle(), entity.getSummary(),
        entity.getContent(), entity.getCoverUrl(), entity.getStatus(), entity.getAuthorName(), entity.getPublishedAt(),
        entity.getViewCount());
  }

  private CareDTO.ServiceCategoryVO toCategoryVO(ServiceCategoryEntity entity) {
    return new CareDTO.ServiceCategoryVO(entity.getId(), entity.getName(), entity.getCode(), entity.getDescription(),
        entity.getSortNo(), entity.getStatus());
  }

  private CareDTO.ServiceProjectVO toProjectVO(ServiceProjectEntity entity) {
    ServiceCategoryEntity category = serviceCategoryMapper.selectById(entity.getCategoryId());
    UserEntity staff = entity.getDefaultStaffUserId() == null ? null : userMapper.selectById(entity.getDefaultStaffUserId());
    return new CareDTO.ServiceProjectVO(entity.getId(), entity.getCategoryId(),
        category == null ? null : category.getName(), entity.getName(), entity.getDescription(), entity.getPrice(),
        entity.getUnit(), entity.getStatus(), entity.getCoverUrl(), entity.getDefaultStaffUserId(),
        staff == null ? null : displayName(staff), entity.getServiceDurationMinutes());
  }

  private List<CareDTO.ServiceOrderVO> mapOrders(List<ServiceOrderEntity> records) {
    return records.stream().map(this::toOrderVO).toList();
  }

  private CareDTO.ServiceOrderVO toOrderVO(ServiceOrderEntity entity) {
    UserEntity elder = requireUser(entity.getElderUserId());
    UserEntity family = entity.getFamilyUserId() == null ? null : userMapper.selectById(entity.getFamilyUserId());
    UserEntity staff = entity.getStaffUserId() == null ? null : userMapper.selectById(entity.getStaffUserId());
    return new CareDTO.ServiceOrderVO(entity.getId(), entity.getOrderNo(), entity.getElderUserId(), displayName(elder),
        entity.getFamilyUserId(), family == null ? null : displayName(family), entity.getStaffUserId(),
        staff == null ? null : displayName(staff), entity.getServiceProjectId(), entity.getServiceName(),
        entity.getCategoryName(), entity.getServiceTime(), entity.getServiceAddress(), entity.getContactName(),
        entity.getContactPhone(), entity.getRemark(), scale(entity.getAmount()), entity.getPayStatus(),
        entity.getOrderStatus(), entity.getPaymentTime(), entity.getAcceptTime(), entity.getStartTime(),
        entity.getFinishTime(), entity.getRating(), entity.getRatingContent(), entity.getCreateTime());
  }

  private List<CareDTO.FamilyBindingVO> mapBindings(List<FamilyBindingEntity> records) {
    return records.stream().map(this::toBindingVO).toList();
  }

  private CareDTO.FamilyBindingVO toBindingVO(FamilyBindingEntity entity) {
    UserEntity elder = requireUser(entity.getElderUserId());
    UserEntity family = requireUser(entity.getFamilyUserId());
    return new CareDTO.FamilyBindingVO(entity.getId(), entity.getElderUserId(), displayName(elder),
        entity.getFamilyUserId(), displayName(family), entity.getRelationLabel(), entity.getStatus(),
        entity.getHealthAccess(), entity.getLocationAccess(), entity.getRemark(), entity.getCreateTime());
  }

  private CareDTO.ElderProfileVO toElderProfileVO(ElderProfileEntity entity) {
    UserEntity user = requireUser(entity.getUserId());
    return new CareDTO.ElderProfileVO(entity.getId(), entity.getUserId(), user.getUsername(), user.getNickname(),
        user.getPhone(), entity.getRealName(), entity.getIdCard(), entity.getEmergencyContactName(),
        entity.getEmergencyContactPhone(), entity.getAddress(), entity.getHealthNotes(),
        entity.getFamilyHealthAuthorized(), entity.getFamilyLocationAuthorized());
  }

  private CareDTO.StaffProfileVO toStaffProfileVO(StaffProfileEntity entity) {
    UserEntity user = requireUser(entity.getUserId());
    return new CareDTO.StaffProfileVO(entity.getId(), entity.getUserId(), user.getUsername(), user.getNickname(),
        user.getPhone(), entity.getRealName(), entity.getCertificateNo(), entity.getSpecialty(),
        entity.getServiceRadiusKm(), entity.getIntro(), entity.getAuditStatus());
  }

  private CareDTO.HealthRecordVO toHealthRecordVO(HealthRecordEntity entity) {
    UserEntity user = requireUser(entity.getUserId());
    return new CareDTO.HealthRecordVO(entity.getId(), entity.getUserId(), user.getUsername(), displayName(user),
        entity.getRecordDate(), entity.getWeight(), entity.getSystolicBp(), entity.getDiastolicBp(),
        entity.getBloodSugar(), entity.getBloodLipid(), entity.getPulse(), entity.getBodyTemp(),
        entity.getCheckInFlag(), entity.getNote(), entity.getAbnormalFlag(), entity.getCreateTime());
  }

  private List<CareDTO.HealthAlertVO> mapAlerts(List<HealthAlertEntity> records) {
    return records.stream().map(this::toAlertVO).toList();
  }

  private CareDTO.HealthAlertVO toAlertVO(HealthAlertEntity entity) {
    UserEntity user = requireUser(entity.getUserId());
    return new CareDTO.HealthAlertVO(entity.getId(), entity.getUserId(), user.getUsername(), displayName(user),
        entity.getRecordId(), entity.getAlertType(), entity.getAlertLevel(), entity.getTitle(), entity.getContent(),
        entity.getStatus(), entity.getCreateTime());
  }

  private CareDTO.SafeZoneVO toSafeZoneVO(SafeZoneEntity entity) {
    return new CareDTO.SafeZoneVO(entity.getId(), entity.getUserId(), entity.getCenterLatitude(),
        entity.getCenterLongitude(), entity.getRadiusMeters(), entity.getAddress());
  }

  private CareDTO.LocationVO toLocationVO(LocationRecordEntity entity) {
    UserEntity user = requireUser(entity.getUserId());
    return new CareDTO.LocationVO(entity.getId(), entity.getUserId(), user.getUsername(), displayName(user),
        entity.getLatitude(), entity.getLongitude(), entity.getAddress(), entity.getWithinZone(), entity.getCreateTime());
  }

  private BigDecimal scale(BigDecimal value) {
    return value == null ? BigDecimal.ZERO : value.setScale(2, RoundingMode.HALF_UP);
  }

  private String displayName(UserEntity user) {
    if (user == null) {
      return null;
    }
    return StringUtils.hasText(user.getNickname()) ? user.getNickname() : user.getUsername();
  }

  private CareDTO.CommunityPostVO toCommunityPostVO(CommunityPostEntity entity) {
    return new CareDTO.CommunityPostVO(entity.getId(), entity.getAuthorId(), entity.getAuthorName(), entity.getTitle(),
        entity.getContent(), entity.getCategory(), entity.getPostStatus(), entity.getAuditStatus(),
        entity.getCommentCount(), entity.getLikeCount(), entity.getViewCount(), entity.getLastAction(),
        entity.getLastActionTime(), entity.getCreateTime());
  }

  private CareDTO.OpLogVO toOpLogVO(OpLogEntity entity) {
    return new CareDTO.OpLogVO(entity.getId(), entity.getUserId(), entity.getUsername(), entity.getAction(),
        entity.getMethod(), entity.getPath(), entity.getSuccess(), entity.getErrorMsg(), entity.getCostMs(),
        entity.getCreateTime());
  }
}
