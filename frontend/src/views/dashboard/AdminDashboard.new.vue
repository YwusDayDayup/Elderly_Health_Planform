<template>
  <div class="admin-dashboard">
    <!-- 欢迎头部 -->
    <div class="dash-header">
      <div>
        <h2 class="dash-title">社区概览</h2>
        <p class="dash-subtitle">实时监控与服务管理工作台。</p>
      </div>
      <div class="dash-actions">
        <button class="btn-outline" @click="router.push('/admin/orders')">
          <span class="material-symbols-outlined">calendar_today</span>
          订单调度
        </button>
        <button class="btn-primary" @click="router.push('/admin/orders')">
          <span class="material-symbols-outlined">add</span>
          新增服务订单
        </button>
      </div>
    </div>

    <!-- 数据卡片 -->
    <div class="metrics-grid">
      <div class="metric-card border-primary">
        <div class="metric-top">
          <div>
            <p class="metric-label">总用户数</p>
            <h3 class="metric-value">{{ overview?.totalUsers ?? '—' }}</h3>
          </div>
          <div class="metric-icon bg-primary-soft text-primary">
            <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1">groups</span>
          </div>
        </div>
        <div class="metric-trend">
          <span class="trend-up">
            <span class="material-symbols-outlined" style="font-size:14px">trending_up</span>
            今日新增 {{ overview?.todayNewUsers ?? 0 }}
          </span>
        </div>
      </div>

      <div class="metric-card border-teal">
        <div class="metric-top">
          <div>
            <p class="metric-label">活跃用户</p>
            <h3 class="metric-value">{{ dashData?.metrics?.[0]?.value ?? '—' }}</h3>
          </div>
          <div class="metric-icon bg-teal-soft text-teal">
            <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1">person_play</span>
          </div>
        </div>
        <div class="metric-trend text-muted">
          <span class="dot-teal"></span>
          {{ dashData?.metrics?.[0]?.trend ?? '数据加载中' }}
        </div>
      </div>

      <div class="metric-card border-blue">
        <div class="metric-top">
          <div>
            <p class="metric-label">服务订单</p>
            <h3 class="metric-value">{{ dashData?.latestOrders?.length ?? '—' }}</h3>
          </div>
          <div class="metric-icon bg-blue-soft text-blue">
            <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1">assignment</span>
          </div>
        </div>
        <div class="metric-trend">
          <span class="trend-up">
            <span class="material-symbols-outlined" style="font-size:14px">check_circle</span>
            {{ completedOrderCount }} 今日已完成
          </span>
        </div>
      </div>

      <div class="metric-card border-error">
        <div class="metric-top">
          <div>
            <p class="metric-label">健康预警</p>
            <h3 class="metric-value text-error">{{ dashData?.latestAlerts?.length ?? '—' }}</h3>
          </div>
          <div class="metric-icon bg-error-soft text-error">
            <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1">warning</span>
          </div>
        </div>
        <div class="metric-trend">
          <span class="trend-error">
            <span class="material-symbols-outlined" style="font-size:14px">priority_high</span>
            {{ criticalAlertCount }} 需立即处理
          </span>
        </div>
      </div>
    </div>

    <!-- 主网格 -->
    <div class="main-grid">
      <!-- 健康预警表格 -->
      <div class="card health-card">
        <div class="card-header">
          <div class="card-title-group">
            <div class="title-bar error"></div>
            <h3 class="card-title">重点健康预警</h3>
          </div>
          <button class="link-btn" @click="router.push('/admin/health')">查看全部体征</button>
        </div>
        <div class="table-wrap">
          <table class="data-table">
            <thead>
              <tr>
                <th>居民</th>
                <th>预警类型</th>
                <th class="text-center">严重程度</th>
                <th>内容</th>
                <th class="text-right">操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="alert in dashData?.latestAlerts?.slice(0, 5) ?? []" :key="alert.id">
                <td>
                  <div class="user-cell">
                    <div class="avatar-circle">{{ alert.nickname?.charAt(0) ?? '?' }}</div>
                    <div>
                      <p class="user-name">{{ alert.nickname }}</p>
                      <p class="user-sub">{{ alert.username }}</p>
                    </div>
                  </div>
                </td>
                <td>
                  <div class="flex-icon">
                    <span class="material-symbols-outlined text-primary" style="font-size:18px">monitor_heart</span>
                    <span>{{ alert.alertType }}</span>
                  </div>
                </td>
                <td class="text-center">
                  <span :class="alertLevelClass(alert.alertLevel)" class="badge">
                    {{ alertLevelLabel(alert.alertLevel) }}
                  </span>
                </td>
                <td class="text-muted text-sm">{{ alert.title }}</td>
                <td class="text-right">
                  <button class="link-btn" @click="router.push('/admin/health')">查看详情</button>
                </td>
              </tr>
              <tr v-if="!dashData?.latestAlerts?.length">
                <td colspan="5" class="empty-row">暂无健康预警</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 服务订单侧栏 -->
      <div class="card order-card">
        <div class="card-header">
          <div>
            <div class="order-title-row">
              <h3 class="card-title">服务订单</h3>
              <span class="live-badge">实时</span>
            </div>
            <p class="card-subtitle">社区居民发起的最新服务请求。</p>
          </div>
        </div>
        <div class="order-list">
          <div
            v-for="order in dashData?.latestOrders?.slice(0, 5) ?? []"
            :key="order.id"
            class="order-item"
            @click="router.push('/admin/orders')"
          >
            <div class="order-item-top">
              <span class="order-no">#{{ order.orderNo?.slice(-6) }}</span>
              <span :class="orderStatusClass(order.orderStatus)" class="status-badge">
                {{ orderStatusLabel(order.orderStatus) }}
              </span>
            </div>
            <div class="order-item-body">
              <div class="order-icon-wrap">
                <span class="material-symbols-outlined">medical_services</span>
              </div>
              <div>
                <p class="order-name">{{ order.serviceName }}</p>
                <p class="order-desc">{{ order.serviceAddress }}</p>
                <div class="order-meta">
                  <span><span class="material-symbols-outlined" style="font-size:13px">schedule</span> {{ formatTime(order.createTime) }}</span>
                  <span><span class="material-symbols-outlined" style="font-size:13px">person</span> {{ order.elderName }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-if="!dashData?.latestOrders?.length" class="empty-orders">暂无订单数据</div>
        </div>
        <div class="order-footer">
          <button class="link-btn-muted" @click="router.push('/admin/orders')">查看全部订单历史</button>
        </div>
      </div>
    </div>

    <!-- 图表行 -->
    <div class="chart-grid">
      <div class="card">
        <div class="card-header">
          <div class="card-title-group">
            <div class="title-bar primary"></div>
            <h3 class="card-title">用户角色分布</h3>
          </div>
          <span class="chart-sub">按当前平台账号统计</span>
        </div>
        <div ref="roleChartRef" class="chart-host"></div>
      </div>
      <div class="card">
        <div class="card-header">
          <div class="card-title-group">
            <div class="title-bar primary"></div>
            <h3 class="card-title">近 7 天平台趋势</h3>
          </div>
          <span class="chart-sub">用户、文件、消息增长</span>
        </div>
        <div ref="trendChartRef" class="chart-host chart-host-wide"></div>
      </div>
    </div>

    <!-- 底部快捷管理 -->
    <div class="bottom-grid">
      <div class="promo-card promo-primary">
        <div class="promo-content">
          <h4 class="promo-title">排班任务管理</h4>
          <p class="promo-desc">为紧急预警和待处理服务订单分配护士或护理助理。</p>
          <button class="promo-btn" @click="router.push('/admin/orders')">打开人员名册</button>
        </div>
        <div class="promo-deco">
          <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1">badge</span>
        </div>
      </div>
      <div class="promo-card promo-teal">
        <div class="promo-content">
          <h4 class="promo-title">后勤保障调度</h4>
          <p class="promo-desc">监控食堂库存、医疗耗材以及设施维护计划。</p>
          <button class="promo-btn" @click="router.push('/admin/system')">进入后勤门户</button>
        </div>
        <div class="promo-deco">
          <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1">inventory</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAdminDashboard, type DashboardVO } from '@/api/care'
import { statisticsOverview, statisticsTrend, type OverviewVO, type TrendVO } from '@/api/statistics'
import * as echarts from 'echarts/core'
import { PieChart, LineChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import type { ECharts } from 'echarts/core'

echarts.use([PieChart, LineChart, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

const router = useRouter()
const dashData = ref<DashboardVO | null>(null)
const overview = ref<OverviewVO | null>(null)
const trend = ref<TrendVO | null>(null)
const roleChartRef = ref<HTMLDivElement | null>(null)
const trendChartRef = ref<HTMLDivElement | null>(null)
let roleChart: ECharts | null = null
let trendChart: ECharts | null = null

const completedOrderCount = computed(() =>
  dashData.value?.latestOrders?.filter(o => o.orderStatus === 'COMPLETED').length ?? 0
)
const criticalAlertCount = computed(() =>
  dashData.value?.latestAlerts?.filter(a => a.alertLevel === 'HIGH' || a.alertLevel === 'CRITICAL').length ?? 0
)

const orderStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    PENDING_PAYMENT: '待支付', PENDING_ASSIGN: '待派单', PENDING_ACCEPT: '待接单',
    PENDING_SERVICE: '待上门', IN_SERVICE: '服务中', COMPLETED: '已完成',
    CANCELLED: '已取消', REFUNDED: '已退款',
  }
  return map[status] ?? status
}

const orderStatusClass = (status: string) => {
  if (status === 'COMPLETED') return 'status-green'
  if (status === 'IN_SERVICE') return 'status-orange'
  if (status === 'CANCELLED' || status === 'REFUNDED') return 'status-gray'
  return 'status-blue'
}

const alertLevelLabel = (level: string) => {
  const map: Record<string, string> = { HIGH: '危险', CRITICAL: '危险', MEDIUM: '警告', LOW: '中度' }
  return map[level] ?? level
}

const alertLevelClass = (level: string) => {
  if (level === 'HIGH' || level === 'CRITICAL') return 'badge-error'
  if (level === 'MEDIUM') return 'badge-warn'
  return 'badge-gray'
}

const formatTime = (time: string) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const diff = Math.floor((now.getTime() - d.getTime()) / 60000)
  if (diff < 60) return `${diff} 分钟前`
  if (diff < 1440) return `${Math.floor(diff / 60)} 小时前`
  return `${Math.floor(diff / 1440)} 天前`
}

const roleLabelMap: Record<string, string> = {
  ADMIN: '管理员', ELDER: '老人用户', FAMILY: '家属用户', STAFF: '服务人员',
}

const renderRoleChart = async () => {
  if (!roleChartRef.value || !overview.value) return
  await nextTick()
  roleChart ??= echarts.init(roleChartRef.value)
  roleChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}<br/>{c} 人 ({d}%)' },
    legend: { bottom: 0, left: 'center', icon: 'circle', textStyle: { color: '#6B7280' } },
    series: [{
      type: 'pie', radius: ['46%', '72%'], center: ['50%', '44%'],
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 3 },
      label: { formatter: '{b}\n{c}人', fontSize: 12 },
      color: ['#E85A3C', '#2D6A4F', '#D4A853', '#EC4899'],
      data: overview.value.roleDistribution.map(item => ({
        name: roleLabelMap[item.name] || item.name, value: item.count,
      })),
    }]
  })
}

const renderTrendChart = async () => {
  if (!trendChartRef.value || !trend.value) return
  await nextTick()
  trendChart ??= echarts.init(trendChartRef.value)
  const days = trend.value.list.map(item => item.day.slice(5))
  trendChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { top: 0, right: 0, textStyle: { color: '#6B7280' } },
    grid: { left: 12, right: 12, top: 48, bottom: 12, containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: days, axisLine: { lineStyle: { color: '#D6D3D1' } }, axisLabel: { color: '#6B7280' } },
    yAxis: { type: 'value', splitLine: { lineStyle: { color: '#EEE7E1' } }, axisLabel: { color: '#6B7280' } },
    series: [
      { name: '新增用户', type: 'line', smooth: true, symbolSize: 8, lineStyle: { width: 3, color: '#E85A3C' }, itemStyle: { color: '#E85A3C' }, areaStyle: { color: 'rgba(232,90,60,0.12)' }, data: trend.value.list.map(i => i.userCount) },
      { name: '上传文件', type: 'line', smooth: true, symbolSize: 8, lineStyle: { width: 3, color: '#2D6A4F' }, itemStyle: { color: '#2D6A4F' }, areaStyle: { color: 'rgba(45,106,79,0.10)' }, data: trend.value.list.map(i => i.fileCount) },
      { name: '消息数量', type: 'line', smooth: true, symbolSize: 8, lineStyle: { width: 3, color: '#D4A853' }, itemStyle: { color: '#D4A853' }, areaStyle: { color: 'rgba(212,168,83,0.10)' }, data: trend.value.list.map(i => i.msgCount) },
    ]
  })
}

const resizeCharts = () => { roleChart?.resize(); trendChart?.resize() }

onMounted(async () => {
  const [dashRes, overviewRes, trendRes] = await Promise.all([
    getAdminDashboard(), statisticsOverview(), statisticsTrend(7),
  ])
  if (dashRes.success) dashData.value = dashRes.data
  if (overviewRes.success) overview.value = overviewRes.data
  if (trendRes.success) trend.value = trendRes.data
  renderRoleChart()
  renderTrendChart()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  roleChart?.dispose()
  trendChart?.dispose()
})
</script>

<style scoped>
/* Google Material Symbols font */
@import url('https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap');

.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-weight: normal;
  font-style: normal;
  font-size: 22px;
  line-height: 1;
  letter-spacing: normal;
  text-transform: none;
  display: inline-block;
  white-space: nowrap;
  word-wrap: normal;
  direction: ltr;
  -webkit-font-smoothing: antialiased;
  vertical-align: middle;
}

.admin-dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
  padding: 4px 0;
}

/* Header */
.dash-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  flex-wrap: wrap;
  gap: 12px;
}
.dash-title {
  font-size: 28px;
  font-weight: 700;
  color: #191c1e;
  margin: 0 0 4px;
}
.dash-subtitle {
  font-size: 16px;
  color: #4f6076;
  margin: 0;
}
.dash-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}
.btn-outline {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 9px 16px;
  background: #fff;
  border: 1px solid #e1bfb8;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #191c1e;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-outline:hover { background: #f2f4f7; }
.btn-primary {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 9px 18px;
  background: linear-gradient(180deg, #EF7055 0%, #E85A3C 100%);
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(232,90,60,0.3);
  transition: opacity 0.15s;
}
.btn-primary:hover { opacity: 0.9; }

/* Metrics */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
@media (max-width: 1100px) { .metrics-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 600px) { .metrics-grid { grid-template-columns: 1fr; } }

.metric-card {
  background: #fff;
  border-radius: 12px;
  padding: 22px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  border-left: 4px solid transparent;
}
.border-primary { border-left-color: #ab2e14; }
.border-teal { border-left-color: #006576; }
.border-blue { border-left-color: #4f6076; }
.border-error { border-left-color: #ba1a1a; }

.metric-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}
.metric-label {
  font-size: 13px;
  font-weight: 500;
  color: #4f6076;
  margin: 0 0 6px;
}
.metric-value {
  font-size: 30px;
  font-weight: 700;
  color: #191c1e;
  margin: 0;
  line-height: 1.1;
}
.metric-value.text-error { color: #ba1a1a; }
.metric-icon {
  width: 44px;
  height: 44px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.bg-primary-soft { background: #ffdad3; }
.text-primary { color: #ab2e14; }
.bg-teal-soft { background: #aaedff; }
.text-teal { color: #006576; }
.bg-blue-soft { background: #d2e4ff; }
.text-blue { color: #4f6076; }
.bg-error-soft { background: #ffdad6; }
.text-error { color: #ba1a1a; }

.metric-trend {
  margin-top: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #6b7280;
}
.trend-up {
  display: flex;
  align-items: center;
  gap: 3px;
  color: #006576;
  font-weight: 600;
}
.trend-error {
  display: flex;
  align-items: center;
  gap: 3px;
  color: #ba1a1a;
  font-weight: 600;
}
.dot-teal {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #006576;
  display: inline-block;
}
.text-muted { color: #6b7280; }

/* Main grid */
.main-grid {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 20px;
}
@media (max-width: 1100px) { .main-grid { grid-template-columns: 1fr; } }

/* Card */
.card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
  border: 1px solid #f1f3f5;
  overflow: hidden;
}
.card-header {
  padding: 18px 22px;
  border-bottom: 1px solid #f1f3f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title-group {
  display: flex;
  align-items: center;
  gap: 10px;
}
.title-bar {
  width: 4px;
  height: 22px;
  border-radius: 4px;
}
.title-bar.error { background: #ba1a1a; }
.title-bar.primary { background: #ab2e14; }
.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #191c1e;
  margin: 0;
}
.card-subtitle {
  font-size: 13px;
  color: #4f6076;
  margin: 4px 0 0;
}
.link-btn {
  background: none;
  border: none;
  color: #ab2e14;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  padding: 0;
}
.link-btn:hover { text-decoration: underline; }
.chart-sub {
  font-size: 12px;
  color: #6b7280;
}

/* Table */
.table-wrap { overflow-x: auto; }
.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.data-table thead tr {
  background: #f8fafc;
}
.data-table th {
  padding: 12px 20px;
  font-size: 11px;
  font-weight: 600;
  color: #4f6076;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  white-space: nowrap;
}
.data-table td {
  padding: 16px 20px;
  border-top: 1px solid #f1f3f5;
  vertical-align: middle;
}
.data-table tbody tr:hover { background: #fafafa; }
.text-center { text-align: center; }
.text-right { text-align: right; }
.text-sm { font-size: 13px; }

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.avatar-circle {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: linear-gradient(135deg, #E85A3C, #ab2e14);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 15px;
  flex-shrink: 0;
}
.user-name { font-weight: 600; color: #191c1e; margin: 0; font-size: 14px; }
.user-sub { font-size: 12px; color: #6b7280; margin: 2px 0 0; }

.flex-icon {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #191c1e;
}

.badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  white-space: nowrap;
}
.badge-error { background: #ffdad6; color: #ba1a1a; border: 1px solid rgba(186,26,26,0.1); }
.badge-warn { background: #ffdad3; color: #ab2e14; border: 1px solid rgba(171,46,20,0.1); }
.badge-gray { background: #e6e8eb; color: #4f6076; border: 1px solid #d0d3d8; }

.empty-row { text-align: center; color: #9ca3af; padding: 32px; }

/* Order card */
.order-card {
  display: flex;
  flex-direction: column;
}
.order-title-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.live-badge {
  background: #e6e8eb;
  color: #4f6076;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 4px;
}
.order-list {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 480px;
}
.order-item {
  padding: 14px;
  border-radius: 10px;
  background: #f8fafc;
  border: 1px solid #e6e8eb;
  cursor: pointer;
  transition: border-color 0.15s;
}
.order-item:hover { border-color: #cd472b; }
.order-item-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.order-no { font-size: 11px; font-weight: 600; color: #4f6076; }
.status-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 4px;
}
.status-orange { background: #fff3e0; color: #e65100; }
.status-blue { background: #aaedff; color: #004e5c; }
.status-green { background: #d1fae5; color: #065f46; }
.status-gray { background: #e6e8eb; color: #4f6076; }

.order-item-body {
  display: flex;
  gap: 10px;
}
.order-icon-wrap {
  width: 38px;
  height: 38px;
  border-radius: 8px;
  background: #fff3e0;
  color: #e65100;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.order-name { font-weight: 600; color: #191c1e; font-size: 14px; margin: 0 0 2px; }
.order-desc { font-size: 12px; color: #6b7280; margin: 0 0 6px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; max-width: 220px; }
.order-meta {
  display: flex;
  gap: 10px;
  font-size: 11px;
  color: #9ca3af;
}
.order-meta span { display: flex; align-items: center; gap: 2px; }
.empty-orders { text-align: center; color: #9ca3af; padding: 32px 0; font-size: 14px; }
.order-footer {
  padding: 12px;
  background: #f8fafc;
  text-align: center;
}
.link-btn-muted {
  background: none;
  border: none;
  color: #4f6076;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
}
.link-btn-muted:hover { color: #ab2e14; }

/* Charts */
.chart-grid {
  display: grid;
  grid-template-columns: 2fr 3fr;
  gap: 20px;
}
@media (max-width: 900px) { .chart-grid { grid-template-columns: 1fr; } }

.chart-host {
  height: 240px;
  width: 100%;
}
.chart-host-wide {
  height: 240px;
}

/* Bottom promo */
.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}
@media (max-width: 700px) { .bottom-grid { grid-template-columns: 1fr; } }

.promo-card {
  border-radius: 12px;
  padding: 28px 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  overflow: hidden;
  position: relative;
  box-shadow: 0 4px 16px rgba(0,0,0,0.12);
}
.promo-primary { background: #ab2e14; color: #fff; }
.promo-teal { background: #006576; color: #fff; }

.promo-content { position: relative; z-index: 1; }
.promo-title { font-size: 22px; font-weight: 700; margin: 0 0 8px; }
.promo-desc { font-size: 14px; opacity: 0.85; margin: 0 0 20px; max-width: 280px; line-height: 1.5; }
.promo-btn {
  background: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 22px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: opacity 0.15s;
}
.promo-primary .promo-btn { color: #ab2e14; }
.promo-teal .promo-btn { color: #006576; }
.promo-btn:hover { opacity: 0.9; }

.promo-deco {
  position: absolute;
  right: 0;
  top: 0;
  height: 100%;
  width: 30%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0.1;
  pointer-events: none;
}
.promo-deco .material-symbols-outlined {
  font-size: 160px;
}
</style>
