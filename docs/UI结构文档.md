社区养老系统 — UI 结构文档

> 用途：供 Stitch 进行 UI 重构设计参考。每个区块均预留截图插入位置。
> 技术栈：Vue 3 + Element Plus + SCSS + Pinia + Vue Router

---

## 一、整体布局结构

```
┌─────────────────────────────────────────────────────────┐
│  Sidebar（左侧导航）  │  Header（顶部栏）                │
│  width: 240px        │  height: 64px                    │
│  collapse: 72px      ├──────────────────────────────────┤
│                      │  Main Content Area               │
│  - Logo区            │  padding: 20px 24px              │
│  - 菜单列表          │  background: var(--app-bg)       │
│                      │                                  │
└──────────────────────┴──────────────────────────────────┘
```

**截图插入位置 — 整体布局（桌面端，1440px）：**

![image-20260425151545598](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151545598.png)

```
[ 请在此处插入整体布局截图 ]
```

---

## 二、侧边栏（SidebarMenu）

**文件：** `frontend/src/layout/components/SidebarMenu.vue`

### 结构
```
el-aside
  ├── .logo（Logo区）
  │     ├── .logo-mark（SVG图标）
  │     └── .logo-copy（标题 + 副标题）
  └── el-scrollbar
        └── el-menu（菜单列表）
              └── el-menu-item × N（每个菜单项）
```

### 配色
- 背景：`var(--app-sidebar-bg)`（深色，`#304156` 系）
- 文字：`rgba(255,255,255,0.65)`
- 激活文字：`#FFFFFF`
- 激活背景：`var(--app-sidebar-active-bg)`
- 激活左侧指示条：主色渐变

### 菜单项（按角色分组）

| 角色 | 菜单项 |
|------|--------|
| 公共 | 工作台 |
| ADMIN | 用户管理、服务管理、内容管理、社区互动、订单调度、健康预警、家属绑定、系统监控 |
| ELDER | 服务大厅、我的订单、健康管理、资讯活动、家属授权、位置安全 |
| STAFF | 接单中心、个人中心、服务档案、消息通知、日程管理 |
| FAMILY | 绑定管理、健康监测、服务同步、位置查看 |

**截图插入位置 — 侧边栏展开状态：**

![image-20260425151621356](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151621356.png)

```
[ 请在此处插入侧边栏截图（展开） ]
```

**截图插入位置 — 侧边栏折叠状态（72px）：**

![image-20260425151615806](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151615806.png)

```
[ 请在此处插入侧边栏截图（折叠） ]
```

---

## 三、顶部栏（Header）

**文件：** `frontend/src/layout/index.vue`

### 结构
```
el-header（height: 64px）
  ├── .header-left
  │     ├── 折叠/展开按钮（toggle-icon）
  │     └── Breadcrumb（面包屑）
  └── .header-right
        ├── TopbarNotice（消息通知铃铛）
        └── UserDropdown（用户头像 + 下拉菜单）
```

### UserDropdown 下拉项
- 个人中心
- 修改密码
- 退出登录

**截图插入位置 — 顶部栏：**

![image-20260425151646516](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151646516.png)

```
[ 请在此处插入顶部栏截图 ]
```

---

## 四、登录 / 注册页

**路由：** `/login`、`/register`
**文件：** `frontend/src/views/login/index.vue`

### 结构（推断）
```
.login-page（全屏居中）
  └── .login-card
        ├── Logo / 系统名称
        ├── el-tabs（登录 / 注册切换）
        │     ├── 登录 Tab
        │     │     ├── el-input（用户名）
        │     │     ├── el-input（密码，type=password）
        │     │     └── el-button（登录）
        │     └── 注册 Tab
        │           ├── el-input（用户名）
        │           ├── el-input（昵称）
        │           ├── el-input（密码）
        │           ├── el-select（角色）
        │           └── el-button（注册）
        └── 底部版权信息
```

**截图插入位置 — 登录页：**

![image-20260425151442645](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151442645.png)

![image-20260425151503830](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151503830.png)

```

```

---

## 五、工作台（Dashboard）

**路由：** `/dashboard`
**文件：** `frontend/src/views/dashboard/index.vue`（根据角色分发）

| 角色 | 组件 |
|------|------|
| ADMIN | AdminDashboard.vue → DashboardBoard.vue |
| ELDER | ElderDashboard.vue |
| STAFF | StaffDashboard.vue |
| FAMILY | FamilyDashboard.vue |

### DashboardBoard 结构（管理员）
```
.app-container
  ├── .page-head（页面标题区）
  │     ├── .page-kicker（标签徽章）
  │     ├── h2（主标题）
  │     └── span（副标题）
  ├── 指标卡片区（MetricVO × N）
  │     └── el-col × N
  │           └── el-card（数字 + 趋势）
  ├── 最新订单列表（el-table）
  ├── 最新健康预警列表（el-table）
  └── 最新内容列表（el-table）
```

**截图插入位置 — 管理员工作台：**

![image-20260425151703912](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151703912.png)

![image-20260425151718168](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151718168.png)

```
[ 请在此处插入管理员工作台截图 ]
```

**截图插入位置 — 老人工作台：**

![image-20260425152357327](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152357327.png)

![](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152410746.png)



**截图插入位置 — 社区服务人员工作台：**

![image-20260425152847260](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152847260.png)

![image-20260425152853422](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152853422.png)

**截图插入位置 — 老人家属工作台：**

![image-20260425153149171](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425153149171.png)

![image-20260425153159500](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425153159500.png)

---

## 六、管理员模块页面

### 6.1 用户管理（/admin/user）

**文件：** `frontend/src/views/admin/user.vue`

```
.app-container
  ├── .page-head
  └── .admin-zone
        ├── .admin-zone__header（标题 + 新增按钮）
        ├── .admin-zone__toolbar（搜索表单）
        │     ├── el-input（关键字）
        │     ├── el-select（状态）
        │     ├── el-select（角色）
        │     └── el-button（搜索/重置）
        └── .admin-zone__body--flush
              ├── el-table
              │     ├── ID列
              │     ├── 头像列（el-avatar）
              │     ├── 用户名列
              │     ├── 昵称列
              │     ├── 邮箱列
              │     ├── 手机号列
              │     ├── 性别列
              │     ├── 地区列
              │     ├── 状态列（el-tag）
              │     └── 操作列（详情/编辑/删除）
              └── el-pagination

弹窗：
  ├── 编辑用户 Dialog（el-form，label-width: 80px）
  ├── 账号详情 Dialog（el-descriptions，2列）
  └── 新增用户 Dialog（el-form + 表单验证）
```

**截图插入位置 — 用户管理列表：**

![image-20260425151734085](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151734085.png)

```
[ 请在此处插入用户管理页截图 ]
```

**截图插入位置 — 新增/编辑用户弹窗：**

![image-20260425151751653](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151751653.png)

```
[ 请在此处插入用户编辑弹窗截图 ]
```

---

### 6.2 服务管理（/admin/service）

**文件：** `frontend/src/views/admin/service.vue`

```
.app-container
  ├── .page-head
  ├── .admin-zone（服务分类）
  │     ├── .admin-zone__header（+ 新增分类按钮）
  │     └── .admin-zone__body
  │           └── el-table（分类列表：名称/编码/状态/操作）
  └── .admin-zone（服务项目）
        ├── .admin-zone__header（+ 新增项目按钮）
        ├── .admin-zone__toolbar（分类筛选）
        └── .admin-zone__body
              └── el-table（项目列表：名称/分类/价格/状态/操作）

弹窗：
  ├── 新增/编辑分类 Dialog
  └── 新增/编辑项目 Dialog
```

**截图插入位置 — 服务管理页：**

![image-20260425151818375](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151818375.png)

![image-20260425151830094](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151830094.png)

```
[ 请在此处插入服务管理页截图 ]
```

---

### 6.3 内容管理（/admin/content）

**文件：** `frontend/src/views/admin/content.vue`

```
.app-container
  ├── .page-head
  └── .admin-zone
        ├── .admin-zone__header（+ 新增内容按钮）
        ├── .admin-zone__toolbar（类型/状态筛选）
        └── .admin-zone__body
              └── el-table（标题/类型/状态/发布时间/操作）

弹窗：新增/编辑内容 Dialog（含富文本/textarea）
```

**截图插入位置 — 内容管理页：**

![image-20260425151840681](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151840681.png)

```
[ 请在此处插入内容管理页截图 ]
```

---

### 6.4 社区互动（/admin/community）

**文件：** `frontend/src/views/admin/community.vue`

```
.app-container
  ├── .page-head
  └── .admin-zone
        ├── .admin-zone__toolbar（状态/审核状态筛选）
        └── .admin-zone__body
              └── el-table（标题/作者/状态/审核状态/操作）
```

**截图插入位置 — 社区互动页：**

![image-20260425151853035](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151853035.png)

```
[ 请在此处插入社区互动页截图 ]
```

---

### 6.5 订单调度（/admin/orders）

**文件：** `frontend/src/views/admin/orders.vue`

```
.app-container
  ├── .page-head
  └── .admin-zone
        ├── .admin-zone__toolbar（关键字/状态筛选）
        └── .admin-zone__body
              └── el-table
                    ├── 订单号列
                    ├── 服务名称列
                    ├── 老人姓名列
                    ├── 服务人员列
                    ├── 预约时间列
                    ├── 订单状态列（el-tag）
                    ├── 支付状态列（el-tag）
                    └── 操作列（详情/派单）

弹窗：
  ├── 分配服务人员 Dialog（el-select 选择员工）
  └── 订单详情 Dialog（el-descriptions，2列）
```

**截图插入位置 — 订单调度页：**

![image-20260425151906892](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151906892.png)

```
[ 请在此处插入订单调度页截图 ]
```

---

### 6.6 健康预警（/admin/health）

**文件：** `frontend/src/views/admin/health.vue`

```
.app-container
  ├── .page-head
  └── .admin-zone
        ├── .admin-zone__toolbar（预警级别/状态筛选）
        └── .admin-zone__body
              └── el-table（用户/预警类型/级别/标题/状态/时间）
```

**截图插入位置 — 健康预警页：**

![image-20260425151915982](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151915982.png)

```
[ 请在此处插入健康预警页截图 ]
```

---

### 6.7 家属绑定（/admin/bindings）

**文件：** `frontend/src/views/admin/bindings.vue`

```
.app-container
  ├── .page-head
  └── .admin-zone
        └── el-table（老人/家属/关系/状态/健康授权/位置授权）
```

**截图插入位置 — 家属绑定页：**

![image-20260425151923649](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151923649.png)

```
[ 请在此处插入家属绑定页截图 ]
```

---

### 6.8 消息管理（/admin/msg）

**文件：** `frontend/src/views/admin/msg.vue`

```
.app-container
  ├── .page-head
  ├── .admin-zone（发送消息区）
  │     └── el-form（接收人/类型/标题/内容 + 发送按钮）
  └── .admin-zone（消息列表区）
        └── el-table（标题/类型/接收人/已读/时间/操作）
```

**截图插入位置 — 消息管理页：**

![image-20260425151946488](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425151946488.png)

```
[ 请在此处插入消息管理页截图 ]
```

---

### 6.9 系统监控（/admin/system）

**文件：** `frontend/src/views/admin/system.vue`

```
.app-container
  ├── .page-head
  ├── 系统指标卡片区（CPU/内存/运行时间/数据库连接）
  ├── .admin-zone（操作日志）
  │     ├── .admin-zone__toolbar（用户/操作/时间范围筛选）
  │     └── el-table（用户/操作/路径/耗时/结果/时间）
  └── .admin-zone（外部调用日志）
        └── el-table（类型/动作/结果/时间）
```

**截图插入位置 — 系统监控页：**

![image-20260425152006441](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152006441.png)

![image-20260425152015685](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152015685.png)

```
[ 请在此处插入系统监控页截图 ]
```

---

## 七、老人（ELDER）模块页面

### 7.1 服务大厅（/elder/services）

```
.app-container
  ├── .page-head
  ├── 服务分类 Tab（el-tabs）
  └── 服务项目卡片列表（el-row + el-col）
        └── el-card × N
              ├── 封面图（el-image）
              ├── 服务名称
              ├── 价格
              └── 预约按钮

弹窗：预约服务 Dialog（时间/地址/联系人/备注）
```

**截图插入位置 — 服务大厅：**

![image-20260425152223965](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152223965.png)

```
[ 请在此处插入服务大厅截图 ]
```

---

### 7.2 我的订单（/elder/orders）

```
.app-container
  ├── .page-head
  └── .admin-zone
        ├── .admin-zone__toolbar（状态筛选）
        └── el-table（订单号/服务/预约时间/状态/操作）
              操作：支付/取消/评价
```

**截图插入位置 — 我的订单（老人）：**

![image-20260425152240153](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152240153.png)

```
[ 请在此处插入老人订单页截图 ]
```

---

### 7.3 健康管理（/elder/health）

```
.app-container
  ├── .page-head
  ├── 健康记录录入区（el-form）
  │     └── 体重/血压/血糖/血脂/脉搏/体温/备注
  └── .admin-zone（历史记录）
        └── el-table（日期/各指标/异常标记）
```

**截图插入位置 — 健康管理：**

![image-20260425152257440](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152257440.png)

```
[ 请在此处插入健康管理页截图 ]
```

---

### 7.4 资讯活动（/elder/content）

```
.app-container
  ├── .page-head
  └── 内容卡片列表（el-row + el-col）
        └── el-card × N（封面/标题/摘要/发布时间）
```

**截图插入位置 — 资讯活动：**

![image-20260425152309154](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152309154.png)

```
[ 请在此处插入资讯活动页截图 ]
```

---

### 7.5 家属授权（/elder/family）

```
.app-container
  ├── .page-head
  └── .admin-zone
        ├── .admin-zone__header（+ 添加家属按钮）
        └── el-table（家属姓名/关系/健康授权/位置授权/状态/操作）
```

**截图插入位置 — 家属授权：**

![image-20260425152317411](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152317411.png)

```
[ 请在此处插入家属授权页截图 ]
```

---

### 7.6 位置安全（/elder/location）

```
.app-container
  ├── .page-head
  ├── 安全区域设置区（el-form：中心坐标/半径/地址）
  └── 位置记录区（el-table：时间/坐标/地址/是否在区内）
```

**截图插入位置 — 位置安全：**

![image-20260425152323422](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152323422.png)

```
[ 请在此处插入位置安全页截图 ]
```

---

## 八、服务人员（STAFF）模块页面

### 8.1 接单中心（/staff/orders）

```
.app-container
  ├── .page-head
  └── .admin-zone
        ├── .admin-zone__toolbar（状态筛选）
        └── el-table（订单号/老人/服务/预约时间/状态/操作）
              操作：接单/开始/完成
```

**截图插入位置 — 接单中心：**

![image-20260425152919547](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152919547.png)

```
[ 请在此处插入接单中心截图 ]
```

---

### 8.2 服务档案（/staff/profile）

```
.app-container
  ├── .page-head
  └── el-form（真实姓名/证书编号/专长/服务半径/简介）
        └── el-button（保存）
```

**截图插入位置 — 服务档案：**

![image-20260425152929351](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152929351.png)

```
[ 请在此处插入服务档案截图 ]
```

---

### 8.3 日程管理（/staff/schedule）

```
.app-container
  ├── .page-head
  └── 日历/时间轴视图（已接订单按时间展示）
```

**截图插入位置 — 日程管理：**

![image-20260425152941905](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425152941905.png)



---

## 九、家属（FAMILY）模块页面

### 9.1 绑定管理（/family/bindings）

```
.app-container
  ├── .page-head
  └── .admin-zone
        ├── .admin-zone__header（+ 申请绑定按钮）
        └── el-table（老人姓名/关系/状态/健康授权/位置授权/操作）
```

**截图插入位置 — 绑定管理：**

![image-20260425153224644](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425153224644.png)

```
[ 请在此处插入绑定管理截图 ]
```

---

### 9.2 健康监测（/family/health）

```
.app-container
  ├── .page-head
  └── .admin-zone
        ├── 老人选择器（el-select）
        └── el-table（日期/各健康指标/异常标记）
```

**截图插入位置 — 健康监测：**

![image-20260425153247343](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425153247343.png)

```
[ 请在此处插入健康监测截图 ]
```

---

### 9.3 服务同步（/family/orders）

```
.app-container
  ├── .page-head
  └── .admin-zone
        └── el-table（订单号/服务/预约时间/状态）
```

**截图插入位置 — 服务同步：**
![image-20260425153255877](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425153255877.png)

```
[ 请在此处插入服务同步截图 ]
```

---

### 9.4 位置查看（/family/location）

```
.app-container
  ├── .page-head
  └── 最新位置展示（坐标/地址/是否在安全区/时间）
```

**截图插入位置 — 位置查看：**
![image-20260425153307128](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425153307128.png)

```
[ 请在此处插入位置查看截图 ]
```

---

## 十、通用功能页面

### 10.1 个人中心（/profile）

**文件：** `frontend/src/views/profile/index.vue`

```
.app-container
  ├── .page-head
  ├── 基本信息区（el-form）
  │     ├── 头像上传（ImageUpload组件）
  │     ├── 昵称/邮箱/手机/性别/生日/地区/城市
  │     └── 保存按钮
  └── 老人/员工专属档案区（根据角色显示）
```

**截图插入位置 — 个人中心：**

![image-20260425153331049](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425153331049.png)

```
[ 请在此处插入个人中心截图 ]
```

---

### 10.2 我的消息（/msg）

**文件：** `frontend/src/views/msg/index.vue`

```
.app-container
  ├── .page-head
  ├── 操作栏（全部已读按钮 + 未读筛选）
  └── 消息列表
        └── .common-msg-item × N
              ├── .common-msg-unread-dot（未读红点）
              ├── .common-msg-title（标题）
              └── .common-msg-time（时间）

弹窗：消息详情 Dialog
```

**截图插入位置 — 我的消息：**

点击顶部消息按钮：

![image-20260425153444284](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425153444284.png)

点击查看全部消息：![image-20260425153501756](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425153501756.png)

消息通知页面：

![image-20260425153438586](C:\Users\Yiwu\AppData\Roaming\Typora\typora-user-images\image-20260425153438586.png)

```
[ 请在此处插入消息页截图 ]
```

---

## 十一、公共组件清单

| 组件 | 文件 | 用途 |
|------|------|------|
| Breadcrumb | `components/Breadcrumb/index.vue` | 顶部面包屑导航 |
| CommonDialog | `components/CommonDialog/index.vue` | 通用弹窗封装 |
| CommonTable | `components/CommonTable/index.vue` | 通用表格封装 |
| ConfirmButton | `components/ConfirmButton/index.vue` | 带确认的操作按钮 |
| ImageUpload | `components/ImageUpload/index.vue` | 图片上传组件 |
| PageHeader | `components/PageHeader/index.vue` | 页面标题区 |
| StatusTag | `components/StatusTag/index.vue` | 状态标签 |

---

## 十二、设计 Token（CSS 变量）

| 变量名 | 说明 | 当前值（亮色） |
|--------|------|----------------|
| `--app-primary` | 主色 | `#E85A3C`（橙红） |
| `--app-primary-light` | 主色浅 | — |
| `--app-primary-bg` | 主色背景 | 低透明度主色 |
| `--app-sidebar-bg` | 侧边栏背景 | `#304156` 系深色 |
| `--app-sidebar-active-bg` | 侧边栏激活背景 | — |
| `--app-bg` | 页面背景 | 浅灰 |
| `--app-surface` | 卡片/面板背景 | 白色 |
| `--app-surface-muted` | 次级面板背景 | 极浅灰 |
| `--app-border-color` | 边框色 | 浅灰 |
| `--app-text-primary` | 主文字色 | 深灰 |
| `--app-text-secondary` | 次文字色 | 中灰 |
| `--app-text-muted` | 弱文字色 | 浅灰 |
| `--app-card-radius` | 卡片圆角 | — |
| `--app-card-shadow` | 卡片阴影 | — |
| `--app-header-bg` | 顶栏背景 | 白/半透明 |
| `--app-header-shadow` | 顶栏阴影 | — |

**SCSS 变量（`_variables.scss`）：**

| 变量 | 值 |
|------|----|
| `$primary-color` | `#409eff` |
| `$sideBarWidth` | `220px` |
| `$menuBg` | `#304156` |
| `$menuHover` | `#263445` |
| `$subMenuBg` | `#1f2d3d` |
| `$font-size-base` | `14px` |

---

## 十三、重构注意事项（给 Stitch）

1. **侧边栏配色不变**：深色系 `#304156`，主色橙红 `#E85A3C`
2. **`.admin-zone` 是核心布局单元**：header + toolbar + body 三段式，保持结构
3. **`.page-head` 统一页面标题**：kicker 徽章 + h2 + 描述文字
4. **老年用户可读性**：字号建议 ≥ 15px，行高 ≥ 1.6，按钮高度 ≥ 40px
5. **表格操作列**：保持 link 按钮风格，不改为图标按钮（老人不熟悉）
6. **弹窗宽度**：编辑类 500px，详情类 680-760px，保持不变
7. **避免渐变/玻璃拟态**：当前主按钮有渐变，重构时可改为纯色
