# 智慧养老健康平台

基于 Spring Boot 3 + Vue 3 构建的社区养老服务管理系统，支持老人健康档案、家庭绑定、服务预约、位置安全、社区互动等功能。

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.3.7 + MyBatis-Plus 3.5.7 |
| 认证授权 | Sa-Token 1.44.0 + Redis |
| 数据库 | MySQL 8.0 + Flyway（自动建表迁移） |
| 前端框架 | Vue 3 + TypeScript + Vite 7 |
| UI 组件库 | Element Plus |
| 图表 | ECharts 6 |
| 接口文档 | SpringDoc OpenAPI（Swagger UI） |

---

## 项目结构

```
Elderly_Health/
├── backend/                        # Spring Boot 后端
│   ├── src/main/java/com/example/
│   │   ├── controller/             # REST 接口层
│   │   │   ├── UserController.java         # 用户注册/登录/个人信息
│   │   │   ├── UserAdminController.java    # 管理员用户管理
│   │   │   ├── CarePublicController.java   # 公开服务/内容查询
│   │   │   ├── CareMeController.java       # 老人/家属端服务操作
│   │   │   ├── CareAdminController.java    # 管理员服务管理
│   │   │   ├── MsgController.java          # 消息管理
│   │   │   ├── MsgMeController.java        # 我的消息
│   │   │   ├── FileController.java         # 文件上传/下载
│   │   │   └── StatisticsController.java   # 统计数据
│   │   ├── entity/                 # 数据库实体
│   │   ├── mapper/                 # MyBatis-Plus Mapper
│   │   ├── service/                # 业务逻辑层
│   │   ├── dto/                    # 数据传输对象
│   │   ├── config/                 # 配置类
│   │   │   ├── auth/               # Sa-Token 认证配置
│   │   │   ├── exception/          # 全局异常处理
│   │   │   ├── mybatis/            # MyBatis 自动填充
│   │   │   └── web/                # 请求日志过滤器
│   │   └── common/                 # 公共工具
│   │       ├── result/             # 统一响应体 Result / PageResult
│   │       ├── exception/          # 业务异常 BizException
│   │       └── util/               # 工具类
│   └── src/main/resources/
│       ├── application.yml         # 主配置（端口/数据库/Redis）
│       ├── application-dev.yml     # 开发环境配置
│       └── db/migration/           # Flyway SQL 迁移脚本
│           ├── V1__init.sql        # 用户/消息/文件基础表
│           ├── V2__care_domain.sql # 养老业务表 + 初始数据
│           └── V3__community_post.sql # 社区帖子表
│
├── frontend/                       # Vue 3 前端
│   └── src/
│       ├── api/                    # 接口请求封装
│       │   ├── http.ts             # axios 实例 + 拦截器
│       │   ├── user.ts             # 用户相关接口
│       │   ├── care.ts             # 养老服务接口
│       │   ├── msg.ts              # 消息接口
│       │   ├── statistics.ts       # 统计接口
│       │   └── file.ts             # 文件上传接口
│       ├── views/                  # 页面视图
│       │   ├── login/              # 登录页
│       │   ├── dashboard/          # 数据看板
│       │   ├── admin/              # 管理员页面（用户/服务/内容管理）
│       │   ├── elder/              # 老人端（健康记录/服务预约）
│       │   ├── family/             # 家属端（绑定/位置/健康查看）
│       │   ├── staff/              # 服务人员端（订单处理）
│       │   ├── msg/                # 消息中心
│       │   └── profile/            # 个人资料
│       ├── stores/                 # Pinia 状态管理
│       ├── router/                 # Vue Router 路由配置
│       ├── layout/                 # 整体布局框架
│       └── components/             # 公共组件
│           ├── CommonTable/        # 通用表格
│           ├── CommonDialog/       # 通用弹窗
│           ├── ImageUpload/        # 图片上传
│           └── StatusTag/          # 状态标签
│
├── setup_and_push.ps1              # Windows 一键安装依赖并推送 GitHub
├── setup_and_push.sh               # Linux/Mac 一键安装依赖并推送 GitHub
├── .gitignore
└── README.md
```

---

## 数据库表说明

| 表名 | 说明 |
|------|------|
| `t_user` | 用户基础信息 |
| `t_user_role` | 用户角色（ADMIN / ELDER / FAMILY / STAFF） |
| `t_elder_profile` | 老人档案（紧急联系人、健康备注） |
| `t_staff_profile` | 服务人员档案（资质、审核状态） |
| `t_family_binding` | 家属与老人绑定关系 |
| `t_service_category` | 服务分类（家政/跑腿/医护） |
| `t_service_project` | 服务项目（价格、时长） |
| `t_service_order` | 服务订单（状态流转） |
| `t_health_record` | 健康记录（血压/血糖/体温等） |
| `t_health_alert` | 健康预警 |
| `t_safe_zone` | 安全区域设置 |
| `t_location_record` | 位置记录 |
| `t_community_post` | 社区帖子 |
| `t_content` | 公告/健康资讯内容 |
| `t_msg` | 站内消息 |
| `t_file` | 上传文件记录 |
| `t_op_log` | 操作日志 |
| `t_external_call_log` | 外部调用日志 |

---

## 环境要求

| 依赖 | 版本要求 |
|------|----------|
| JDK | **21+** |
| Maven | 3.8+ |
| Node.js | 18+ |
| MySQL | 8.0+ |
| Redis | 6.0+ |

---

## 快速部署

### 方式一：自动部署脚本（推荐）

**Windows（PowerShell）**

```powershell
# 1. 克隆项目
git clone git@github.com:YwusDayDayup/Elderly_Health.git
cd Elderly_Health

# 2. 创建数据库（只需执行一次）
mysql -uroot -p123456 -e "CREATE DATABASE IF NOT EXISTS \`154laoren\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 3. 一键安装依赖 + 推送
.\setup_and_push.ps1
```

**Linux / Mac（Bash）**

```bash
git clone git@github.com:YwusDayDayup/Elderly_Health.git
cd Elderly_Health

mysql -uroot -p123456 -e "CREATE DATABASE IF NOT EXISTS \`154laoren\` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

bash setup_and_push.sh
```

---

### 方式二：手动部署

#### 第一步：准备数据库

```sql
-- 登录 MySQL 后执行
CREATE DATABASE IF NOT EXISTS `154laoren`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

> 数据库表结构由 **Flyway 自动创建**，启动后端时会自动执行 `db/migration/` 下的迁移脚本，无需手动导入 SQL。

确认 `backend/src/main/resources/application.yml` 中数据库配置与本机一致：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/154laoren?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8
    username: root
    password: 123456
  data:
    redis:
      host: 127.0.0.1
      port: 6379
```

#### 第二步：启动后端

```bash
cd backend

# Windows 使用 JDK 21
set JAVA_HOME=F:\JAVA\Jdk21

# 打包（跳过测试）
mvn clean package -DskipTests

# 启动
java -jar target/example-0.0.1-SNAPSHOT.jar
```

启动成功后控制台输出：
```
应用已启动  local=http://localhost:8080/  external=http://x.x.x.x:8080/
```

接口文档地址：http://localhost:8080/swagger-ui/index.html

#### 第三步：启动前端

```bash
cd frontend

# 安装依赖
npm install

# 开发模式（带热更新，代理到后端 8080）
npm run dev

# 或生产构建后预览
npm run build
npx vite preview --port 3000 --host
```

前端访问地址：http://localhost:5173（dev）或 http://localhost:3000（preview）

---

## 默认账号

系统启动时会自动生成种子数据（`app.seed.enabled: true`），可直接使用以下账号登录：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | `admin` | `123456` |

---

## 接口说明

所有接口统一前缀 `/api`，认证接口需在请求头携带：

```
Authorization: Bearer <token>
```

| 路径前缀 | 说明 |
|----------|------|
| `/api/public/**` | 无需登录（登录、注册、公开内容） |
| `/api/user/**` | 用户个人操作 |
| `/api/care/me/**` | 老人/家属端服务操作 |
| `/api/care/admin/**` | 管理员服务管理 |
| `/api/admin/user/**` | 管理员用户管理 |
| `/api/msg/**` | 消息管理 |
| `/api/statistics/**` | 统计数据 |
| `/uploads/**` | 上传文件访问（无需认证） |

---

## 常见问题

**Q: 后端启动报 `Unknown database '154laoren'`**
> 需要先手动创建数据库，见「第一步：准备数据库」。

**Q: 后端启动报 Redis 连接失败**
> 确保 Redis 服务已启动。Windows 可执行 `redis-server` 或在服务管理器中启动 Redis 服务。

**Q: 前端 build 报 TypeScript 错误**
> 执行 `npm install` 重新安装依赖后再 `npm run build`。

**Q: 端口冲突**
> 后端端口在 `application.yml` 的 `server.port` 修改；前端端口在 `vite.config.ts` 的 `server.port` 修改。
