# 慧养家园系统 Bug 修复文档

本文档记录了系统开发和测试过程中发现并修复的所有 Bug，包括问题描述、修复方案和涉及的文件。

---

## 目录

1. [状态显示一致性问题](#1-状态显示一致性问题)
2. [分页功能问题](#2-分页功能问题)
3. [头像上传功能问题](#3-头像上传功能问题)
4. [登录页面问题](#4-登录页面问题)
5. [国际化问题](#5-国际化问题)
6. [项目名称更新](#6-项目名称更新)

---

## 1. 状态显示一致性问题

### 1.1 订单调度页面状态显示不一致

**问题描述：**
- 订单调度页面状态筛选需要使用英文状态码（如 PENDING_ASSIGN）才能搜索
- 用户体验不友好，需要记住英文状态码

**修复方案：**
- 将状态输入框改为下拉选择框
- 用户选择中文标签（如"待上门"），后台自动提交对应的英文状态码（PENDING_SERVICE）

**涉及文件：**
- `frontend/src/views/admin/orders.vue`
- `frontend/src/views/admin/health.vue`

**状态码映射：**
```javascript
const orderStatusMap = {
  PENDING_PAYMENT: '待支付',
  PENDING_ASSIGN: '待派单',
  PENDING_ACCEPT: '待接单',
  PENDING_SERVICE: '待上门',
  IN_SERVICE: '服务中',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  REFUNDED: '已退款'
}
```

---

### 1.2 工作台最新内容和订单状态显示

**问题描述：**
- 工作台的"最新内容"模块直接显示英文内容类型（如 NOTICE、NEWS）
- "最新订单"模块直接显示英文订单状态（如 PENDING_SERVICE）
- "绑定关系"模块直接显示英文绑定状态（如 ACTIVE）

**修复方案：**
添加状态转换函数，将英文状态码转换为中文显示

**涉及文件：**
- `frontend/src/views/dashboard/DashboardBoard.vue`

**修复内容：**
1. 添加 `contentTypeLabel` 函数转换内容类型
2. 使用 `orderStatusLabelMap` 转换订单状态
3. 使用 `bindingStatusLabelMap` 转换绑定状态

---

### 1.3 资讯活动和家属授权页面类型显示

**问题描述：**
- 资讯活动页面的内容类型显示英文（NOTICE、NEWS 等）
- 家属授权页面的绑定状态显示英文（ACTIVE、PENDING、REJECTED）

**修复方案：**
- 为资讯活动页面添加 `contentTypeLabel` 函数
- 为家属授权页面添加 `statusLabel` 和 `statusType` 函数，使用 `el-tag` 组件显示带颜色的中文状态

**涉及文件：**
- `frontend/src/views/elder/content.vue`
- `frontend/src/views/elder/family.vue`

---

### 1.4 消息通知类型和内容状态显示

**问题描述：**
- 消息详情中的消息类型显示英文（FAMILY_BINDING、SERVICE_ORDER）
- 消息内容中包含的订单状态和绑定状态显示英文代码

**修复方案：**
1. 扩展 `msgTypeLabel` 函数，添加更多消息类型映射
2. 添加 `formatMessageContent` 函数，自动替换消息内容中的英文状态码

**涉及文件：**
- `frontend/src/views/msg/MsgDialog.vue`
- `frontend/src/views/admin/msg.vue`

**消息类型映射：**
```javascript
const msgTypeMap = {
  SYSTEM: '系统通知',
  NOTICE: '普通通知',
  ALERT: '预警通知',
  SERVICE_ORDER: '服务订单',
  FAMILY_BINDING: '家属绑定',
  HEALTH_ALERT: '健康预警',
  PAYMENT: '支付通知'
}
```

---

## 2. 分页功能问题

### 2.1 缺少分页功能的页面

**问题描述：**
多个列表页面缺少分页功能，数据量大时影响性能和用户体验

**修复方案：**
为以下页面添加分页功能：
- 添加 `total` 变量存储总记录数
- 在 `query` 对象中添加 `pageNo` 和 `pageSize`
- 添加 `handleSizeChange` 方法处理每页显示数量变化
- 添加 `el-pagination` 组件

**涉及文件：**

**管理员页面：**
- `frontend/src/views/admin/orders.vue` - 订单调度
- `frontend/src/views/admin/content.vue` - 内容管理
- `frontend/src/views/admin/health.vue` - 健康预警
- `frontend/src/views/admin/community.vue` - 社区互动
- `frontend/src/views/admin/bindings.vue` - 家属绑定（绑定记录和服务人员审核分别独立分页）

**老人用户页面：**
- `frontend/src/views/elder/services.vue` - 服务大厅
- `frontend/src/views/elder/orders.vue` - 我的订单
- `frontend/src/views/elder/content.vue` - 资讯活动
- `frontend/src/views/elder/family.vue` - 家属授权

**家属用户页面：**
- `frontend/src/views/family/bindings.vue` - 绑定记录
- `frontend/src/views/family/health.vue` - 健康监测
- `frontend/src/views/family/orders.vue` - 服务同步

**服务人员页面：**
- `frontend/src/views/staff/orders.vue` - 接单中心
- `frontend/src/views/staff/schedule.vue` - 服务排班表

---

### 2.2 分页组件布局问题

**问题描述：**
- 部分页面的分页组件下方出现大面积空白
- 分页组件布局不统一

**修复方案：**
1. 修改全局样式类 `.page-pagination-right`，使用 `text-align: right` 替代 `display: flex`
2. 统一所有分页组件使用 `.page-pagination-right` 类

**涉及文件：**
- `frontend/src/assets/styles/_common.scss` - 全局样式
- 所有包含分页组件的页面

**统一样式：**
```scss
.page-pagination-right {
  margin-top: 16px;
  text-align: right;
}
```

---

## 3. 头像上传功能问题

### 3.1 头像上传后无法保存和同步

**问题描述：**
1. 个人中心上传头像后显示成功，但刷新页面后头像丢失
2. 上传成功后其他页面（如顶部导航栏）无法查看更新后的头像
3. 上传时出现双重提示（成功和失败同时显示）

**修复方案：**
1. 头像上传成功后立即调用 `updateProfile` API 保存到数据库
2. 修复 `UserDropdown` 组件，添加 `:src="userInfo?.avatarUrl"` 属性
3. 优化 `ImageUpload` 组件的成功回调逻辑
4. 上传成功后自动调用 `userStore.fetchMe()` 重新获取用户信息确保全局同步
5. 简化错误处理，避免重复提示

**涉及文件：**
- `frontend/src/views/profile/index.vue` - 个人中心页面
- `frontend/src/components/ImageUpload/index.vue` - 图片上传组件
- `frontend/src/layout/components/UserDropdown.vue` - 用户下拉菜单

---

### 3.2 头像上传双重提示问题

**问题描述：**
头像上传时会同时显示"上传成功"和"上传失败"两个提示

**修复方案：**
1. 在 `handleUploadError` 中移除重复的错误提示（因为拦截器已经显示过）
2. 在 `handleUploadSuccess` 中简化条件判断，只在真正成功时显示提示
3. 拦截器已经验证过 `success` 字段，能进入 `handleUploadSuccess` 说明上传成功

**涉及文件：**
- `frontend/src/components/ImageUpload/index.vue`

---

## 4. 登录页面问题

### 4.1 特点说明图标无法显示

**问题描述：**
登录和注册页面的四个特点说明（贴心服务、安全保障、家属协同、及时响应）的图标无法正常显示

**修复方案：**
将图标组件从 `template` 字符串改为 `render` 函数（使用 Vue 的 `h()` 函数）

**涉及文件：**
- `frontend/src/views/login/index.vue`

**修复前：**
```javascript
const features = [
  { icon: '<HeartIcon />', title: '贴心服务', desc: '...' }
]
```

**修复后：**
```javascript
import { h } from 'vue'
const features = [
  { icon: () => h(HeartIcon), title: '贴心服务', desc: '...' }
]
```

---

### 4.2 移除忘记密码功能

**问题描述：**
登录表单中包含"忘记密码？"链接，但该功能未实现

**修复方案：**
移除登录表单中的"忘记密码？"链接及相关样式

**涉及文件：**
- `frontend/src/views/login/index.vue`

---

## 5. 国际化问题

### 5.1 Element Plus 组件未中文化

**问题描述：**
分页组件、日期选择器、表单验证等 Element Plus 组件显示英文文本

**修复方案：**
在 `main.ts` 中导入并配置 Element Plus 中文语言包

**涉及文件：**
- `frontend/src/main.ts`

**修复代码：**
```typescript
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

app.use(ElementPlus, {
  locale: zhCn,
})
```

---

## 6. 项目名称更新

### 6.1 统一项目名称为"慧养家园"

**问题描述：**
项目名称在不同地方显示不一致，部分地方仍显示"社区老人服务"

**修复方案：**
将项目名称从"社区老人服务"统一更新为"慧养家园"，英文名称为"Smart Care Home"

**涉及文件：**
- `frontend/src/views/login/index.vue` - 登录页面
- `frontend/src/layout/components/SidebarMenu.vue` - 侧边栏
- `frontend/src/views/dashboard/DashboardBoard.vue` - 工作台
- `frontend/src/views/elder/services.vue` - 服务大厅
- `frontend/index.html` - HTML 标题
- `frontend/src/router/index.ts` - 路由配置
- `frontend/package.json` - 包配置
- `backend/pom.xml` - Maven 配置
- `README.md` - 项目说明

---

## 修复统计

### 按类型统计

| 类型 | 数量 | 占比 |
|------|------|------|
| 状态显示一致性 | 4 | 26.7% |
| 分页功能 | 2 | 13.3% |
| 头像上传 | 2 | 13.3% |
| 登录页面 | 2 | 13.3% |
| 国际化 | 1 | 6.7% |
| 项目名称 | 1 | 6.7% |
| **总计** | **15** | **100%** |

### 按影响范围统计

| 影响范围 | 数量 |
|----------|------|
| 前端页面 | 30+ |
| 后端配置 | 1 |
| 全局样式 | 2 |
| 组件 | 3 |

---

## 测试建议

### 功能测试清单

- [ ] 所有列表页面的状态显示为中文
- [ ] 所有列表页面的分页功能正常
- [ ] 头像上传、保存和同步功能正常
- [ ] 登录页面图标显示正常
- [ ] Element Plus 组件显示中文
- [ ] 项目名称统一为"慧养家园"
- [ ] 消息通知中的状态显示为中文

### 回归测试重点

1. **状态筛选功能**：确保所有状态筛选下拉框能正常工作
2. **分页功能**：测试切换页码、修改每页显示数量
3. **头像上传**：测试上传、保存、刷新后显示
4. **消息通知**：测试各类消息的类型和内容显示

---

## 维护说明

### 添加新状态码

如需添加新的状态码，请在以下位置同步更新：

1. **订单状态**：搜索 `orderStatusLabel` 或 `orderStatusMap`
2. **绑定状态**：搜索 `bindingStatusLabel` 或 `bindingStatusMap`
3. **消息类型**：搜索 `msgTypeLabel` 或 `msgTypeMap`
4. **内容类型**：搜索 `contentTypeLabel` 或 `contentTypeMap`

### 添加新分页页面

为新页面添加分页功能时，请遵循以下步骤：

1. 添加 `total` 变量
2. 在 `query` 中添加 `pageNo` 和 `pageSize`
3. 添加 `handleSizeChange` 方法
4. 使用 `.page-pagination-right` 类包裹分页组件

---

**文档版本：** 1.0  
**最后更新：** 2026-04-25  
**维护人员：** 开发团队
