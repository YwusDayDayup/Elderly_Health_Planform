package com.example.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.constant.CareConstants;
import com.example.common.constant.GlobalConstants;
import com.example.common.util.PasswordUtils;
import com.example.entity.CommunityPostEntity;
import com.example.entity.ContentEntity;
import com.example.entity.ElderProfileEntity;
import com.example.entity.ExternalCallLogEntity;
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
import com.example.mapper.CommunityPostMapper;
import com.example.mapper.ContentMapper;
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
import com.example.service.UserRoleDomainService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.seed", name = "enabled", havingValue = "true")
public class UserSeedRunner implements CommandLineRunner {

  private static final String DEMO_PREFIX = "demo_";
  private static final String[] SURNAMES = {"张", "王", "李", "赵", "刘", "陈", "杨", "黄", "周", "吴", "徐", "孙", "胡", "朱", "高", "林"};
  private static final String[] ELDER_NAMES = {"秀兰", "桂芳", "玉英", "淑珍", "建国", "德华", "志强", "美玲", "春梅", "翠花", "文秀", "国庆", "安民", "淑华", "爱华", "永福"};
  private static final String[] FAMILY_NAMES = {"小梅", "晓丽", "佳宁", "婷婷", "志远", "浩然", "欣怡", "嘉豪", "子轩", "宇航", "思雨", "梦洁"};
  private static final String[] STAFF_NAMES = {"护工", "陪诊员", "家政员", "康复师", "护理员", "社工", "营养师", "维修员"};
  private static final String[] COMMUNITIES = {"幸福社区", "康乐社区", "颐养社区", "暖阳社区", "安康社区", "同心社区"};
  private static final String[] ROADS = {"松龄路", "长寿路", "敬老街", "康宁路", "福寿路", "颐和路"};
  private static final String[] SPECIALTIES = {"基础医护", "陪诊服务", "康复护理", "上门保洁", "助餐服务", "水电维修", "心理陪护", "跑腿代办"};
  private static final String[] BINDING_REMARKS = {"老人已授权家属查看健康记录", "等待老人确认位置授权", "家属长期协助就医陪诊", "家属负责日常生活安排"};
  private static final String[] CONTENT_TITLES = {
      "社区春季义诊活动通知", "高血压老人饮食建议", "养老补贴申请指南", "居家防跌倒安全提醒", "老年人慢病随访安排",
      "社区助餐服务开放公告", "夏季防暑健康提示", "智能手机基础课程招募", "家庭陪护沟通建议", "老年人体检预约流程"
  };
  private static final String[] POST_TITLES = {
      "今天参加了社区义诊，医生很耐心", "想咨询一下上门保洁服务体验", "最近血压控制得不错，分享一下经验", "社区活动中心的书法课很有意思",
      "有谁用过陪诊服务吗", "给大家推荐低盐低脂家常菜", "刚刚报名了健康讲座", "家里老人想找维修师傅上门", "跑腿代办服务效率挺高", "社区舞蹈活动很热闹"
  };
  private static final String[] PORTAL_MODULES = {"账号中心", "信息浏览", "社区互动", "服务大厅", "订单中心", "健康管理", "家属授权", "消息中心", "位置与安全", "个人服务评价"};
  private static final String[] MODULE_STATUS_SUFFIX = {
      "资料已完善", "内容已上架", "帖子已审核", "服务可预约", "订单跟进中",
      "档案已更新", "授权待确认", "消息已送达", "安全状态正常", "评价已归档"
  };
  private static final String[] COMMUNITY_CATEGORIES = {"论坛交流", "活动分享", "健康打卡", "服务反馈"};
  private static final String[] SERVICE_ORDER_REMARKS = {"需要提前半小时电话联系", "老人腿脚不便，请协助上下楼", "服务完成后请同步家属", "希望安排熟悉社区路线的服务人员"};
  private static final String[] MESSAGE_TITLES = {
      "服务预约提醒", "家属授权提醒", "健康打卡提醒", "位置安全提醒", "社区活动通知",
      "订单支付提醒", "服务完成回访", "老人关怀通知", "社区讲座邀请", "满意度评价提醒"
  };
  private static final String[] MESSAGE_CONTENTS = {
      "您在服务大厅预约的上门服务已进入处理流程，请及时查看详情。",
      "有新的家属授权申请待处理，建议核对关系后尽快完成确认。",
      "今日健康打卡尚未完成，建议记录血压、血糖等关键指标。",
      "系统检测到您最近一次位置上报接近安全边界，请注意出行安全。",
      "社区服务中心本周将开展义诊、助餐和防诈骗宣讲活动，欢迎参加。"
  };
  private static final String[] ACCOUNT_SIGNATURES = {"健康常伴，平安喜乐", "日子安稳，心里踏实", "把身体照顾好，就是给家人最大的安心", "社区有温度，生活更方便"};
  private static final String[] ACCOUNT_BIOS = {
      "退休后定居社区，平时喜欢散步、听戏和参加社区活动。",
      "注重规律作息和慢病管理，希望把生活安排得更从容。",
      "和家属保持日常沟通，习惯使用平台查看服务和健康提醒。",
      "常参加社区课堂和义诊活动，愿意体验便民养老服务。"
  };
  private static final String[] STAFF_PORTAL_MODULES = {"账号与资料", "接单中心", "日程管理", "服务执行", "服务记录", "消息提醒", "个人中心"};
  private static final String[] STAFF_MODULE_STATUS_SUFFIX = {
      "资料已完善", "订单待接收", "日程已排班", "服务执行中", "记录已归档", "提醒已送达", "主页已更新"
  };
  private static final String[] STAFF_ACCOUNT_BIOS = {
      "长期从事社区养老上门服务，熟悉老人陪护、陪诊与基础照护流程。",
      "注重服务细节与沟通反馈，擅长慢病老人日常照护与上门协助。",
      "常驻社区周边片区，可快速响应居家服务、巡访与应急协助需求。",
      "具备较强的老人沟通能力和服务执行经验，重视服务记录留痕。"
  };
  private static final String[] STAFF_SIGNATURES = {"把每次上门服务做到让老人安心", "及时响应，细致服务，过程留痕", "服务有温度，执行有标准", "让每一次接单都有结果反馈"};
  private static final String[] STAFF_ORDER_REMARKS = {
      "请提前联系老人确认在家时间，并带好基础服务工具。",
      "本次服务需同步拍照留档，服务完成后补充服务记录。",
      "建议优先处理老人诉求，再向家属反馈执行结果。",
      "如遇特殊情况请在消息中心反馈，并更新日程状态。"
  };
  private static final String[] STAFF_MESSAGE_TITLES = {
      "新派单提醒", "服务开始提醒", "服务记录补录提醒", "日程调整提醒", "老人回访提醒",
      "服务评价通知", "资料审核通知", "执行异常提醒", "今日排班提醒", "消息回执通知"
  };
  private static final String[] STAFF_MESSAGE_CONTENTS = {
      "您有新的服务订单待处理，请在接单中心查看详情并及时接单。",
      "系统检测到预约时间临近，建议确认路线后准时上门开始服务。",
      "有服务执行记录待补充，请在服务记录中完善本次上门情况。",
      "今日排班出现调整，请核对服务地址和时间后更新个人日程。",
      "老人服务已结束，请尽快查看评价结果并处理后续回访。"
  };
  private static final String[] FAMILY_PORTAL_MODULES = {"绑定管理", "老人信息查看", "健康监测", "服务同步", "位置查看", "提醒中心", "互动协同"};
  private static final String[] FAMILY_MODULE_STATUS_SUFFIX = {
      "绑定待确认", "档案已同步", "指标已更新", "订单已同步", "位置已刷新", "提醒已送达", "协同处理中"
  };
  private static final String[] FAMILY_ACCOUNT_BIOS = {
      "长期关注家中老人健康与出行安全，习惯通过平台同步服务和预警信息。",
      "主要负责长辈日常照护协同，关注健康记录、服务进度和位置安全。",
      "和老人保持高频沟通，重视异常提醒、服务回访和授权管理。",
      "负责家庭协同照护，常查看老人档案、订单状态和健康波动。"
  };
  private static final String[] FAMILY_SIGNATURES = {"多一份同步，少一份担心", "及时查看、及时沟通、及时协同", "把老人每天的状态放在心上", "服务和健康信息同步更安心"};
  private static final String[] FAMILY_BINDING_REMARKS = {
      "已查看详情，等待老人确认本次授权申请。",
      "授权信息已编辑，计划同步查看健康和位置数据。",
      "协同备注已完善，如需变更可删除后重新申请。",
      "本次绑定用于日常陪诊、服务跟进和健康协同。"
  };
  private static final String[] FAMILY_ORDER_REMARKS = {
      "家属已同步查看订单详情，建议服务完成后及时回访。",
      "本单用于陪诊协同，需关注服务开始和完成状态。",
      "已编辑协同备注，等待服务人员更新执行记录。",
      "如遇变更请及时在提醒中心查看消息并处理。"
  };
  private static final String[] FAMILY_MESSAGE_TITLES = {
      "绑定审核结果提醒", "健康异常提醒", "位置安全提醒", "服务进度提醒", "老人回访提醒",
      "授权变更提醒", "互动协同提醒", "服务评价提醒", "家属消息回执", "老人档案更新提醒"
  };
  private static final String[] FAMILY_MESSAGE_CONTENTS = {
      "您发起的老人绑定申请已有新进度，请在绑定管理中查看详情。",
      "系统检测到老人健康指标波动，请尽快进入健康监测查看详情。",
      "最近一次位置上报已刷新，如出现越界请及时联系老人或社区人员。",
      "服务订单状态有更新，请在服务同步中查看当前执行进度。",
      "老人档案或授权信息发生变化，建议进入互动协同完成后续处理。"
  };
  private static final String[] LOG_ACTIONS = {"查看详情", "编辑资料", "删除记录", "审核通过", "审核驳回", "派单处理", "发布通知", "下架内容"};
  private static final String[] LOG_MODULES = {"用户管理", "老人档案管理", "家属关系管理", "服务人员管理", "服务项目管理", "订单调度管理", "内容管理", "社区互动管理", "消息通知管理", "统计与系统管理"};
  private static final String[] LOG_PATHS = {"users", "elder-profiles", "family-bindings", "staff-profiles", "service-projects", "service-orders", "contents", "community-posts", "messages", "system"};

  private final Faker faker = new Faker(Locale.SIMPLIFIED_CHINESE);
  private final UserMapper userMapper;
  private final UserRoleDomainService userRoleDomainService;
  private final ElderProfileMapper elderProfileMapper;
  private final StaffProfileMapper staffProfileMapper;
  private final FamilyBindingMapper familyBindingMapper;
  private final ServiceCategoryMapper serviceCategoryMapper;
  private final ServiceProjectMapper serviceProjectMapper;
  private final ServiceOrderMapper serviceOrderMapper;
  private final ContentMapper contentMapper;
  private final CommunityPostMapper communityPostMapper;
  private final HealthRecordMapper healthRecordMapper;
  private final HealthAlertMapper healthAlertMapper;
  private final MsgMapper msgMapper;
  private final ExternalCallLogMapper externalCallLogMapper;
  private final SafeZoneMapper safeZoneMapper;
  private final LocationRecordMapper locationRecordMapper;
  private final OpLogMapper opLogMapper;

  public UserSeedRunner(
      UserMapper userMapper,
      UserRoleDomainService userRoleDomainService,
      ElderProfileMapper elderProfileMapper,
      StaffProfileMapper staffProfileMapper,
      FamilyBindingMapper familyBindingMapper,
      ServiceCategoryMapper serviceCategoryMapper,
      ServiceProjectMapper serviceProjectMapper,
      ServiceOrderMapper serviceOrderMapper,
      ContentMapper contentMapper,
      CommunityPostMapper communityPostMapper,
      HealthRecordMapper healthRecordMapper,
      HealthAlertMapper healthAlertMapper,
      MsgMapper msgMapper,
      ExternalCallLogMapper externalCallLogMapper,
      SafeZoneMapper safeZoneMapper,
      LocationRecordMapper locationRecordMapper,
      OpLogMapper opLogMapper) {
    this.userMapper = userMapper;
    this.userRoleDomainService = userRoleDomainService;
    this.elderProfileMapper = elderProfileMapper;
    this.staffProfileMapper = staffProfileMapper;
    this.familyBindingMapper = familyBindingMapper;
    this.serviceCategoryMapper = serviceCategoryMapper;
    this.serviceProjectMapper = serviceProjectMapper;
    this.serviceOrderMapper = serviceOrderMapper;
    this.contentMapper = contentMapper;
    this.communityPostMapper = communityPostMapper;
    this.healthRecordMapper = healthRecordMapper;
    this.healthAlertMapper = healthAlertMapper;
    this.msgMapper = msgMapper;
    this.externalCallLogMapper = externalCallLogMapper;
    this.safeZoneMapper = safeZoneMapper;
    this.locationRecordMapper = locationRecordMapper;
    this.opLogMapper = opLogMapper;
  }

  @Override
  public void run(String... args) {
    ensureAdminUser();
    seedDemoData();
  }

  private void ensureAdminUser() {
    UserEntity admin = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
        .eq(UserEntity::getUsername, "admin"));
    if (admin == null) {
      admin = new UserEntity();
      admin.setUsername("admin");
      admin.setPasswordHash(PasswordUtils.hash("123456"));
      admin.setNickname("管理员");
      admin.setGender(1);
      admin.setStatus(GlobalConstants.STATUS_ENABLED);
      admin.setPhone(chinaPhone());
      userMapper.insert(admin);
    }
    userRoleDomainService.replaceRole(admin.getId(), GlobalConstants.ROLE_ADMIN);
  }

  private void seedDemoData() {
    List<UserEntity> elders = seedUsers("elder", 20, GlobalConstants.ROLE_ELDER, true);
    List<UserEntity> families = seedUsers("family", 20, GlobalConstants.ROLE_FAMILY, false);
    List<UserEntity> staffs = seedUsers("staff", 20, GlobalConstants.ROLE_STAFF, false);
    seedUsers("adminx", 2, GlobalConstants.ROLE_ADMIN, false);

    seedElderProfiles(elders);
    seedStaffProfiles(staffs);
    seedSafeZones(elders);
    List<ServiceCategoryEntity> categories = seedServiceCategories();
    List<ServiceProjectEntity> projects = seedServiceProjects(categories, staffs);
    seedFamilyBindings(elders, families);
    seedContents();
    seedCommunityPosts(elders);
    seedOrders(elders, families, staffs, projects);
    seedHealthRecordsAndAlerts(elders);
    seedMessages(elders, families, staffs);
    seedExternalLogs();
    seedOpLogs();
    seedFocusedElderPortalData(elders, families, staffs, projects);
    seedFocusedStaffPortalData(elders, families, staffs, projects);
    seedFocusedFamilyPortalData(elders, families, staffs, projects);
  }

  private void seedFocusedElderPortalData(List<UserEntity> elders, List<UserEntity> families,
                                          List<UserEntity> staffs, List<ServiceProjectEntity> projects) {
    if (elders.isEmpty()) {
      return;
    }
    UserEntity elder = elders.get(0);
    enrichPortalAccount(elder);
    seedPortalContents();
    seedPortalCommunityPosts(elder);
    seedPortalBindings(elder, families);
    seedPortalOrders(elders, families, staffs, projects);
    seedPortalHealthRecordsAndAlerts(elder);
    seedPortalMessages(elder);
    seedPortalLocations(elder);
    seedPortalOpLogs(elder);
  }

  private void seedFocusedStaffPortalData(List<UserEntity> elders, List<UserEntity> families,
                                          List<UserEntity> staffs, List<ServiceProjectEntity> projects) {
    if (staffs.isEmpty()) {
      return;
    }
    UserEntity staff = staffs.get(0);
    enrichStaffPortalAccount(staff);
    seedStaffPortalOrders(elders, families, staff, projects);
    seedStaffPortalMessages(staff);
    seedStaffPortalOpLogs(staff);
  }

  private void seedFocusedFamilyPortalData(List<UserEntity> elders, List<UserEntity> families,
                                           List<UserEntity> staffs, List<ServiceProjectEntity> projects) {
    if (families.isEmpty()) {
      return;
    }
    UserEntity family = families.get(0);
    enrichFamilyPortalAccount(family);
    seedFamilyPortalBindings(elders, family);
    seedFamilyPortalOrders(elders, family, staffs, projects);
    seedFamilyPortalMessages(family);
    seedFamilyPortalOpLogs(family);
  }

  private List<UserEntity> seedUsers(String prefix, int count, String role, boolean elderRole) {
    List<UserEntity> users = new ArrayList<>();
    for (int i = 1; i <= count; i++) {
      String username = DEMO_PREFIX + prefix + "_" + i;
      UserEntity user = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
          .eq(UserEntity::getUsername, username));
      if (user == null) {
        user = new UserEntity();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtils.hash("123456"));
        user.setNickname(buildNickname(role, i, elderRole));
        user.setEmail(username + "@yanglao.cn");
        user.setPhone(chinaPhone());
        user.setGender(i % 2 == 0 ? 2 : 1);
        user.setBirthday(LocalDate.now().minusYears(elderRole ? randomBetween(60, 82) : randomBetween(25, 48)));
        user.setBio(buildBio(role));
        user.setSignature(pick(new String[]{"健康相伴，安心养老", "把服务做到老人心里", "一家人一起守护长辈", "让社区服务更温暖"}));
        user.setRegion("上海市");
        user.setCity("上海");
        user.setStatus(i % 11 == 0 ? GlobalConstants.STATUS_DISABLED : GlobalConstants.STATUS_ENABLED);
        userMapper.insert(user);
      }
      userRoleDomainService.replaceRole(user.getId(), role);
      users.add(userMapper.selectById(user.getId()));
    }
    return users;
  }

  private void seedElderProfiles(List<UserEntity> elders) {
    for (int i = 0; i < elders.size(); i++) {
      UserEntity user = elders.get(i);
      ElderProfileEntity profile = elderProfileMapper.selectOne(new LambdaQueryWrapper<ElderProfileEntity>()
          .eq(ElderProfileEntity::getUserId, user.getId()));
      if (profile != null) {
        continue;
      }
      profile = new ElderProfileEntity();
      profile.setUserId(user.getId());
      profile.setRealName(buildChineseName(ELDER_NAMES));
      profile.setIdCard("31010119" + faker.number().digits(10));
      profile.setEmergencyContactName(buildChineseName(FAMILY_NAMES));
      profile.setEmergencyContactPhone(chinaPhone());
      profile.setAddress(pick(COMMUNITIES) + " " + pick(ROADS) + randomBetween(1, 20) + "号" + randomBetween(1, 6) + "栋" + randomBetween(101, 602) + "室");
      profile.setHealthNotes(pick(new String[]{"高血压定期监测", "糖尿病饮食控制", "膝关节不适注意出行安全", "心血管慢病需按时服药"}));
      profile.setFamilyHealthAuthorized(i % 5 == 0 ? 0 : 1);
      profile.setFamilyLocationAuthorized(i % 4 == 0 ? 0 : 1);
      elderProfileMapper.insert(profile);
    }
  }

  private void enrichPortalAccount(UserEntity elder) {
    elder.setNickname("演示老人" + buildChineseName(ELDER_NAMES));
    elder.setPhone(chinaPhone());
    elder.setEmail("demo_elder_portal@yanglao.cn");
    elder.setBirthday(LocalDate.now().minusYears(randomBetween(66, 79)));
    elder.setRegion("上海市浦东新区");
    elder.setCity("上海");
    elder.setSignature(pick(ACCOUNT_SIGNATURES));
    elder.setBio(pick(ACCOUNT_BIOS));
    elder.setStatus(GlobalConstants.STATUS_ENABLED);
    userMapper.updateById(elder);

    ElderProfileEntity profile = elderProfileMapper.selectOne(new LambdaQueryWrapper<ElderProfileEntity>()
        .eq(ElderProfileEntity::getUserId, elder.getId()));
    if (profile == null) {
      profile = new ElderProfileEntity();
      profile.setUserId(elder.getId());
      elderProfileMapper.insert(profile);
    }
    profile.setRealName(buildChineseName(ELDER_NAMES));
    profile.setIdCard("31010119" + faker.number().digits(10));
    profile.setEmergencyContactName(buildChineseName(FAMILY_NAMES));
    profile.setEmergencyContactPhone(chinaPhone());
    profile.setAddress("上海市浦东新区" + pick(COMMUNITIES) + pick(ROADS) + randomBetween(8, 66) + "号" + randomBetween(1, 8) + "栋" + randomBetween(101, 1802) + "室");
    profile.setHealthNotes(pick(new String[]{"近期血压波动较大，建议保持晨起测量。", "需要持续关注餐后血糖，注意清淡饮食。", "膝关节偶有不适，外出建议家属陪同。", "睡眠质量一般，晚间减少浓茶和咖啡摄入。"}));
    profile.setFamilyHealthAuthorized(1);
    profile.setFamilyLocationAuthorized(1);
    elderProfileMapper.updateById(profile);
  }

  private void seedStaffProfiles(List<UserEntity> staffs) {
    for (int i = 0; i < staffs.size(); i++) {
      UserEntity user = staffs.get(i);
      StaffProfileEntity profile = staffProfileMapper.selectOne(new LambdaQueryWrapper<StaffProfileEntity>()
          .eq(StaffProfileEntity::getUserId, user.getId()));
      if (profile != null) {
        continue;
      }
      profile = new StaffProfileEntity();
      profile.setUserId(user.getId());
      profile.setRealName(buildChineseName(STAFF_NAMES));
      profile.setCertificateNo("CERT-" + faker.number().digits(6));
      profile.setSpecialty(pick(SPECIALTIES));
      profile.setServiceRadiusKm(randomBetween(3, 12));
      profile.setIntro("具备" + randomBetween(2, 9) + "年社区养老服务经验，擅长" + pick(SPECIALTIES) + "。");
      profile.setAuditStatus(pick(new String[]{CareConstants.STAFF_AUDIT_APPROVED, CareConstants.STAFF_AUDIT_PENDING, CareConstants.STAFF_AUDIT_REJECTED}));
      staffProfileMapper.insert(profile);
    }
  }

  private void enrichStaffPortalAccount(UserEntity staff) {
    staff.setNickname("演示服务人员" + buildChineseName(STAFF_NAMES));
    staff.setPhone(chinaPhone());
    staff.setEmail("demo_staff_portal@yanglao.cn");
    staff.setBirthday(LocalDate.now().minusYears(randomBetween(28, 46)));
    staff.setRegion("上海市浦东新区");
    staff.setCity("上海");
    staff.setSignature(pick(STAFF_SIGNATURES));
    staff.setBio(pick(STAFF_ACCOUNT_BIOS));
    staff.setStatus(GlobalConstants.STATUS_ENABLED);
    userMapper.updateById(staff);

    StaffProfileEntity profile = staffProfileMapper.selectOne(new LambdaQueryWrapper<StaffProfileEntity>()
        .eq(StaffProfileEntity::getUserId, staff.getId()));
    if (profile == null) {
      profile = new StaffProfileEntity();
      profile.setUserId(staff.getId());
      profile.setAuditStatus(CareConstants.STAFF_AUDIT_PENDING);
      staffProfileMapper.insert(profile);
    }
    profile.setRealName(buildChineseName(STAFF_NAMES));
    profile.setCertificateNo("STAFF-" + faker.number().digits(8));
    profile.setSpecialty(pick(SPECIALTIES));
    profile.setServiceRadiusKm(randomBetween(5, 15));
    profile.setIntro("【账号与资料】"
        + pick(new String[]{"已编辑服务简介", "已完善资质信息", "已查看资料详情", "资料可删除归档"}) + "；擅长"
        + pick(SPECIALTIES) + "、社区老人陪护与上门执行。");
    profile.setAuditStatus(pick(new String[]{CareConstants.STAFF_AUDIT_APPROVED, CareConstants.STAFF_AUDIT_APPROVED, CareConstants.STAFF_AUDIT_PENDING}));
    staffProfileMapper.updateById(profile);
  }

  private void enrichFamilyPortalAccount(UserEntity family) {
    family.setNickname("演示家属" + buildChineseName(FAMILY_NAMES));
    family.setPhone(chinaPhone());
    family.setEmail("demo_family_portal@yanglao.cn");
    family.setBirthday(LocalDate.now().minusYears(randomBetween(28, 52)));
    family.setRegion("上海市浦东新区");
    family.setCity("上海");
    family.setSignature(pick(FAMILY_SIGNATURES));
    family.setBio(pick(FAMILY_ACCOUNT_BIOS));
    family.setStatus(GlobalConstants.STATUS_ENABLED);
    userMapper.updateById(family);
  }

  private void seedSafeZones(List<UserEntity> elders) {
    for (int i = 0; i < elders.size(); i++) {
      UserEntity user = elders.get(i);
      SafeZoneEntity zone = safeZoneMapper.selectOne(new LambdaQueryWrapper<SafeZoneEntity>()
          .eq(SafeZoneEntity::getUserId, user.getId()));
      if (zone != null) {
        continue;
      }
      zone = new SafeZoneEntity();
      zone.setUserId(user.getId());
      zone.setCenterLatitude(decimal(31.21 + i * 0.001));
      zone.setCenterLongitude(decimal(121.45 + i * 0.001));
      zone.setRadiusMeters(randomBetween(300, 1200));
      zone.setAddress(pick(COMMUNITIES) + "中心广场");
      safeZoneMapper.insert(zone);
    }
  }

  private List<ServiceCategoryEntity> seedServiceCategories() {
    String[][] defs = {
        {"家政服务", "HOUSEKEEPING", "保洁、做饭、陪护等上门服务"},
        {"跑腿代办", "ERRAND", "买菜、取件、缴费等代办事项"},
        {"基础医护", "CARE", "血压测量、陪诊、慢病随访"},
        {"居家维修", "REPAIR", "水电检修、家电维护、门锁更换"},
        {"精神慰藉", "COMPANION", "聊天陪伴、兴趣活动、心理关怀"}
    };
    List<ServiceCategoryEntity> list = new ArrayList<>();
    for (int i = 0; i < defs.length; i++) {
      String[] def = defs[i];
      ServiceCategoryEntity entity = serviceCategoryMapper.selectOne(new LambdaQueryWrapper<ServiceCategoryEntity>()
          .eq(ServiceCategoryEntity::getCode, def[1]));
      if (entity == null) {
        entity = new ServiceCategoryEntity();
        entity.setName(def[0]);
        entity.setCode(def[1]);
        entity.setDescription(def[2]);
        entity.setSortNo((i + 1) * 10);
        entity.setStatus(GlobalConstants.STATUS_ENABLED);
        serviceCategoryMapper.insert(entity);
      }
      list.add(serviceCategoryMapper.selectById(entity.getId()));
    }
    return list;
  }

  private List<ServiceProjectEntity> seedServiceProjects(List<ServiceCategoryEntity> categories, List<UserEntity> staffs) {
    List<String> projectNames = List.of(
        "上门保洁", "居家做饭", "助浴护理", "陪同就医", "代买代送", "药品代取", "生活缴费代办", "血压测量",
        "血糖监测", "康复训练陪护", "水龙头维修", "灯具更换", "门锁检修", "家电调试", "陪伴聊天",
        "兴趣课堂陪护", "助餐配送", "上门理发", "手机使用指导", "防诈骗宣传入户"
    );
    List<ServiceProjectEntity> list = new ArrayList<>();
    for (int i = 0; i < projectNames.size(); i++) {
      String name = projectNames.get(i);
      ServiceProjectEntity entity = serviceProjectMapper.selectOne(new LambdaQueryWrapper<ServiceProjectEntity>()
          .eq(ServiceProjectEntity::getName, name));
      if (entity == null) {
        ServiceCategoryEntity category = categories.get(i % categories.size());
        UserEntity staff = staffs.get(i % staffs.size());
        entity = new ServiceProjectEntity();
        entity.setCategoryId(category.getId());
        entity.setName(name);
        entity.setDescription(name + "服务，适合社区老人居家场景，支持预约上门。");
        entity.setPrice(BigDecimal.valueOf(randomBetween(20, 120)).setScale(2, RoundingMode.HALF_UP));
        entity.setUnit("次");
        entity.setStatus(i % 7 == 0 ? 0 : 1);
        entity.setDefaultStaffUserId(staff.getId());
        entity.setServiceDurationMinutes(randomBetween(30, 180));
        serviceProjectMapper.insert(entity);
      }
      list.add(serviceProjectMapper.selectById(entity.getId()));
    }
    return list;
  }

  private void seedFamilyBindings(List<UserEntity> elders, List<UserEntity> families) {
    if (familyBindingMapper.selectCount(new LambdaQueryWrapper<FamilyBindingEntity>()
        .like(FamilyBindingEntity::getRemark, "演示绑定")) >= 20) {
      return;
    }
    for (int i = 0; i < 20; i++) {
      UserEntity elder = elders.get(i % elders.size());
      UserEntity family = families.get(i % families.size());
      FamilyBindingEntity existing = familyBindingMapper.selectOne(new LambdaQueryWrapper<FamilyBindingEntity>()
          .eq(FamilyBindingEntity::getElderUserId, elder.getId())
          .eq(FamilyBindingEntity::getFamilyUserId, family.getId()));
      if (existing != null) {
        continue;
      }
      FamilyBindingEntity entity = new FamilyBindingEntity();
      entity.setElderUserId(elder.getId());
      entity.setFamilyUserId(family.getId());
      entity.setRelationLabel(pick(new String[]{"女儿", "儿子", "孙女", "孙子", "女婿", "外孙"}));
      entity.setStatus(pick(new String[]{CareConstants.BINDING_ACTIVE, CareConstants.BINDING_PENDING, CareConstants.BINDING_REJECTED}));
      entity.setHealthAccess(i % 3 == 0 ? 0 : 1);
      entity.setLocationAccess(i % 4 == 0 ? 0 : 1);
      entity.setRemark("演示绑定：" + pick(BINDING_REMARKS));
      familyBindingMapper.insert(entity);
    }
  }

  private void seedContents() {
    if (contentMapper.selectCount(new LambdaQueryWrapper<ContentEntity>()
        .like(ContentEntity::getAuthorName, "演示运营")) >= 20) {
      return;
    }
    String[] types = {CareConstants.CONTENT_NOTICE, CareConstants.CONTENT_NEWS, CareConstants.CONTENT_POLICY, CareConstants.CONTENT_HEALTH, CareConstants.CONTENT_ACTIVITY};
    for (int i = 0; i < 20; i++) {
      ContentEntity entity = new ContentEntity();
      entity.setContentType(types[i % types.length]);
      entity.setTitle(CONTENT_TITLES[i % CONTENT_TITLES.length] + "（第" + (i + 1) + "期）");
      entity.setSummary("围绕社区养老、健康科普和服务通知整理的中文演示内容。");
      entity.setContent("【演示内容】" + entity.getTitle() + "。本条数据由 DataFaker 驱动生成，用于后台内容管理、详情查看、编辑删除等演示场景。");
      entity.setStatus(i % 6 == 0 ? 0 : 1);
      entity.setAuthorName("演示运营" + (i % 4 + 1));
      entity.setPublishedAt(LocalDateTime.now().minusDays(randomBetween(1, 60)).with(LocalTime.of(randomBetween(8, 20), randomBetween(0, 59))));
      entity.setViewCount((long) randomBetween(50, 1200));
      contentMapper.insert(entity);
    }
  }

  private void seedPortalContents() {
    if (contentMapper.selectCount(new LambdaQueryWrapper<ContentEntity>()
        .like(ContentEntity::getTitle, "老人端信息浏览演示")) >= 20) {
      return;
    }
    String[] types = {CareConstants.CONTENT_NOTICE, CareConstants.CONTENT_NEWS, CareConstants.CONTENT_POLICY, CareConstants.CONTENT_HEALTH, CareConstants.CONTENT_ACTIVITY};
    for (int i = 0; i < 20; i++) {
      ContentEntity entity = new ContentEntity();
      entity.setContentType(types[i % types.length]);
      entity.setTitle("老人端信息浏览演示第" + (i + 1) + "条：" + pick(CONTENT_TITLES));
      entity.setSummary("由 DataFaker 组合生成的中文资讯摘要，适用于信息浏览、详情查看和后台内容维护场景。");
      entity.setContent("【信息浏览模块】" + entity.getTitle()
          + "。当前操作状态：" + pick(new String[]{"详情已查看", "内容可编辑", "记录可删除", "已发布到老人端"})
          + "；模块状态：" + MODULE_STATUS_SUFFIX[1] + "。");
      entity.setStatus(i % 8 == 0 ? 0 : 1);
      entity.setAuthorName("老人端内容运营" + (i % 5 + 1));
      entity.setPublishedAt(LocalDateTime.now().minusDays(randomBetween(1, 45)).with(LocalTime.of(randomBetween(8, 20), randomBetween(0, 59))));
      entity.setViewCount((long) randomBetween(120, 3600));
      contentMapper.insert(entity);
    }
  }

  private void seedCommunityPosts(List<UserEntity> elders) {
    if (communityPostMapper.selectCount(new LambdaQueryWrapper<CommunityPostEntity>()
        .like(CommunityPostEntity::getAuthorName, "演示")) >= 20) {
      return;
    }
    for (int i = 0; i < 20; i++) {
      UserEntity author = elders.get(i % elders.size());
      CommunityPostEntity entity = new CommunityPostEntity();
      entity.setAuthorId(author.getId());
      entity.setAuthorName("演示" + author.getNickname());
      entity.setTitle(POST_TITLES[i % POST_TITLES.length] + "（第" + (i + 1) + "帖）");
      entity.setContent("【社区互动演示】" + entity.getTitle() + "，分享社区活动、健康管理和服务体验，便于管理员进行详情查看、编辑和删除审核。");
      entity.setCategory(pick(new String[]{"论坛交流", "活动分享", "健康打卡", "服务反馈"}));
      entity.setPostStatus(pick(new String[]{"PUBLISHED", "HIDDEN", "DELETED"}));
      entity.setAuditStatus(pick(new String[]{"APPROVED", "PENDING", "REJECTED"}));
      entity.setCommentCount(randomBetween(0, 36));
      entity.setLikeCount(randomBetween(6, 180));
      entity.setViewCount(randomBetween(30, 900));
      entity.setLastAction(pick(new String[]{"查看详情", "编辑内容", "删除帖子"}));
      entity.setLastActionTime(LocalDateTime.now().minusDays(randomBetween(0, 30)));
      communityPostMapper.insert(entity);
    }
  }

  private void seedPortalCommunityPosts(UserEntity elder) {
    if (communityPostMapper.selectCount(new LambdaQueryWrapper<CommunityPostEntity>()
        .like(CommunityPostEntity::getTitle, "老人端社区互动演示")) >= 20) {
      return;
    }
    for (int i = 0; i < 20; i++) {
      CommunityPostEntity entity = new CommunityPostEntity();
      entity.setAuthorId(elder.getId());
      entity.setAuthorName(elder.getNickname());
      entity.setTitle("老人端社区互动演示第" + (i + 1) + "帖：" + pick(POST_TITLES));
      entity.setContent("【社区互动模块】"
          + pick(new String[]{"分享今天参加社区活动的感受。", "记录一次健康打卡后的心得。", "交流上门服务和陪诊体验。", "咨询邻里关于社区课程的报名建议。"})
          + " 当前可执行操作：详情、编辑、删除；最近操作状态：" + pick(new String[]{"已编辑正文", "已查看详情", "已申请删除", "审核通过待展示"}) + "。");
      entity.setCategory(COMMUNITY_CATEGORIES[i % COMMUNITY_CATEGORIES.length]);
      entity.setPostStatus(pick(new String[]{"PUBLISHED", "HIDDEN", "DELETED"}));
      entity.setAuditStatus(pick(new String[]{"APPROVED", "PENDING", "REJECTED"}));
      entity.setCommentCount(randomBetween(1, 48));
      entity.setLikeCount(randomBetween(12, 220));
      entity.setViewCount(randomBetween(80, 1500));
      entity.setLastAction(pick(new String[]{"查看详情", "编辑帖子", "删除帖子", "重新提交审核"}));
      entity.setLastActionTime(LocalDateTime.now().minusDays(randomBetween(0, 20)).minusHours(randomBetween(0, 23)));
      communityPostMapper.insert(entity);
    }
  }

  private void seedOrders(List<UserEntity> elders, List<UserEntity> families, List<UserEntity> staffs, List<ServiceProjectEntity> projects) {
    if (serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .like(ServiceOrderEntity::getOrderNo, "DEMO")) >= 20) {
      return;
    }
    String[] statuses = {
        CareConstants.ORDER_PENDING_PAYMENT, CareConstants.ORDER_PENDING_ASSIGN, CareConstants.ORDER_PENDING_ACCEPT,
        CareConstants.ORDER_PENDING_SERVICE, CareConstants.ORDER_IN_SERVICE, CareConstants.ORDER_COMPLETED,
        CareConstants.ORDER_CANCELLED, CareConstants.ORDER_REFUNDED
    };
    for (int i = 0; i < 20; i++) {
      UserEntity elder = elders.get(i % elders.size());
      UserEntity family = families.get(i % families.size());
      UserEntity staff = staffs.get(i % staffs.size());
      ServiceProjectEntity project = projects.get(i % projects.size());
      ServiceCategoryEntity category = serviceCategoryMapper.selectById(project.getCategoryId());
      ServiceOrderEntity entity = new ServiceOrderEntity();
      entity.setOrderNo("DEMO" + faker.number().digits(12));
      entity.setElderUserId(elder.getId());
      entity.setFamilyUserId(i % 3 == 0 ? family.getId() : null);
      entity.setStaffUserId(i % 2 == 0 ? staff.getId() : null);
      entity.setServiceProjectId(project.getId());
      entity.setServiceName(project.getName());
      entity.setCategoryName(category == null ? "社区服务" : category.getName());
      entity.setServiceTime(LocalDateTime.now().plusDays(randomBetween(-15, 20)).with(LocalTime.of(randomBetween(8, 18), 0)));
      entity.setServiceAddress(pick(COMMUNITIES) + pick(ROADS) + randomBetween(1, 30) + "号");
      entity.setContactName(elder.getNickname());
      entity.setContactPhone(elder.getPhone());
      entity.setRemark("演示订单：" + pick(new String[]{"需要提前电话联系", "老人独居请耐心沟通", "家属同步关注订单进度", "上门前确认门牌号"}));
      entity.setAmount(project.getPrice());
      String status = statuses[i % statuses.length];
      entity.setOrderStatus(status);
      entity.setPayStatus(resolvePayStatus(status));
      entity.setPaymentTime(entity.getPayStatus().equals(CareConstants.PAY_PAID) ? LocalDateTime.now().minusDays(randomBetween(1, 10)) : null);
      entity.setAcceptTime(CareConstants.ORDER_PENDING_SERVICE.equals(status) || CareConstants.ORDER_IN_SERVICE.equals(status) || CareConstants.ORDER_COMPLETED.equals(status)
          ? LocalDateTime.now().minusDays(randomBetween(1, 5)) : null);
      entity.setStartTime(CareConstants.ORDER_IN_SERVICE.equals(status) || CareConstants.ORDER_COMPLETED.equals(status)
          ? LocalDateTime.now().minusDays(randomBetween(0, 2)) : null);
      entity.setFinishTime(CareConstants.ORDER_COMPLETED.equals(status) ? LocalDateTime.now().minusHours(randomBetween(1, 12)) : null);
      if (CareConstants.ORDER_COMPLETED.equals(status)) {
        entity.setRating(randomBetween(4, 5));
        entity.setRatingContent(pick(new String[]{"服务态度很好，老人很满意", "上门准时，操作规范", "沟通耐心，值得再次预约"}));
      }
      serviceOrderMapper.insert(entity);
    }
  }

  private void seedPortalOrders(List<UserEntity> elders, List<UserEntity> families, List<UserEntity> staffs, List<ServiceProjectEntity> projects) {
    UserEntity elder = elders.get(0);
    if (serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .eq(ServiceOrderEntity::getElderUserId, elder.getId())
        .like(ServiceOrderEntity::getOrderNo, "PORTAL")) >= 20) {
      return;
    }
    String[] statuses = {
        CareConstants.ORDER_PENDING_PAYMENT, CareConstants.ORDER_PENDING_ASSIGN, CareConstants.ORDER_PENDING_ACCEPT,
        CareConstants.ORDER_PENDING_SERVICE, CareConstants.ORDER_IN_SERVICE, CareConstants.ORDER_COMPLETED,
        CareConstants.ORDER_CANCELLED, CareConstants.ORDER_REFUNDED
    };
    for (int i = 0; i < 20; i++) {
      ServiceProjectEntity project = projects.get(i % projects.size());
      ServiceCategoryEntity category = serviceCategoryMapper.selectById(project.getCategoryId());
      UserEntity family = families.get(i % families.size());
      UserEntity staff = staffs.get(i % staffs.size());
      String status = statuses[i % statuses.length];
      ServiceOrderEntity entity = new ServiceOrderEntity();
      entity.setOrderNo("PORTAL" + faker.number().digits(10));
      entity.setElderUserId(elder.getId());
      entity.setFamilyUserId(i % 2 == 0 ? family.getId() : null);
      entity.setStaffUserId(i % 6 == 0 ? null : staff.getId());
      entity.setServiceProjectId(project.getId());
      entity.setServiceName(project.getName());
      entity.setCategoryName(category == null ? "社区服务" : category.getName());
      entity.setServiceTime(LocalDateTime.now().plusDays(randomBetween(-10, 18)).with(LocalTime.of(randomBetween(8, 18), pick(new Integer[]{0, 30}))));
      entity.setServiceAddress("上海市浦东新区" + pick(COMMUNITIES) + pick(ROADS) + randomBetween(1, 99) + "号");
      entity.setContactName(elder.getNickname());
      entity.setContactPhone(elder.getPhone());
      entity.setRemark("【服务大厅/订单中心】" + pick(SERVICE_ORDER_REMARKS) + "；操作状态：" + pick(new String[]{"详情已查看", "订单可编辑", "订单已删除归档", "等待评价"}) + "。");
      entity.setAmount(project.getPrice());
      entity.setOrderStatus(status);
      entity.setPayStatus(resolvePayStatus(status));
      entity.setPaymentTime(CareConstants.PAY_PAID.equals(entity.getPayStatus()) ? LocalDateTime.now().minusDays(randomBetween(1, 9)) : null);
      entity.setAcceptTime(Set.of(CareConstants.ORDER_PENDING_SERVICE, CareConstants.ORDER_IN_SERVICE, CareConstants.ORDER_COMPLETED).contains(status)
          ? LocalDateTime.now().minusDays(randomBetween(1, 4)) : null);
      entity.setStartTime(Set.of(CareConstants.ORDER_IN_SERVICE, CareConstants.ORDER_COMPLETED).contains(status)
          ? LocalDateTime.now().minusHours(randomBetween(6, 36)) : null);
      entity.setFinishTime(CareConstants.ORDER_COMPLETED.equals(status) ? LocalDateTime.now().minusHours(randomBetween(1, 5)) : null);
      if (CareConstants.ORDER_COMPLETED.equals(status)) {
        entity.setRating(randomBetween(4, 5));
        entity.setRatingContent(pick(new String[]{"服务态度亲切，评价为满意并已归档。", "上门及时，问题处理细致，已完成详情查看。", "老人体验不错，家属建议继续保持服务标准。"}));
      }
      serviceOrderMapper.insert(entity);
    }
  }

  private void seedStaffPortalOrders(List<UserEntity> elders, List<UserEntity> families, UserEntity staff, List<ServiceProjectEntity> projects) {
    if (serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .eq(ServiceOrderEntity::getStaffUserId, staff.getId())
        .like(ServiceOrderEntity::getOrderNo, "STAFFP")) >= 20) {
      return;
    }
    String[] statuses = {
        CareConstants.ORDER_PENDING_ACCEPT, CareConstants.ORDER_PENDING_SERVICE, CareConstants.ORDER_IN_SERVICE,
        CareConstants.ORDER_COMPLETED, CareConstants.ORDER_CANCELLED, CareConstants.ORDER_REFUNDED
    };
    for (int i = 0; i < 20; i++) {
      UserEntity elder = elders.get(i % elders.size());
      UserEntity family = families.get(i % families.size());
      ServiceProjectEntity project = projects.get(i % projects.size());
      ServiceCategoryEntity category = serviceCategoryMapper.selectById(project.getCategoryId());
      String status = statuses[i % statuses.length];
      ServiceOrderEntity entity = new ServiceOrderEntity();
      entity.setOrderNo("STAFFP" + faker.number().digits(9));
      entity.setElderUserId(elder.getId());
      entity.setFamilyUserId(i % 3 == 0 ? family.getId() : null);
      entity.setStaffUserId(staff.getId());
      entity.setServiceProjectId(project.getId());
      entity.setServiceName(project.getName());
      entity.setCategoryName(category == null ? "社区服务" : category.getName());
      entity.setServiceTime(LocalDateTime.now().plusDays(randomBetween(-5, 20)).with(LocalTime.of(randomBetween(8, 18), pick(new Integer[]{0, 30}))));
      entity.setServiceAddress("上海市浦东新区" + pick(COMMUNITIES) + pick(ROADS) + randomBetween(1, 88) + "号");
      entity.setContactName(elder.getNickname());
      entity.setContactPhone(elder.getPhone());
      entity.setRemark("【接单中心/日程管理/服务执行/服务记录】" + pick(STAFF_ORDER_REMARKS)
          + " 操作状态：" + pick(new String[]{"详情已查看", "服务单已编辑", "记录可删除", "执行状态已更新", "日程已调整"}) + "。");
      entity.setAmount(project.getPrice());
      entity.setOrderStatus(status);
      entity.setPayStatus(CareConstants.ORDER_CANCELLED.equals(status) ? pick(new String[]{CareConstants.PAY_UNPAID, CareConstants.PAY_PAID})
          : CareConstants.ORDER_REFUNDED.equals(status) ? CareConstants.PAY_REFUNDED : CareConstants.PAY_PAID);
      entity.setPaymentTime(CareConstants.PAY_PAID.equals(entity.getPayStatus()) ? LocalDateTime.now().minusDays(randomBetween(1, 7)) : null);
      entity.setAcceptTime(Set.of(CareConstants.ORDER_PENDING_SERVICE, CareConstants.ORDER_IN_SERVICE, CareConstants.ORDER_COMPLETED).contains(status)
          ? LocalDateTime.now().minusDays(randomBetween(1, 4)) : null);
      entity.setStartTime(Set.of(CareConstants.ORDER_IN_SERVICE, CareConstants.ORDER_COMPLETED).contains(status)
          ? LocalDateTime.now().minusHours(randomBetween(2, 30)) : null);
      entity.setFinishTime(CareConstants.ORDER_COMPLETED.equals(status)
          ? LocalDateTime.now().minusHours(randomBetween(1, 8)) : null);
      if (CareConstants.ORDER_COMPLETED.equals(status)) {
        entity.setRating(randomBetween(4, 5));
        entity.setRatingContent(pick(new String[]{"服务记录已归档，老人反馈沟通耐心。", "本次执行过程顺畅，详情和评价均已查看。", "上门准时、记录完整，后续回访已安排。"}));
      }
      serviceOrderMapper.insert(entity);
    }
  }

  private void seedFamilyPortalOrders(List<UserEntity> elders, UserEntity family, List<UserEntity> staffs, List<ServiceProjectEntity> projects) {
    if (serviceOrderMapper.selectCount(new LambdaQueryWrapper<ServiceOrderEntity>()
        .eq(ServiceOrderEntity::getFamilyUserId, family.getId())
        .like(ServiceOrderEntity::getOrderNo, "FAMILYP")) >= 20) {
      return;
    }
    String[] statuses = {
        CareConstants.ORDER_PENDING_PAYMENT, CareConstants.ORDER_PENDING_ASSIGN, CareConstants.ORDER_PENDING_ACCEPT,
        CareConstants.ORDER_PENDING_SERVICE, CareConstants.ORDER_IN_SERVICE, CareConstants.ORDER_COMPLETED,
        CareConstants.ORDER_CANCELLED, CareConstants.ORDER_REFUNDED
    };
    for (int i = 0; i < 20; i++) {
      UserEntity elder = elders.get(i % elders.size());
      UserEntity staff = staffs.get(i % staffs.size());
      ServiceProjectEntity project = projects.get(i % projects.size());
      ServiceCategoryEntity category = serviceCategoryMapper.selectById(project.getCategoryId());
      String status = statuses[i % statuses.length];
      ServiceOrderEntity entity = new ServiceOrderEntity();
      entity.setOrderNo("FAMILYP" + faker.number().digits(9));
      entity.setElderUserId(elder.getId());
      entity.setFamilyUserId(family.getId());
      entity.setStaffUserId(i % 5 == 0 ? null : staff.getId());
      entity.setServiceProjectId(project.getId());
      entity.setServiceName(project.getName());
      entity.setCategoryName(category == null ? "社区服务" : category.getName());
      entity.setServiceTime(LocalDateTime.now().plusDays(randomBetween(-8, 18)).with(LocalTime.of(randomBetween(8, 18), pick(new Integer[]{0, 30}))));
      entity.setServiceAddress("上海市浦东新区" + pick(COMMUNITIES) + pick(ROADS) + randomBetween(1, 96) + "号");
      entity.setContactName(elder.getNickname());
      entity.setContactPhone(elder.getPhone());
      entity.setRemark("【服务同步/互动协同】" + FAMILY_ORDER_REMARKS[i % FAMILY_ORDER_REMARKS.length]
          + " 操作状态：" + pick(new String[]{"详情已查看", "协同备注已编辑", "同步记录可删除", "服务状态已跟进"}) + "。");
      entity.setAmount(project.getPrice());
      entity.setOrderStatus(status);
      entity.setPayStatus(resolvePayStatus(status));
      entity.setPaymentTime(CareConstants.PAY_PAID.equals(entity.getPayStatus()) ? LocalDateTime.now().minusDays(randomBetween(1, 8)) : null);
      entity.setAcceptTime(Set.of(CareConstants.ORDER_PENDING_SERVICE, CareConstants.ORDER_IN_SERVICE, CareConstants.ORDER_COMPLETED).contains(status)
          ? LocalDateTime.now().minusDays(randomBetween(1, 4)) : null);
      entity.setStartTime(Set.of(CareConstants.ORDER_IN_SERVICE, CareConstants.ORDER_COMPLETED).contains(status)
          ? LocalDateTime.now().minusHours(randomBetween(3, 28)) : null);
      entity.setFinishTime(CareConstants.ORDER_COMPLETED.equals(status) ? LocalDateTime.now().minusHours(randomBetween(1, 6)) : null);
      if (CareConstants.ORDER_COMPLETED.equals(status)) {
        entity.setRating(randomBetween(4, 5));
        entity.setRatingContent(pick(new String[]{"家属已查看详情，服务过程同步清晰。", "互动协同顺畅，服务记录和回访结果已归档。", "本次服务评价较高，后续健康观察已提醒。"}));
      }
      serviceOrderMapper.insert(entity);
    }
  }

  private void seedHealthRecordsAndAlerts(List<UserEntity> elders) {
    if (healthRecordMapper.selectCount(new LambdaQueryWrapper<HealthRecordEntity>()
        .in(HealthRecordEntity::getUserId, elders.stream().map(UserEntity::getId).toList())) >= 20) {
      return;
    }
    for (int i = 0; i < 24; i++) {
      UserEntity elder = elders.get(i % elders.size());
      HealthRecordEntity record = new HealthRecordEntity();
      record.setUserId(elder.getId());
      record.setRecordDate(LocalDate.now().minusDays(randomBetween(0, 20)));
      record.setWeight(BigDecimal.valueOf(randomBetween(48, 78)).setScale(1, RoundingMode.HALF_UP));
      record.setSystolicBp(randomBetween(118, 168));
      record.setDiastolicBp(randomBetween(70, 102));
      record.setBloodSugar(BigDecimal.valueOf(randomBetween(48, 95) / 10.0).setScale(1, RoundingMode.HALF_UP));
      record.setBloodLipid(BigDecimal.valueOf(randomBetween(38, 72) / 10.0).setScale(1, RoundingMode.HALF_UP));
      record.setPulse(randomBetween(62, 96));
      record.setBodyTemp(BigDecimal.valueOf(randomBetween(360, 382) / 10.0).setScale(1, RoundingMode.HALF_UP));
      record.setCheckInFlag(1);
      record.setNote("演示健康记录，建议按时服药并关注饮食。");
      int abnormal = (record.getSystolicBp() >= 145 || record.getDiastolicBp() >= 95 || record.getBloodSugar().compareTo(new BigDecimal("7.2")) >= 0) ? 1 : 0;
      record.setAbnormalFlag(abnormal);
      healthRecordMapper.insert(record);
      if (abnormal == 1) {
        HealthAlertEntity alert = new HealthAlertEntity();
        alert.setUserId(elder.getId());
        alert.setRecordId(record.getId());
        alert.setAlertType(CareConstants.ALERT_HEALTH);
        alert.setAlertLevel(pick(new String[]{"MEDIUM", "HIGH"}));
        alert.setTitle("老人健康指标异常提醒");
        alert.setContent("演示预警：检测到血压或血糖高于阈值，请联系家属和社区服务人员跟进。");
        alert.setStatus("OPEN");
        healthAlertMapper.insert(alert);
      }
    }
  }

  private void seedPortalHealthRecordsAndAlerts(UserEntity elder) {
    if (healthRecordMapper.selectCount(new LambdaQueryWrapper<HealthRecordEntity>()
        .eq(HealthRecordEntity::getUserId, elder.getId())
        .like(HealthRecordEntity::getNote, "老人端健康管理演示")) >= 20) {
      return;
    }
    for (int i = 0; i < 20; i++) {
      HealthRecordEntity record = new HealthRecordEntity();
      record.setUserId(elder.getId());
      record.setRecordDate(LocalDate.now().minusDays(i));
      record.setWeight(BigDecimal.valueOf(randomBetween(52, 76)).setScale(1, RoundingMode.HALF_UP));
      record.setSystolicBp(randomBetween(116, 166));
      record.setDiastolicBp(randomBetween(68, 100));
      record.setBloodSugar(BigDecimal.valueOf(randomBetween(49, 92) / 10.0).setScale(1, RoundingMode.HALF_UP));
      record.setBloodLipid(BigDecimal.valueOf(randomBetween(41, 70) / 10.0).setScale(1, RoundingMode.HALF_UP));
      record.setPulse(randomBetween(60, 94));
      record.setBodyTemp(BigDecimal.valueOf(randomBetween(362, 381) / 10.0).setScale(1, RoundingMode.HALF_UP));
      record.setCheckInFlag(1);
      record.setNote("老人端健康管理演示第" + (i + 1) + "条："
          + pick(new String[]{"已编辑健康备注", "已查看详情", "异常记录待跟进", "数据已同步家属"}) + "。");
      int abnormal = (record.getSystolicBp() >= 145 || record.getDiastolicBp() >= 95 || record.getBloodSugar().compareTo(new BigDecimal("7.2")) >= 0) ? 1 : 0;
      record.setAbnormalFlag(abnormal);
      healthRecordMapper.insert(record);
      if (abnormal == 1) {
        HealthAlertEntity alert = new HealthAlertEntity();
        alert.setUserId(elder.getId());
        alert.setRecordId(record.getId());
        alert.setAlertType(CareConstants.ALERT_HEALTH);
        alert.setAlertLevel(pick(new String[]{"MEDIUM", "HIGH"}));
        alert.setTitle("老人端健康管理演示预警第" + (i + 1) + "条");
        alert.setContent("检测到血压或血糖波动，模块状态：" + MODULE_STATUS_SUFFIX[5] + "，请查看详情并安排复测。");
        alert.setStatus(i % 4 == 0 ? "RESOLVED" : "OPEN");
        healthAlertMapper.insert(alert);
      }
    }
  }

  private void seedMessages(List<UserEntity> elders, List<UserEntity> families, List<UserEntity> staffs) {
    if (msgMapper.selectCount(new LambdaQueryWrapper<MsgEntity>()
        .like(MsgEntity::getTitle, "演示通知")) >= 20) {
      return;
    }
    List<UserEntity> receivers = new ArrayList<>();
    receivers.addAll(elders);
    receivers.addAll(families);
    receivers.addAll(staffs);
    UserEntity admin = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
        .eq(UserEntity::getUsername, "admin"));
    for (int i = 0; i < 20; i++) {
      UserEntity receiver = receivers.get(i % receivers.size());
      MsgEntity msg = new MsgEntity();
      msg.setSenderId(admin == null ? null : admin.getId());
      msg.setReceiverId(receiver.getId());
      msg.setMsgType(pick(new String[]{"SYSTEM", "NOTICE", "ALERT"}));
      msg.setTitle("演示通知第" + (i + 1) + "条");
      msg.setContent(pick(new String[]{"您预约的服务时间已调整，请及时查看订单详情。", "社区健康讲座将于本周五举行，欢迎报名参加。", "检测到健康指标异常，请尽快联系家属或社区医生。"}));
      msg.setBizId("MSG-" + (1000 + i));
      msg.setReadFlag(i % 3 == 0 ? 1 : 0);
      msgMapper.insert(msg);
    }
  }

  private void seedPortalMessages(UserEntity elder) {
    if (msgMapper.selectCount(new LambdaQueryWrapper<MsgEntity>()
        .eq(MsgEntity::getReceiverId, elder.getId())
        .like(MsgEntity::getTitle, "老人端消息中心演示")) >= 20) {
      return;
    }
    UserEntity admin = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
        .eq(UserEntity::getUsername, "admin"));
    for (int i = 0; i < 20; i++) {
      MsgEntity msg = new MsgEntity();
      msg.setSenderId(admin == null ? null : admin.getId());
      msg.setReceiverId(elder.getId());
      msg.setMsgType(pick(new String[]{"SYSTEM", "NOTICE", "ALERT"}));
      msg.setTitle("老人端消息中心演示第" + (i + 1) + "条：" + MESSAGE_TITLES[i % MESSAGE_TITLES.length]);
      msg.setContent(MESSAGE_CONTENTS[i % MESSAGE_CONTENTS.length] + " 当前操作状态：" + pick(new String[]{"详情已查看", "消息已编辑备注", "消息已删除归档", "已标记未读"}) + "。");
      msg.setBizId("PORTAL-MSG-" + faker.number().digits(6));
      msg.setReadFlag(i % 3 == 0 ? 1 : 0);
      msg.setReadTime(msg.getReadFlag() == 1 ? LocalDateTime.now().minusHours(randomBetween(1, 48)) : null);
      msgMapper.insert(msg);
    }
  }

  private void seedStaffPortalMessages(UserEntity staff) {
    if (msgMapper.selectCount(new LambdaQueryWrapper<MsgEntity>()
        .eq(MsgEntity::getReceiverId, staff.getId())
        .like(MsgEntity::getTitle, "服务人员消息提醒演示")) >= 20) {
      return;
    }
    UserEntity admin = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
        .eq(UserEntity::getUsername, "admin"));
    for (int i = 0; i < 20; i++) {
      MsgEntity msg = new MsgEntity();
      msg.setSenderId(admin == null ? null : admin.getId());
      msg.setReceiverId(staff.getId());
      msg.setMsgType(pick(new String[]{"SYSTEM", "NOTICE", "ALERT"}));
      msg.setTitle("服务人员消息提醒演示第" + (i + 1) + "条：" + STAFF_MESSAGE_TITLES[i % STAFF_MESSAGE_TITLES.length]);
      msg.setContent(STAFF_MESSAGE_CONTENTS[i % STAFF_MESSAGE_CONTENTS.length]
          + " 当前操作状态：" + pick(new String[]{"详情已查看", "消息已编辑备注", "消息已删除归档", "提醒已确认处理"}) + "。");
      msg.setBizId("STAFF-MSG-" + faker.number().digits(6));
      msg.setReadFlag(i % 4 == 0 ? 1 : 0);
      msg.setReadTime(msg.getReadFlag() == 1 ? LocalDateTime.now().minusHours(randomBetween(1, 36)) : null);
      msgMapper.insert(msg);
    }
  }

  private void seedFamilyPortalMessages(UserEntity family) {
    if (msgMapper.selectCount(new LambdaQueryWrapper<MsgEntity>()
        .eq(MsgEntity::getReceiverId, family.getId())
        .like(MsgEntity::getTitle, "家属端提醒中心演示")) >= 20) {
      return;
    }
    UserEntity admin = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
        .eq(UserEntity::getUsername, "admin"));
    for (int i = 0; i < 20; i++) {
      MsgEntity msg = new MsgEntity();
      msg.setSenderId(admin == null ? null : admin.getId());
      msg.setReceiverId(family.getId());
      msg.setMsgType(pick(new String[]{"SYSTEM", "NOTICE", "ALERT"}));
      msg.setTitle("家属端提醒中心演示第" + (i + 1) + "条：" + FAMILY_MESSAGE_TITLES[i % FAMILY_MESSAGE_TITLES.length]);
      msg.setContent(FAMILY_MESSAGE_CONTENTS[i % FAMILY_MESSAGE_CONTENTS.length]
          + " 当前操作状态：" + pick(new String[]{"详情已查看", "提醒已编辑备注", "消息可删除", "协同结果已确认"}) + "。");
      msg.setBizId("FAMILY-MSG-" + faker.number().digits(6));
      msg.setReadFlag(i % 4 == 0 ? 1 : 0);
      msg.setReadTime(msg.getReadFlag() == 1 ? LocalDateTime.now().minusHours(randomBetween(1, 48)) : null);
      msgMapper.insert(msg);
    }
  }

  private void seedPortalLocations(UserEntity elder) {
    if (locationRecordMapper.selectCount(new LambdaQueryWrapper<LocationRecordEntity>()
        .eq(LocationRecordEntity::getUserId, elder.getId())
        .like(LocationRecordEntity::getAddress, "老人端位置安全演示")) >= 20) {
      return;
    }
    SafeZoneEntity zone = safeZoneMapper.selectOne(new LambdaQueryWrapper<SafeZoneEntity>()
        .eq(SafeZoneEntity::getUserId, elder.getId()));
    if (zone == null) {
      zone = new SafeZoneEntity();
      zone.setUserId(elder.getId());
      zone.setCenterLatitude(decimal(31.231230));
      zone.setCenterLongitude(decimal(121.476880));
      zone.setRadiusMeters(800);
      zone.setAddress("老人端位置安全演示中心点");
      safeZoneMapper.insert(zone);
    } else {
      zone.setCenterLatitude(decimal(31.231230));
      zone.setCenterLongitude(decimal(121.476880));
      zone.setRadiusMeters(800);
      zone.setAddress("老人端位置安全演示中心点");
      safeZoneMapper.updateById(zone);
    }

    for (int i = 0; i < 20; i++) {
      LocationRecordEntity record = new LocationRecordEntity();
      record.setUserId(elder.getId());
      record.setLatitude(decimal(31.231230 + (i % 5) * 0.0012));
      record.setLongitude(decimal(121.476880 + (i % 4) * 0.0011));
      record.setAddress("老人端位置安全演示第" + (i + 1) + "条：" + pick(COMMUNITIES) + pick(ROADS) + randomBetween(1, 60) + "号");
      record.setWithinZone(i % 6 == 0 ? 0 : 1);
      locationRecordMapper.insert(record);

      if (record.getWithinZone() == 0) {
        HealthAlertEntity alert = new HealthAlertEntity();
        alert.setUserId(elder.getId());
        alert.setAlertType(CareConstants.ALERT_LOCATION);
        alert.setAlertLevel(pick(new String[]{"LOW", "MEDIUM", "HIGH"}));
        alert.setTitle("老人端位置与安全演示预警第" + (i + 1) + "条");
        alert.setContent("检测到位置越界，操作状态：详情已查看；模块状态：" + MODULE_STATUS_SUFFIX[8] + "。");
        alert.setStatus(i % 2 == 0 ? "OPEN" : "RESOLVED");
        healthAlertMapper.insert(alert);
      }
    }
  }

  private void seedPortalBindings(UserEntity elder, List<UserEntity> families) {
    if (familyBindingMapper.selectCount(new LambdaQueryWrapper<FamilyBindingEntity>()
        .eq(FamilyBindingEntity::getElderUserId, elder.getId())
        .like(FamilyBindingEntity::getRemark, "老人端家属授权演示")) >= 20) {
      return;
    }
    for (int i = 0; i < 20; i++) {
      UserEntity family = families.get(i % families.size());
      FamilyBindingEntity entity = familyBindingMapper.selectOne(new LambdaQueryWrapper<FamilyBindingEntity>()
          .eq(FamilyBindingEntity::getElderUserId, elder.getId())
          .eq(FamilyBindingEntity::getFamilyUserId, family.getId())
          .like(FamilyBindingEntity::getRemark, "老人端家属授权演示"));
      if (entity != null) {
        continue;
      }
      entity = new FamilyBindingEntity();
      entity.setElderUserId(elder.getId());
      entity.setFamilyUserId(family.getId());
      entity.setRelationLabel(pick(new String[]{"女儿", "儿子", "孙女", "孙子", "女婿", "外孙女"}));
      entity.setStatus(pick(new String[]{CareConstants.BINDING_ACTIVE, CareConstants.BINDING_PENDING, CareConstants.BINDING_REJECTED}));
      entity.setHealthAccess(i % 4 == 0 ? 0 : 1);
      entity.setLocationAccess(i % 5 == 0 ? 0 : 1);
      entity.setRemark("老人端家属授权演示第" + (i + 1) + "条："
          + pick(new String[]{"已查看详情", "授权信息已编辑", "解绑申请待处理", "关系信息可删除"}) + "。");
      familyBindingMapper.insert(entity);
    }
  }

  private void seedFamilyPortalBindings(List<UserEntity> elders, UserEntity family) {
    if (familyBindingMapper.selectCount(new LambdaQueryWrapper<FamilyBindingEntity>()
        .eq(FamilyBindingEntity::getFamilyUserId, family.getId())
        .like(FamilyBindingEntity::getRemark, "家属端绑定管理演示")) >= 20) {
      return;
    }
    for (int i = 0; i < 20; i++) {
      UserEntity elder = elders.get(i % elders.size());
      ElderProfileEntity profile = elderProfileMapper.selectOne(new LambdaQueryWrapper<ElderProfileEntity>()
          .eq(ElderProfileEntity::getUserId, elder.getId()));
      if (profile != null) {
        profile.setFamilyHealthAuthorized(1);
        profile.setFamilyLocationAuthorized(1);
        elderProfileMapper.updateById(profile);
      }
      FamilyBindingEntity existing = familyBindingMapper.selectOne(new LambdaQueryWrapper<FamilyBindingEntity>()
          .eq(FamilyBindingEntity::getElderUserId, elder.getId())
          .eq(FamilyBindingEntity::getFamilyUserId, family.getId())
          .like(FamilyBindingEntity::getRemark, "家属端绑定管理演示"));
      if (existing != null) {
        continue;
      }
      FamilyBindingEntity entity = new FamilyBindingEntity();
      entity.setElderUserId(elder.getId());
      entity.setFamilyUserId(family.getId());
      entity.setRelationLabel(pick(new String[]{"女儿", "儿子", "孙女", "孙子", "女婿", "外孙"}));
      entity.setStatus(pick(new String[]{CareConstants.BINDING_ACTIVE, CareConstants.BINDING_ACTIVE, CareConstants.BINDING_PENDING, CareConstants.BINDING_REJECTED}));
      entity.setHealthAccess(i % 5 == 0 ? 0 : 1);
      entity.setLocationAccess(i % 6 == 0 ? 0 : 1);
      entity.setRemark("家属端绑定管理演示第" + (i + 1) + "条："
          + FAMILY_BINDING_REMARKS[i % FAMILY_BINDING_REMARKS.length]);
      familyBindingMapper.insert(entity);
    }
  }

  private void seedPortalOpLogs(UserEntity elder) {
    if (opLogMapper.selectCount(new LambdaQueryWrapper<OpLogEntity>()
        .eq(OpLogEntity::getUsername, elder.getUsername())
        .like(OpLogEntity::getAction, "账号中心-")) >= 20) {
      return;
    }
    String[] operations = {"查看详情", "编辑资料", "删除记录"};
    for (int i = 0; i < 20; i++) {
      String module = PORTAL_MODULES[i % PORTAL_MODULES.length];
      String operation = operations[i % operations.length];
      OpLogEntity log = new OpLogEntity();
      log.setUserId(elder.getId());
      log.setUsername(elder.getUsername());
      log.setTraceId("PORTALTRACE" + faker.number().digits(8));
      log.setAction(module + "-" + operation + "-" + MODULE_STATUS_SUFFIX[i % MODULE_STATUS_SUFFIX.length]);
      log.setMethod(resolvePortalLogMethod(operation));
      log.setPath(resolvePortalPath(module, operation));
      log.setReqJson("{\"module\":\"" + module + "\",\"operation\":\"" + operation + "\",\"nickname\":\"" + elder.getNickname() + "\"}");
      log.setRespJson("{\"message\":\"老人端模块中文演示日志\",\"moduleStatus\":\"" + MODULE_STATUS_SUFFIX[i % MODULE_STATUS_SUFFIX.length] + "\"}");
      log.setSuccess(i % 7 == 0 ? 0 : 1);
      log.setErrorMsg(log.getSuccess() == 1 ? null : module + "执行" + operation + "时触发演示业务校验");
      log.setCostMs((long) randomBetween(18, 360));
      opLogMapper.insert(log);
    }
  }

  private void seedStaffPortalOpLogs(UserEntity staff) {
    if (opLogMapper.selectCount(new LambdaQueryWrapper<OpLogEntity>()
        .eq(OpLogEntity::getUsername, staff.getUsername())
        .like(OpLogEntity::getAction, "账号与资料-")) >= 20) {
      return;
    }
    String[] operations = {"查看详情", "编辑资料", "删除记录"};
    for (int i = 0; i < 20; i++) {
      String module = STAFF_PORTAL_MODULES[i % STAFF_PORTAL_MODULES.length];
      String operation = operations[i % operations.length];
      OpLogEntity log = new OpLogEntity();
      log.setUserId(staff.getId());
      log.setUsername(staff.getUsername());
      log.setTraceId("STAFFTRACE" + faker.number().digits(8));
      log.setAction(module + "-" + operation + "-" + STAFF_MODULE_STATUS_SUFFIX[i % STAFF_MODULE_STATUS_SUFFIX.length]);
      log.setMethod(resolvePortalLogMethod(operation));
      log.setPath(resolveStaffPortalPath(module, operation));
      log.setReqJson("{\"module\":\"" + module + "\",\"operation\":\"" + operation + "\",\"nickname\":\"" + staff.getNickname() + "\"}");
      log.setRespJson("{\"message\":\"服务人员端模块中文演示日志\",\"moduleStatus\":\"" + STAFF_MODULE_STATUS_SUFFIX[i % STAFF_MODULE_STATUS_SUFFIX.length] + "\"}");
      log.setSuccess(i % 8 == 0 ? 0 : 1);
      log.setErrorMsg(log.getSuccess() == 1 ? null : module + "执行" + operation + "时触发演示校验");
      log.setCostMs((long) randomBetween(16, 320));
      opLogMapper.insert(log);
    }
  }

  private void seedFamilyPortalOpLogs(UserEntity family) {
    if (opLogMapper.selectCount(new LambdaQueryWrapper<OpLogEntity>()
        .eq(OpLogEntity::getUsername, family.getUsername())
        .like(OpLogEntity::getAction, "绑定管理-")) >= 20) {
      return;
    }
    String[] operations = {"查看详情", "编辑资料", "删除记录"};
    for (int i = 0; i < 20; i++) {
      String module = FAMILY_PORTAL_MODULES[i % FAMILY_PORTAL_MODULES.length];
      String operation = operations[i % operations.length];
      OpLogEntity log = new OpLogEntity();
      log.setUserId(family.getId());
      log.setUsername(family.getUsername());
      log.setTraceId("FAMILYTRACE" + faker.number().digits(8));
      log.setAction(module + "-" + operation + "-" + FAMILY_MODULE_STATUS_SUFFIX[i % FAMILY_MODULE_STATUS_SUFFIX.length]);
      log.setMethod(resolvePortalLogMethod(operation));
      log.setPath(resolveFamilyPortalPath(module, operation));
      log.setReqJson("{\"module\":\"" + module + "\",\"operation\":\"" + operation + "\",\"nickname\":\"" + family.getNickname() + "\"}");
      log.setRespJson("{\"message\":\"家属端模块中文演示日志\",\"moduleStatus\":\"" + FAMILY_MODULE_STATUS_SUFFIX[i % FAMILY_MODULE_STATUS_SUFFIX.length] + "\"}");
      log.setSuccess(i % 9 == 0 ? 0 : 1);
      log.setErrorMsg(log.getSuccess() == 1 ? null : module + "执行" + operation + "时触发演示校验");
      log.setCostMs((long) randomBetween(18, 340));
      opLogMapper.insert(log);
    }
  }

  private void seedExternalLogs() {
    if (externalCallLogMapper.selectCount(new LambdaQueryWrapper<ExternalCallLogEntity>()
        .like(ExternalCallLogEntity::getBizId, "DEMO-")) >= 20) {
      return;
    }
    String[] providers = {CareConstants.CALL_PAYMENT, CareConstants.CALL_SMS, CareConstants.CALL_MAP};
    String[] actions = {"PAY", "REFUND", "SEND_ALERT", "REVERSE_GEOCODE", "GEOFENCE_CHECK"};
    for (int i = 0; i < 20; i++) {
      ExternalCallLogEntity log = new ExternalCallLogEntity();
      log.setProviderType(providers[i % providers.length]);
      log.setAction(actions[i % actions.length]);
      log.setBizType(pick(new String[]{"SERVICE_ORDER", "MESSAGE", "LOCATION"}));
      log.setBizId("DEMO-" + faker.number().digits(8));
      log.setReqJson("{\"demo\":true,\"index\":" + i + "}");
      log.setRespJson("{\"message\":\"中文演示外部接口返回成功\",\"success\":" + (i % 7 != 0) + "}");
      log.setSuccess(i % 7 == 0 ? 0 : 1);
      externalCallLogMapper.insert(log);
    }
  }

  private void seedOpLogs() {
    if (opLogMapper.selectCount(new LambdaQueryWrapper<OpLogEntity>()
        .like(OpLogEntity::getUsername, "演示管理员")) >= 20) {
      return;
    }
    UserEntity admin = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>()
        .eq(UserEntity::getUsername, "admin"));
    for (int i = 0; i < 20; i++) {
      String module = LOG_MODULES[i % LOG_MODULES.length];
      String action = LOG_ACTIONS[i % LOG_ACTIONS.length];
      String path = LOG_PATHS[i % LOG_PATHS.length];
      OpLogEntity log = new OpLogEntity();
      log.setUserId(admin == null ? null : admin.getId());
      log.setUsername("演示管理员");
      log.setTraceId("TRACE" + faker.number().digits(10));
      log.setAction(module + "-" + action);
      log.setMethod(resolveLogMethod(action));
      log.setPath("/api/admin/care/" + path + "/" + resolveLogActionPath(action));
      log.setReqJson("{\"module\":\"" + module + "\",\"action\":\"" + action + "\",\"operator\":\"演示管理员\"}");
      log.setRespJson("{\"message\":\"中文演示操作日志\",\"status\":\"" + (i % 9 == 0 ? "失败" : "成功") + "\"}");
      log.setSuccess(i % 9 == 0 ? 0 : 1);
      log.setErrorMsg(log.getSuccess() == 1 ? null : "演示删除操作被业务校验拦截");
      log.setCostMs((long) randomBetween(12, 420));
      opLogMapper.insert(log);
    }
  }

  private String resolvePortalLogMethod(String operation) {
    if (operation.contains("详情")) {
      return "GET";
    }
    if (operation.contains("编辑")) {
      return "PUT";
    }
    return "DELETE";
  }

  private String resolvePortalPath(String module, String operation) {
    String path = switch (module) {
      case "账号中心" -> "/api/me/profile";
      case "信息浏览" -> "/api/public/contents";
      case "社区互动" -> "/api/admin/care/community-posts";
      case "服务大厅" -> "/api/public/service-projects";
      case "订单中心", "个人服务评价" -> "/api/me/service-orders";
      case "健康管理" -> "/api/me/health-records";
      case "家属授权" -> "/api/me/family-bindings";
      case "消息中心" -> "/api/me/messages";
      case "位置与安全" -> "/api/me/locations/latest";
      default -> "/api/me/dashboard";
    };
    if (operation.contains("编辑")) {
      return path + "/edit";
    }
    if (operation.contains("删除")) {
      return path + "/delete";
    }
    return path + "/detail";
  }

  private String resolveStaffPortalPath(String module, String operation) {
    String path = switch (module) {
      case "账号与资料" -> "/api/me/staff-profile";
      case "接单中心", "日程管理", "服务执行", "服务记录" -> "/api/me/service-orders";
      case "消息提醒" -> "/api/me/messages";
      case "个人中心" -> "/api/me/profile";
      default -> "/api/me/dashboard";
    };
    if (operation.contains("编辑")) {
      return path + "/edit";
    }
    if (operation.contains("删除")) {
      return path + "/delete";
    }
    return path + "/detail";
  }

  private String resolveFamilyPortalPath(String module, String operation) {
    String path = switch (module) {
      case "绑定管理", "老人信息查看", "互动协同" -> "/api/me/family-bindings";
      case "健康监测" -> "/api/me/health-records";
      case "服务同步" -> "/api/me/service-orders";
      case "位置查看" -> "/api/me/locations/latest";
      case "提醒中心" -> "/api/me/messages";
      default -> "/api/me/profile";
    };
    if (operation.contains("编辑")) {
      return path + "/edit";
    }
    if (operation.contains("删除")) {
      return path + "/delete";
    }
    return path + "/detail";
  }

  private int countDemoUsers(String prefix) {
    return Math.toIntExact(userMapper.selectCount(new LambdaQueryWrapper<UserEntity>()
        .like(UserEntity::getUsername, DEMO_PREFIX + prefix + "_")));
  }

  private String buildNickname(String role, int index, boolean elderRole) {
    if (GlobalConstants.ROLE_STAFF.equals(role)) {
      return "演示" + buildChineseName(STAFF_NAMES) + index;
    }
    if (GlobalConstants.ROLE_FAMILY.equals(role)) {
      return "演示家属" + buildChineseName(FAMILY_NAMES) + index;
    }
    if (GlobalConstants.ROLE_ADMIN.equals(role)) {
      return "演示管理员" + index;
    }
    return elderRole ? "演示老人" + buildChineseName(ELDER_NAMES) + index : "演示用户" + index;
  }

  private String buildBio(String role) {
    return switch (role) {
      case GlobalConstants.ROLE_STAFF -> "负责社区老人上门服务、护理陪伴与服务执行。";
      case GlobalConstants.ROLE_FAMILY -> "关注老人健康与服务进度，协助家庭照护。";
      case GlobalConstants.ROLE_ADMIN -> "负责平台审核、调度、内容发布与运营管理。";
      default -> "社区老人服务平台演示账号，用于预约服务、记录健康数据。";
    };
  }

  private String buildChineseName(String[] pool) {
    return pick(SURNAMES) + pick(pool);
  }

  private String chinaPhone() {
    return "13" + faker.number().digits(9);
  }

  private String resolvePayStatus(String status) {
    if (CareConstants.ORDER_PENDING_PAYMENT.equals(status)) {
      return CareConstants.PAY_UNPAID;
    }
    if (CareConstants.ORDER_REFUNDED.equals(status)) {
      return CareConstants.PAY_REFUNDED;
    }
    if (CareConstants.ORDER_CANCELLED.equals(status)) {
      return pick(new String[]{CareConstants.PAY_UNPAID, CareConstants.PAY_PAID});
    }
    return CareConstants.PAY_PAID;
  }

  private String resolveLogMethod(String action) {
    if (action.contains("查看")) {
      return "GET";
    }
    if (action.contains("编辑")) {
      return "PUT";
    }
    if (action.contains("删除")) {
      return "DELETE";
    }
    return "POST";
  }

  private String resolveLogActionPath(String action) {
    if (action.contains("查看")) {
      return "detail";
    }
    if (action.contains("编辑")) {
      return "edit";
    }
    if (action.contains("删除")) {
      return "delete";
    }
    if (action.contains("审核通过")) {
      return "approve";
    }
    if (action.contains("审核驳回")) {
      return "reject";
    }
    if (action.contains("派单")) {
      return "assign";
    }
    if (action.contains("发布")) {
      return "publish";
    }
    return "disable";
  }

  private BigDecimal decimal(double value) {
    return BigDecimal.valueOf(value).setScale(6, RoundingMode.HALF_UP);
  }

  private int randomBetween(int min, int max) {
    return ThreadLocalRandom.current().nextInt(min, max + 1);
  }

  private <T> T pick(T[] values) {
    return values[randomBetween(0, values.length - 1)];
  }
}
