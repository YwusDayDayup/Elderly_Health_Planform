update t_user_role
set role = 'ELDER'
where role = 'USER';

create table if not exists t_elder_profile (
  id bigint not null auto_increment,
  user_id bigint not null,
  real_name varchar(64) null,
  id_card varchar(32) null,
  emergency_contact_name varchar(64) null,
  emergency_contact_phone varchar(32) null,
  address varchar(255) null,
  health_notes varchar(255) null,
  family_health_authorized tinyint not null default 1,
  family_location_authorized tinyint not null default 0,
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  unique key uk_elder_profile_user_id (user_id)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_staff_profile (
  id bigint not null auto_increment,
  user_id bigint not null,
  real_name varchar(64) null,
  certificate_no varchar(64) null,
  specialty varchar(128) null,
  service_radius_km int not null default 5,
  intro varchar(255) null,
  audit_status varchar(32) not null default 'PENDING',
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  unique key uk_staff_profile_user_id (user_id),
  key idx_staff_profile_audit_status (audit_status)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_family_binding (
  id bigint not null auto_increment,
  elder_user_id bigint not null,
  family_user_id bigint not null,
  relation_label varchar(64) not null,
  status varchar(32) not null default 'PENDING',
  health_access tinyint not null default 1,
  location_access tinyint not null default 0,
  remark varchar(255) null,
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  key idx_family_binding_elder_user_id (elder_user_id),
  key idx_family_binding_family_user_id (family_user_id),
  key idx_family_binding_status (status)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_service_category (
  id bigint not null auto_increment,
  name varchar(64) not null,
  code varchar(64) not null,
  description varchar(255) null,
  sort_no int not null default 0,
  status tinyint not null default 1,
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  unique key uk_service_category_code (code)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_service_project (
  id bigint not null auto_increment,
  category_id bigint not null,
  name varchar(128) not null,
  description varchar(255) null,
  price decimal(10,2) not null default 0.00,
  unit varchar(32) not null default '次',
  status tinyint not null default 1,
  cover_url varchar(255) null,
  default_staff_user_id bigint null,
  service_duration_minutes int not null default 60,
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  key idx_service_project_category_id (category_id),
  key idx_service_project_status (status)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_service_order (
  id bigint not null auto_increment,
  order_no varchar(64) not null,
  elder_user_id bigint not null,
  family_user_id bigint null,
  staff_user_id bigint null,
  service_project_id bigint not null,
  service_name varchar(128) not null,
  category_name varchar(64) not null,
  service_time datetime(3) not null,
  service_address varchar(255) not null,
  contact_name varchar(64) not null,
  contact_phone varchar(32) not null,
  remark varchar(255) null,
  amount decimal(10,2) not null default 0.00,
  pay_status varchar(32) not null default 'UNPAID',
  order_status varchar(32) not null default 'PENDING_PAYMENT',
  payment_time datetime(3) null,
  accept_time datetime(3) null,
  start_time datetime(3) null,
  finish_time datetime(3) null,
  rating int null,
  rating_content varchar(255) null,
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  unique key uk_service_order_order_no (order_no),
  key idx_service_order_elder_user_id (elder_user_id),
  key idx_service_order_staff_user_id (staff_user_id),
  key idx_service_order_status (order_status)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_content (
  id bigint not null auto_increment,
  content_type varchar(32) not null,
  title varchar(128) not null,
  summary varchar(255) null,
  content text not null,
  cover_url varchar(255) null,
  status tinyint not null default 1,
  author_id bigint null,
  author_name varchar(64) null,
  published_at datetime(3) null,
  view_count bigint not null default 0,
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  key idx_content_type (content_type),
  key idx_content_status (status)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_health_record (
  id bigint not null auto_increment,
  user_id bigint not null,
  record_date date not null,
  weight decimal(6,2) null,
  systolic_bp int null,
  diastolic_bp int null,
  blood_sugar decimal(6,2) null,
  blood_lipid decimal(6,2) null,
  pulse int null,
  body_temp decimal(4,1) null,
  check_in_flag tinyint not null default 1,
  note varchar(255) null,
  abnormal_flag tinyint not null default 0,
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  key idx_health_record_user_id (user_id),
  key idx_health_record_record_date (record_date)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_health_alert (
  id bigint not null auto_increment,
  user_id bigint not null,
  record_id bigint null,
  alert_type varchar(32) not null,
  alert_level varchar(32) not null,
  title varchar(128) not null,
  content varchar(255) not null,
  status varchar(32) not null default 'OPEN',
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  key idx_health_alert_user_id (user_id),
  key idx_health_alert_status (status)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_safe_zone (
  id bigint not null auto_increment,
  user_id bigint not null,
  center_latitude decimal(10,6) not null,
  center_longitude decimal(10,6) not null,
  radius_meters int not null default 500,
  address varchar(255) null,
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  unique key uk_safe_zone_user_id (user_id)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_location_record (
  id bigint not null auto_increment,
  user_id bigint not null,
  latitude decimal(10,6) not null,
  longitude decimal(10,6) not null,
  address varchar(255) null,
  within_zone tinyint not null default 1,
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  key idx_location_record_user_id (user_id),
  key idx_location_record_create_time (create_time)
) engine=InnoDB default charset=utf8mb4;

create table if not exists t_external_call_log (
  id bigint not null auto_increment,
  provider_type varchar(32) not null,
  action varchar(64) not null,
  biz_type varchar(64) null,
  biz_id varchar(64) null,
  req_json text null,
  resp_json text null,
  success tinyint not null default 1,
  create_time datetime(3) not null default current_timestamp(3),
  update_time datetime(3) not null default current_timestamp(3) on update current_timestamp(3),
  primary key (id),
  key idx_external_call_log_provider_type (provider_type),
  key idx_external_call_log_biz_type (biz_type)
) engine=InnoDB default charset=utf8mb4;

insert into t_service_category (name, code, description, sort_no, status)
select '家政服务', 'HOUSEKEEPING', '保洁、做饭、陪护等日常上门服务', 10, 1
where not exists (select 1 from t_service_category where code = 'HOUSEKEEPING');

insert into t_service_category (name, code, description, sort_no, status)
select '跑腿代办', 'ERRAND', '购物、取件、缴费等代办服务', 20, 1
where not exists (select 1 from t_service_category where code = 'ERRAND');

insert into t_service_category (name, code, description, sort_no, status)
select '基础医护', 'CARE', '血压测量、陪诊、健康咨询等服务', 30, 1
where not exists (select 1 from t_service_category where code = 'CARE');

insert into t_service_project (category_id, name, description, price, unit, status, service_duration_minutes)
select c.id, '上门保洁', '支持客厅卧室基础清洁', 68.00, '次', 1, 120
from t_service_category c
where c.code = 'HOUSEKEEPING'
  and not exists (select 1 from t_service_project where name = '上门保洁');

insert into t_service_project (category_id, name, description, price, unit, status, service_duration_minutes)
select c.id, '代买代送', '社区周边生活物资代买代送', 20.00, '次', 1, 60
from t_service_category c
where c.code = 'ERRAND'
  and not exists (select 1 from t_service_project where name = '代买代送');

insert into t_service_project (category_id, name, description, price, unit, status, service_duration_minutes)
select c.id, '血压测量', '上门血压检测与健康建议', 39.00, '次', 1, 45
from t_service_category c
where c.code = 'CARE'
  and not exists (select 1 from t_service_project where name = '血压测量');

insert into t_content (content_type, title, summary, content, status, author_name, published_at)
select 'NOTICE', '社区春季义诊活动通知', '本周六上午在社区服务中心开展义诊活动。', '社区服务中心将联合卫生服务站开展义诊，欢迎老年居民预约参与。', 1, '系统初始化', now(3)
where not exists (select 1 from t_content where title = '社区春季义诊活动通知');

insert into t_content (content_type, title, summary, content, status, author_name, published_at)
select 'HEALTH', '高血压老人日常饮食建议', '控制盐分摄入，保持规律作息。', '建议减少高盐高脂饮食，保持每日适量运动并定期记录血压变化。', 1, '系统初始化', now(3)
where not exists (select 1 from t_content where title = '高血压老人日常饮食建议');
