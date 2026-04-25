<template>
  <div class="dashboard-board">
    <!-- Hero Card -->
    <el-card shadow="never" class="hero-card">
      <div class="hero-head">
        <div class="hero-content">
          <p class="hero-kicker">
            <span class="kicker-dot"></span>
            {{ formatRole(data?.role) }}
          </p>
          <h2>{{ data?.title || '慧养家园服务系统' }}</h2>
          <p class="hero-desc">围绕服务预约、健康记录、家属协同与系统治理组织日常工作与生活服务。</p>
        </div>
        <div class="hero-decoration">
          <div class="deco-ring ring-1"></div>
          <div class="deco-ring ring-2"></div>
        </div>
      </div>
    </el-card>

    <!-- Metrics Row -->
    <el-row :gutter="16" class="metric-row">
      <el-col v-for="(item, index) in data?.metrics || []" :key="item.label" :xs="12" :sm="12" :lg="6">
        <el-card shadow="never" class="metric-card" :class="`metric-card-${index}`">
          <div class="metric-icon-wrap">
            <div class="metric-icon">
              <component :is="metricIcons[index]" />
            </div>
          </div>
          <div class="metric-label">{{ item.label }}</div>
          <div class="metric-value">{{ item.value }}</div>
          <div class="metric-trend">
            <span class="trend-arrow">↑</span>
            {{ item.trend }}
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row v-if="props.admin" :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="9">
        <el-card shadow="never" class="content-card chart-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>用户角色分布</span>
              </div>
              <span class="chart-subtitle">按当前平台账号统计</span>
            </div>
          </template>
          <div ref="roleChartRef" class="chart-host"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="15">
        <el-card shadow="never" class="content-card chart-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>近 7 天平台趋势</span>
              </div>
              <span class="chart-subtitle">用户、文件、消息增长</span>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-host chart-host-wide"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row v-else-if="data?.role === 'ELDER'" :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="9">
        <el-card shadow="never" class="content-card chart-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>我的订单分布</span>
              </div>
              <span class="chart-subtitle">最近服务状态一目了然</span>
            </div>
          </template>
          <div ref="elderOrderChartRef" class="chart-host"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="15">
        <el-card shadow="never" class="content-card chart-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>近 7 次健康记录</span>
              </div>
              <span class="chart-subtitle">血压和血糖趋势</span>
            </div>
          </template>
          <div ref="elderHealthChartRef" class="chart-host chart-host-wide"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row v-else-if="data?.role === 'STAFF'" :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="9">
        <el-card shadow="never" class="content-card chart-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>我的工单分布</span>
              </div>
              <span class="chart-subtitle">接单与执行状态总览</span>
            </div>
          </template>
          <div ref="staffOrderChartRef" class="chart-host"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="15">
        <el-card shadow="never" class="content-card chart-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>近期服务排班</span>
              </div>
              <span class="chart-subtitle">未来 7 天服务节奏</span>
            </div>
          </template>
          <div ref="staffScheduleChartRef" class="chart-host chart-host-wide"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row v-else-if="data?.role === 'FAMILY'" :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="9">
        <el-card shadow="never" class="content-card chart-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>我的协同分布</span>
              </div>
              <span class="chart-subtitle">绑定与服务同步总览</span>
            </div>
          </template>
          <div ref="familyBindingChartRef" class="chart-host"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="15">
        <el-card shadow="never" class="content-card chart-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>健康与服务趋势</span>
              </div>
              <span class="chart-subtitle">绑定老人健康异常与订单完成情况</span>
            </div>
          </template>
          <div ref="familyTrendChartRef" class="chart-host chart-host-wide"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Main Content Grid -->
    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="content-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>最新内容</span>
              </div>
              <el-button link type="primary" @click="router.push(contentPath)">查看全部</el-button>
            </div>
          </template>
          <el-table :data="data?.latestContents || []" size="small" class="modern-table">
            <el-table-column prop="contentType" label="类型" width="100">
              <template #default="{ row }">
                <span class="type-badge">{{ contentTypeLabel(row.contentType) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" min-width="220" />
            <el-table-column prop="publishedAt" label="发布时间" width="160" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="content-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>最新订单</span>
              </div>
              <el-button link type="primary" @click="router.push(orderPath)">查看全部</el-button>
            </div>
          </template>
          <div class="mini-list">
            <div v-for="item in data?.latestOrders || []" :key="item.id" class="mini-item">
              <div class="mini-item-main">
                <strong>{{ item.serviceName }}</strong>
                <span>{{ orderStatusLabelMap[item.orderStatus] || item.orderStatus }} · {{ item.serviceTime }}</span>
              </div>
              <div class="mini-item-arrow">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="m9 18 6-6-6-6"/>
                </svg>
              </div>
            </div>
            <el-empty v-if="!(data?.latestOrders || []).length" description="暂无订单数据" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Bottom Row -->
    <el-row :gutter="16" class="bottom-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="content-card">
          <template #header>
            <div class="section-head">
              <div class="section-title warning">
                <span class="section-dot"></span>
                <span>预警提醒</span>
              </div>
            </div>
          </template>
          <div class="mini-list">
            <div v-for="item in data?.latestAlerts || []" :key="item.id" class="mini-item alert-item">
              <div class="mini-item-main">
                <strong>{{ item.title }}</strong>
                <span>{{ item.content }}</span>
              </div>
              <div class="alert-badge">!</div>
            </div>
            <el-empty v-if="!(data?.latestAlerts || []).length" description="暂无预警" :image-size="60" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="never" class="content-card">
          <template #header>
            <div class="section-head">
              <div class="section-title">
                <span class="section-dot"></span>
                <span>绑定关系</span>
              </div>
            </div>
          </template>
          <div class="mini-list">
            <div v-for="item in data?.latestBindings || []" :key="item.id" class="mini-item binding-item">
              <div class="binding-avatars">
                <el-avatar :size="32" class="binding-avatar">{{ item.elderName?.charAt(0) }}</el-avatar>
                <el-avatar :size="32" class="binding-avatar second">{{ item.familyName?.charAt(0) }}</el-avatar>
              </div>
              <div class="mini-item-main">
                <strong>{{ item.elderName }} / {{ item.familyName }}</strong>
                <span>{{ item.relationLabel }} · {{ bindingStatusLabelMap[item.status] || item.status }}</span>
              </div>
            </div>
            <el-empty v-if="!(data?.latestBindings || []).length" description="暂无绑定数据" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, nextTick, ref, h } from 'vue'
import { useRouter } from 'vue-router'
import {
  getAdminDashboard,
  getMyDashboard,
  myFamilyBindings,
  myHealthRecords,
  myServiceOrders,
  type DashboardVO,
  type FamilyBindingVO,
  type HealthRecordVO,
  type ServiceOrderVO,
} from '@/api/care'
import { statisticsOverview, statisticsTrend, type OverviewVO, type TrendVO } from '@/api/statistics'
import { formatRole } from '@/utils/role'
import { useUserStore } from '@/stores/user'
import * as echarts from 'echarts/core'
import { PieChart, LineChart, BarChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import type { ECharts } from 'echarts/core'

const props = defineProps<{
  admin?: boolean
}>()

const router = useRouter()
const userStore = useUserStore()
const data = ref<DashboardVO | null>(null)
const overview = ref<OverviewVO | null>(null)
const trend = ref<TrendVO | null>(null)
const elderOrders = ref<ServiceOrderVO[]>([])
const elderRecords = ref<HealthRecordVO[]>([])
const elderBindings = ref<FamilyBindingVO[]>([])
const staffOrders = ref<ServiceOrderVO[]>([])
const familyOrders = ref<ServiceOrderVO[]>([])
const familyBindings = ref<FamilyBindingVO[]>([])
const familyRecords = ref<HealthRecordVO[]>([])
const roleChartRef = ref<HTMLDivElement | null>(null)
const trendChartRef = ref<HTMLDivElement | null>(null)
const elderOrderChartRef = ref<HTMLDivElement | null>(null)
const elderHealthChartRef = ref<HTMLDivElement | null>(null)
const staffOrderChartRef = ref<HTMLDivElement | null>(null)
const staffScheduleChartRef = ref<HTMLDivElement | null>(null)
const familyBindingChartRef = ref<HTMLDivElement | null>(null)
const familyTrendChartRef = ref<HTMLDivElement | null>(null)
let roleChart: ECharts | null = null
let trendChartInstance: ECharts | null = null
let elderOrderChart: ECharts | null = null
let elderHealthChart: ECharts | null = null
let staffOrderChart: ECharts | null = null
let staffScheduleChart: ECharts | null = null
let familyBindingChart: ECharts | null = null
let familyTrendChart: ECharts | null = null

echarts.use([PieChart, LineChart, BarChart, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

const UsersIcon = {
  render: () => h('svg', { width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
    h('path', { d: 'M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2' }),
    h('circle', { cx: 9, cy: 7, r: 4 }),
    h('path', { d: 'M22 21v-2a4 4 0 0 0-3-3.87' }),
    h('path', { d: 'M16 3.13a4 4 0 0 1 0 7.75' })
  ])
}

const TicketIcon = {
  render: () => h('svg', { width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
    h('path', { d: 'M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z' }),
    h('path', { d: 'M13 5v2' }),
    h('path', { d: 'M13 17v2' }),
    h('path', { d: 'M13 11v2' })
  ])
}

const ChartIcon = {
  render: () => h('svg', { width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
    h('path', { d: 'M3 3v18h18' }),
    h('path', { d: 'm19 9-5 5-4-4-3 3' })
  ])
}

const HeartIcon = {
  render: () => h('svg', { width: 20, height: 20, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', 'stroke-width': 2 }, [
    h('path', { d: 'M19 14c1.49-1.46 3-3.21 3-5.5A5.5 5.5 0 0 0 16.5 3c-1.76 0-3 .5-4.5 2-1.5-1.5-2.74-2-4.5-2A5.5 5.5 0 0 0 2 8.5c0 2.3 1.5 4.05 3 5.5l7 7Z' })
  ])
}

const metricIcons = [UsersIcon, TicketIcon, ChartIcon, HeartIcon]

const orderPath = computed(() => {
  const role = data.value?.role || userStore.userInfo?.role
  if (role === 'ADMIN') return '/admin/orders'
  if (role === 'STAFF') return '/staff/orders'
  if (role === 'FAMILY') return '/family/orders'
  return '/elder/orders'
})

const contentPath = computed(() => {
  const role = data.value?.role || userStore.userInfo?.role
  if (role === 'ADMIN') return '/admin/content'
  if (role === 'FAMILY') return '/family/health'
  if (role === 'STAFF') return '/staff/profile'
  return '/elder/content'
})

const roleLabelMap: Record<string, string> = {
  ADMIN: '管理员',
  ELDER: '老人用户',
  FAMILY: '家属用户',
  STAFF: '服务人员',
}

const resizeCharts = () => {
  roleChart?.resize()
  trendChartInstance?.resize()
  elderOrderChart?.resize()
  elderHealthChart?.resize()
  staffOrderChart?.resize()
  staffScheduleChart?.resize()
  familyBindingChart?.resize()
  familyTrendChart?.resize()
}

const renderRoleChart = async () => {
  if (!props.admin || !roleChartRef.value || !overview.value) return
  await nextTick()
  roleChart ??= echarts.init(roleChartRef.value)
  roleChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>{c} 人 ({d}%)'
    },
    legend: {
      bottom: 0,
      left: 'center',
      icon: 'circle',
      textStyle: {
        color: '#6B7280'
      }
    },
    series: [
      {
        type: 'pie',
        radius: ['46%', '72%'],
        center: ['50%', '44%'],
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 3
        },
        label: {
          formatter: '{b}\n{c}人',
          fontSize: 12
        },
        color: ['#E85A3C', '#2D6A4F', '#D4A853', '#EC4899'],
        data: overview.value.roleDistribution.map(item => ({
          name: roleLabelMap[item.name] || item.name,
          value: item.count,
        })),
      }
    ]
  })
}

const renderTrendChart = async () => {
  if (!props.admin || !trendChartRef.value || !trend.value) return
  await nextTick()
  trendChartInstance ??= echarts.init(trendChartRef.value)
  const days = trend.value.list.map(item => item.day.slice(5))
  trendChartInstance.setOption({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      top: 0,
      right: 0,
      textStyle: {
        color: '#6B7280'
      }
    },
    grid: {
      left: 12,
      right: 12,
      top: 48,
      bottom: 12,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: days,
      axisLine: {
        lineStyle: {
          color: '#D6D3D1'
        }
      },
      axisLabel: {
        color: '#6B7280'
      }
    },
    yAxis: {
      type: 'value',
      splitLine: {
        lineStyle: {
          color: '#EEE7E1'
        }
      },
      axisLabel: {
        color: '#6B7280'
      }
    },
    series: [
      {
        name: '新增用户',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: { width: 3, color: '#E85A3C' },
        itemStyle: { color: '#E85A3C' },
        areaStyle: { color: 'rgba(232, 90, 60, 0.12)' },
        data: trend.value.list.map(item => item.userCount),
      },
      {
        name: '上传文件',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: { width: 3, color: '#2D6A4F' },
        itemStyle: { color: '#2D6A4F' },
        areaStyle: { color: 'rgba(45, 106, 79, 0.10)' },
        data: trend.value.list.map(item => item.fileCount),
      },
      {
        name: '消息数量',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: { width: 3, color: '#D4A853' },
        itemStyle: { color: '#D4A853' },
        areaStyle: { color: 'rgba(212, 168, 83, 0.10)' },
        data: trend.value.list.map(item => item.msgCount),
      }
    ]
  })
}

const fetchAdminCharts = async () => {
  if (!props.admin) return
  const [overviewRes, trendRes] = await Promise.all([
    statisticsOverview(),
    statisticsTrend(7),
  ])
  if (overviewRes.success) overview.value = overviewRes.data
  if (trendRes.success) trend.value = trendRes.data
  renderRoleChart()
  renderTrendChart()
}

const orderStatusLabelMap: Record<string, string> = {
  PENDING_PAYMENT: '待支付',
  PENDING_ASSIGN: '待派单',
  PENDING_ACCEPT: '待接单',
  PENDING_SERVICE: '待上门',
  IN_SERVICE: '服务中',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  REFUNDED: '已退款',
}

const contentTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    NOTICE: '公告',
    NEWS: '新闻',
    ACTIVITY: '活动',
    HEALTH: '健康',
    POLICY: '政策',
  }
  return map[type] || type
}

const renderElderOrderChart = async () => {
  if (props.admin || data.value?.role !== 'ELDER' || !elderOrderChartRef.value) return
  await nextTick()
  const statusMap = elderOrders.value.reduce<Record<string, number>>((acc, item) => {
    const key = orderStatusLabelMap[item.orderStatus] || item.orderStatus
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {})
  const activeBindings = elderBindings.value.filter(item => item.status === 'ACTIVE').length
  const pieData = Object.entries(statusMap).map(([name, value]) => ({ name, value }))
  if (!pieData.length) {
    pieData.push(
      { name: '待支付', value: 0 },
      { name: '服务中', value: 0 },
      { name: '已完成', value: 0 },
    )
  }
  elderOrderChart ??= echarts.init(elderOrderChartRef.value)
  elderOrderChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>{c} 单 ({d}%)'
    },
    title: {
      text: `${elderOrders.value.length}`,
      subtext: `活跃绑定 ${activeBindings}`,
      left: 'center',
      top: '38%',
      textStyle: {
        fontSize: 28,
        fontWeight: 800,
        color: '#1F2937'
      },
      subtextStyle: {
        fontSize: 12,
        color: '#6B7280'
      }
    },
    series: [
      {
        type: 'pie',
        radius: ['52%', '76%'],
        center: ['50%', '46%'],
        label: {
          formatter: '{b}\n{c}单',
          fontSize: 12
        },
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 3
        },
        color: ['#E85A3C', '#2D6A4F', '#D4A853', '#F97316', '#0EA5E9', '#EC4899'],
        data: pieData,
      }
    ]
  })
}

const renderElderHealthChart = async () => {
  if (props.admin || data.value?.role !== 'ELDER' || !elderHealthChartRef.value) return
  await nextTick()
  const records = [...elderRecords.value]
    .sort((a, b) => String(a.recordDate).localeCompare(String(b.recordDate)))
    .slice(-7)
  elderHealthChart ??= echarts.init(elderHealthChartRef.value)
  elderHealthChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      top: 0,
      right: 0,
      textStyle: {
        color: '#6B7280'
      }
    },
    grid: {
      left: 12,
      right: 12,
      top: 48,
      bottom: 12,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: records.map(item => String(item.recordDate).slice(5)),
      axisLine: {
        lineStyle: {
          color: '#D6D3D1'
        }
      },
      axisLabel: {
        color: '#6B7280'
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '血压',
        axisLabel: { color: '#6B7280' },
        splitLine: {
          lineStyle: { color: '#EEE7E1' }
        }
      },
      {
        type: 'value',
        name: '血糖',
        axisLabel: { color: '#6B7280' },
      }
    ],
    series: [
      {
        name: '收缩压',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: { width: 3, color: '#E85A3C' },
        itemStyle: { color: '#E85A3C' },
        data: records.map(item => item.systolicBp ?? 0),
      },
      {
        name: '舒张压',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: { width: 3, color: '#2D6A4F' },
        itemStyle: { color: '#2D6A4F' },
        data: records.map(item => item.diastolicBp ?? 0),
      },
      {
        name: '血糖',
        type: 'bar',
        yAxisIndex: 1,
        barMaxWidth: 24,
        itemStyle: {
          color: '#D4A853',
          borderRadius: [8, 8, 0, 0]
        },
        data: records.map(item => Number(item.bloodSugar ?? 0)),
      }
    ]
  })
}

const fetchElderCharts = async () => {
  if (props.admin || data.value?.role !== 'ELDER') return
  const [orderRes, recordRes, bindingRes] = await Promise.all([
    myServiceOrders({ pageNo: 1, pageSize: 100 }),
    myHealthRecords({ pageNo: 1, pageSize: 100 }),
    myFamilyBindings({ pageNo: 1, pageSize: 100 }),
  ])
  if (orderRes.success) elderOrders.value = orderRes.data.list
  if (recordRes.success) elderRecords.value = recordRes.data.list
  if (bindingRes.success) elderBindings.value = bindingRes.data.list
  renderElderOrderChart()
  renderElderHealthChart()
}

const renderStaffOrderChart = async () => {
  if (props.admin || data.value?.role !== 'STAFF' || !staffOrderChartRef.value) return
  await nextTick()
  const statusMap = staffOrders.value.reduce<Record<string, number>>((acc, item) => {
    const key = orderStatusLabelMap[item.orderStatus] || item.orderStatus
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {})
  const pieData = Object.entries(statusMap).map(([name, value]) => ({ name, value }))
  if (!pieData.length) {
    pieData.push(
      { name: '待接单', value: 0 },
      { name: '待上门', value: 0 },
      { name: '服务中', value: 0 },
    )
  }
  const servingCount = staffOrders.value.filter(item => item.orderStatus === 'IN_SERVICE').length
  staffOrderChart ??= echarts.init(staffOrderChartRef.value)
  staffOrderChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>{c} 单 ({d}%)'
    },
    title: {
      text: `${staffOrders.value.length}`,
      subtext: `服务中 ${servingCount} 单`,
      left: 'center',
      top: '38%',
      textStyle: {
        fontSize: 28,
        fontWeight: 800,
        color: '#1F2937'
      },
      subtextStyle: {
        fontSize: 12,
        color: '#6B7280'
      }
    },
    series: [
      {
        type: 'pie',
        radius: ['52%', '76%'],
        center: ['50%', '46%'],
        label: {
          formatter: '{b}\n{c}单',
          fontSize: 12
        },
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 3
        },
        color: ['#2D6A4F', '#E85A3C', '#D4A853', '#0EA5E9', '#EC4899', '#F97316'],
        data: pieData,
      }
    ]
  })
}

const renderStaffScheduleChart = async () => {
  if (props.admin || data.value?.role !== 'STAFF' || !staffScheduleChartRef.value) return
  await nextTick()
  const today = new Date()
  const days = Array.from({ length: 7 }, (_, index) => {
    const current = new Date(today)
    current.setDate(today.getDate() + index)
    return current.toISOString().slice(0, 10)
  })
  const labels = days.map(day => day.slice(5))
  const counts = days.map(day => staffOrders.value.filter(order => String(order.serviceTime).slice(0, 10) === day).length)
  const doneCounts = days.map(day => staffOrders.value.filter(order => String(order.serviceTime).slice(0, 10) === day && order.orderStatus === 'COMPLETED').length)
  staffScheduleChart ??= echarts.init(staffScheduleChartRef.value)
  staffScheduleChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      top: 0,
      right: 0,
      textStyle: {
        color: '#6B7280'
      }
    },
    grid: {
      left: 12,
      right: 12,
      top: 48,
      bottom: 12,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: labels,
      axisLine: {
        lineStyle: {
          color: '#D6D3D1'
        }
      },
      axisLabel: {
        color: '#6B7280'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#6B7280'
      },
      splitLine: {
        lineStyle: {
          color: '#EEE7E1'
        }
      }
    },
    series: [
      {
        name: '排班数量',
        type: 'bar',
        barMaxWidth: 28,
        itemStyle: {
          color: '#2D6A4F',
          borderRadius: [8, 8, 0, 0]
        },
        data: counts,
      },
      {
        name: '已完成',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: { width: 3, color: '#E85A3C' },
        itemStyle: { color: '#E85A3C' },
        areaStyle: { color: 'rgba(232, 90, 60, 0.10)' },
        data: doneCounts,
      }
    ]
  })
}

const fetchStaffCharts = async () => {
  if (props.admin || data.value?.role !== 'STAFF') return
  const orderRes = await myServiceOrders({ pageNo: 1, pageSize: 100 })
  if (orderRes.success) staffOrders.value = orderRes.data.list
  renderStaffOrderChart()
  renderStaffScheduleChart()
}

const bindingStatusLabelMap: Record<string, string> = {
  ACTIVE: '已绑定',
  PENDING: '待审核',
  REJECTED: '已拒绝',
}

const renderFamilyBindingChart = async () => {
  if (props.admin || data.value?.role !== 'FAMILY' || !familyBindingChartRef.value) return
  await nextTick()
  const statusMap = familyBindings.value.reduce<Record<string, number>>((acc, item) => {
    const key = bindingStatusLabelMap[item.status] || item.status
    acc[key] = (acc[key] || 0) + 1
    return acc
  }, {})
  const orderCount = familyOrders.value.length
  const pieData = Object.entries(statusMap).map(([name, value]) => ({ name, value }))
  if (!pieData.length) {
    pieData.push(
      { name: '已绑定', value: 0 },
      { name: '待审核', value: 0 },
      { name: '已拒绝', value: 0 },
    )
  }
  familyBindingChart ??= echarts.init(familyBindingChartRef.value)
  familyBindingChart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>{c} 项 ({d}%)'
    },
    title: {
      text: `${familyBindings.value.length}`,
      subtext: `同步订单 ${orderCount}`,
      left: 'center',
      top: '38%',
      textStyle: {
        fontSize: 28,
        fontWeight: 800,
        color: '#1F2937'
      },
      subtextStyle: {
        fontSize: 12,
        color: '#6B7280'
      }
    },
    series: [
      {
        type: 'pie',
        radius: ['52%', '76%'],
        center: ['50%', '46%'],
        label: {
          formatter: '{b}\n{c}项',
          fontSize: 12
        },
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 3
        },
        color: ['#2D6A4F', '#D4A853', '#E85A3C'],
        data: pieData,
      }
    ]
  })
}

const renderFamilyTrendChart = async () => {
  if (props.admin || data.value?.role !== 'FAMILY' || !familyTrendChartRef.value) return
  await nextTick()
  const activeBindingIds = familyBindings.value
    .filter(item => item.status === 'ACTIVE')
    .map(item => item.elderUserId)
  const lastSevenDays = Array.from({ length: 7 }, (_, index) => {
    const current = new Date()
    current.setDate(current.getDate() - (6 - index))
    return current.toISOString().slice(0, 10)
  })
  const healthSeries = lastSevenDays.map(day =>
    familyRecords.value.filter(item =>
      activeBindingIds.includes(item.userId) && String(item.recordDate).slice(0, 10) === day && item.abnormalFlag === 1
    ).length
  )
  const completedSeries = lastSevenDays.map(day =>
    familyOrders.value.filter(item =>
      String(item.serviceTime).slice(0, 10) === day && item.orderStatus === 'COMPLETED'
    ).length
  )
  familyTrendChart ??= echarts.init(familyTrendChartRef.value)
  familyTrendChart.setOption({
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      top: 0,
      right: 0,
      textStyle: {
        color: '#6B7280'
      }
    },
    grid: {
      left: 12,
      right: 12,
      top: 48,
      bottom: 12,
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: lastSevenDays.map(day => day.slice(5)),
      axisLine: {
        lineStyle: {
          color: '#D6D3D1'
        }
      },
      axisLabel: {
        color: '#6B7280'
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        color: '#6B7280'
      },
      splitLine: {
        lineStyle: {
          color: '#EEE7E1'
        }
      }
    },
    series: [
      {
        name: '健康异常',
        type: 'line',
        smooth: true,
        symbolSize: 8,
        lineStyle: { width: 3, color: '#E85A3C' },
        itemStyle: { color: '#E85A3C' },
        areaStyle: { color: 'rgba(232, 90, 60, 0.12)' },
        data: healthSeries,
      },
      {
        name: '完成订单',
        type: 'bar',
        barMaxWidth: 28,
        itemStyle: {
          color: '#2D6A4F',
          borderRadius: [8, 8, 0, 0]
        },
        data: completedSeries,
      }
    ]
  })
}

const fetchFamilyCharts = async () => {
  if (props.admin || data.value?.role !== 'FAMILY') return
  const [orderRes, bindingRes] = await Promise.all([
    myServiceOrders({ pageNo: 1, pageSize: 100 }),
    myFamilyBindings({ pageNo: 1, pageSize: 100 }),
  ])
  if (orderRes.success) familyOrders.value = orderRes.data.list
  if (bindingRes.success) familyBindings.value = bindingRes.data.list

  const activeBindingIds = familyBindings.value
    .filter(item => item.status === 'ACTIVE')
    .map(item => item.elderUserId)

  const recordPromises = activeBindingIds.slice(0, 5).map(targetUserId =>
    myHealthRecords({ pageNo: 1, pageSize: 100, targetUserId })
  )
  const recordResults = await Promise.all(recordPromises)
  familyRecords.value = recordResults
    .filter(result => result.success)
    .flatMap(result => result.data.list)

  renderFamilyBindingChart()
  renderFamilyTrendChart()
}

const fetchData = async () => {
  const res = props.admin ? await getAdminDashboard() : await getMyDashboard()
  if (res.success) {
    data.value = res.data
  }
}

onMounted(async () => {
  await fetchData()
  await fetchAdminCharts()
  await fetchElderCharts()
  await fetchStaffCharts()
  await fetchFamilyCharts()
  window.addEventListener('resize', resizeCharts)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts)
  roleChart?.dispose()
  trendChartInstance?.dispose()
  elderOrderChart?.dispose()
  elderHealthChart?.dispose()
  staffOrderChart?.dispose()
  staffScheduleChart?.dispose()
  familyBindingChart?.dispose()
  familyTrendChart?.dispose()
  roleChart = null
  trendChartInstance = null
  elderOrderChart = null
  elderHealthChart = null
  staffOrderChart = null
  staffScheduleChart = null
  familyBindingChart = null
  familyTrendChart = null
})
</script>

<style scoped lang="scss">
.dashboard-board {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

// Hero Card
.hero-card {
  border: none;
  border-radius: 20px;
  background: linear-gradient(135deg, var(--app-green-dark) 0%, var(--app-green-mid) 50%, var(--app-green-light) 100%);
  color: #fff;
  overflow: hidden;
  position: relative;

  &:hover {
    transform: translateY(-2px);
    transition: transform 0.3s ease;
  }
}

.hero-head {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  position: relative;
  z-index: 1;
}

.hero-content {
  position: relative;
  z-index: 2;
}

.hero-kicker {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 12px;
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.12);
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.06em;
  backdrop-filter: blur(8px);
}

.kicker-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--app-primary-light);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.6; transform: scale(1.2); }
}

.hero-head h2 {
  margin: 0 0 10px;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.hero-desc {
  margin: 0;
  font-size: 14px;
  opacity: 0.85;
  line-height: 1.7;
  max-width: 520px;
}

.hero-decoration {
  position: absolute;
  top: -30px;
  right: -30px;
  z-index: 0;
}

.deco-ring {
  position: absolute;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.ring-1 {
  width: 160px;
  height: 160px;
  top: 0;
  right: 0;
  animation: rotate 20s linear infinite;
}

.ring-2 {
  width: 220px;
  height: 220px;
  top: -30px;
  right: -30px;
  animation: rotate 30s linear infinite reverse;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// Metric Cards
.metric-row {
  margin-bottom: 8px;
}

.chart-row {
  margin-bottom: 8px;
}

.metric-card {
  border: 1px solid var(--app-border-color);
  border-radius: 18px;
  background: var(--app-surface);
  padding: 20px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    border-radius: 18px 18px 0 0;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
  }

  &.metric-card-0::before { background: linear-gradient(90deg, var(--app-primary), var(--app-primary-light)); }
  &.metric-card-1::before { background: linear-gradient(90deg, var(--app-green-mid), var(--app-green-light)); }
  &.metric-card-2::before { background: linear-gradient(90deg, var(--app-accent), #E8C88A); }
  &.metric-card-3::before { background: linear-gradient(90deg, #EC4899, #F472B6); }
}

.metric-icon-wrap {
  margin-bottom: 14px;
}

.metric-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: var(--app-primary-bg);
  color: var(--app-primary);
}

.metric-card-1 .metric-icon { background: rgba(45, 106, 79, 0.1); color: var(--app-green-mid); }
.metric-card-2 .metric-icon { background: rgba(212, 168, 83, 0.1); color: var(--app-accent); }
.metric-card-3 .metric-icon { background: rgba(236, 72, 153, 0.1); color: #EC4899; }

.metric-label {
  color: var(--app-text-secondary);
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 28px;
  font-weight: 800;
  color: var(--app-text-primary);
  letter-spacing: -0.02em;
  margin-bottom: 6px;
}

.metric-trend {
  color: var(--app-text-muted);
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.trend-arrow {
  color: var(--app-success);
  font-weight: 700;
}

// Content Cards
.content-card {
  border: 1px solid var(--app-border-color);
  border-radius: 18px;
  background: var(--app-surface);
  transition: all 0.3s ease;

  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
  }

  :deep(.el-card__header) {
    padding: 18px 20px;
    border-bottom: 1px solid var(--app-border-color-light);
  }

  :deep(.el-card__body) {
    padding: 16px 20px;
  }
}

.chart-card {
  height: 100%;
}

.chart-host {
  width: 100%;
  height: 320px;
}

.chart-host-wide {
  height: 320px;
}

.chart-subtitle {
  font-size: 12px;
  color: var(--app-text-muted);
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 15px;
  font-weight: 700;
  color: var(--app-text-primary);

  &.warning {
    color: var(--app-danger);
  }
}

.section-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--app-primary);
  box-shadow: 0 2px 8px rgba(232, 90, 60, 0.4);

  .warning & {
    background: var(--app-danger);
    box-shadow: 0 2px 8px rgba(239, 68, 68, 0.4);
  }
}

// Modern Table
.modern-table {
  :deep(.el-table__header-wrapper th) {
    background: var(--app-surface-muted) !important;
    color: var(--app-text-secondary);
    font-weight: 600;
    font-size: 12px;
    text-transform: uppercase;
    letter-spacing: 0.06em;
    border: none !important;
  }

  :deep(.el-table__body-wrapper td) {
    border-bottom: 1px solid var(--app-border-color-light);
    font-size: 14px;
  }

  :deep(.el-table__row:hover td) {
    background: var(--app-hover-bg) !important;
  }
}

.type-badge {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 999px;
  background: var(--app-primary-bg);
  color: var(--app-primary);
  font-size: 12px;
  font-weight: 600;
}

// Mini List Items
.mini-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.mini-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-radius: 14px;
  background: var(--app-surface-muted);
  transition: all 0.25s ease;
  cursor: pointer;

  &:hover {
    background: var(--app-hover-bg);
    transform: translateX(4px);
  }
}

@media (max-width: 991px) {
  .chart-host,
  .chart-host-wide {
    height: 280px;
  }
}

.mini-item-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;

  strong {
    color: var(--app-text-primary);
    font-size: 14px;
    font-weight: 600;
  }

  span {
    color: var(--app-text-secondary);
    font-size: 13px;
  }
}

.mini-item-arrow {
  color: var(--app-text-muted);
  opacity: 0;
  transform: translateX(-8px);
  transition: all 0.25s ease;

  .mini-item:hover & {
    opacity: 1;
    transform: translateX(0);
  }
}

.alert-item {
  position: relative;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 60%;
    border-radius: 0 4px 4px 0;
    background: var(--app-danger);
  }
}

.alert-badge {
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(239, 68, 68, 0.1);
  color: var(--app-danger);
  font-size: 14px;
  font-weight: 700;
}

.binding-item {
  gap: 14px;
}

.binding-avatars {
  display: flex;
  align-items: center;
}

.binding-avatar {
  background: linear-gradient(135deg, var(--app-primary) 0%, var(--app-primary-light) 100%);
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  border: 2px solid var(--app-surface);

  &.second {
    margin-left: -10px;
    background: linear-gradient(135deg, var(--app-green-mid) 0%, var(--app-green-light) 100%);
  }
}

.bottom-row {
  margin-top: 8px;
}

// Responsive
@media (max-width: 768px) {
  .hero-head h2 {
    font-size: 22px;
  }

  .hero-decoration {
    display: none;
  }

  .metric-card {
    margin-bottom: 8px;
  }
}
</style>
