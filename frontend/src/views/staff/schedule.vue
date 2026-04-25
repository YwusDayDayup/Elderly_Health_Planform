<template>
  <div class="app-container staff-schedule-page">
    <div class="page-head">
      <div>
        <p class="page-kicker">日程管理</p>
        <h2>服务人员每日上门安排</h2>
        <span>按服务时间查看今日任务、待上门事项和本周服务节奏。</span>
      </div>
    </div>

    <el-row :gutter="16" class="summary-row">
      <el-col v-for="item in summaryCards" :key="item.label" :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="summary-card">
          <p class="summary-label">{{ item.label }}</p>
          <strong class="summary-value">{{ item.value }}</strong>
          <span class="summary-note">{{ item.note }}</span>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :xl="8">
        <el-card shadow="never" class="agenda-card">
          <template #header>
            <div class="card-head">
              <strong>{{ agendaTitle }}</strong>
              <el-tag effect="light" type="warning">{{ agendaOrders.length }} 项</el-tag>
            </div>
          </template>
          <div v-if="agendaOrders.length" class="agenda-list">
            <div v-for="item in agendaOrders" :key="item.id" class="agenda-item">
              <div class="agenda-time">{{ formatTime(item.serviceTime) }}</div>
              <div class="agenda-main">
                <div class="agenda-title">{{ item.serviceName }}</div>
                <div class="agenda-sub">{{ item.elderName }} · {{ item.serviceAddress }}</div>
              </div>
              <el-tag size="small" effect="light" :type="statusTagType(item.orderStatus)">{{ statusLabel(item.orderStatus) }}</el-tag>
            </div>
          </div>
          <el-empty v-else description="暂无待处理服务安排" />
        </el-card>
      </el-col>

      <el-col :xs="24" :xl="16">
        <el-card shadow="never" class="schedule-card">
          <template #header>
            <div class="card-head">
              <div>
                <strong>服务排班表</strong>
                <span>按预约时间排序，展示近期上门服务安排</span>
              </div>
            </div>
          </template>

          <el-table :data="sortedOrders" border>
            <el-table-column prop="serviceTime" label="预约时间" width="170" />
            <el-table-column prop="elderName" label="服务对象" width="110" />
            <el-table-column prop="serviceName" label="服务项目" min-width="160" />
            <el-table-column prop="serviceAddress" label="上门地点" min-width="220" show-overflow-tooltip />
            <el-table-column prop="contactName" label="联系人" width="100" />
            <el-table-column prop="contactPhone" label="联系电话" width="130" />
            <el-table-column label="状态" width="110">
              <template #default="{ row }">
                <el-tag effect="light" :type="statusTagType(row.orderStatus)">{{ statusLabel(row.orderStatus) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
            <div class="page-pagination-right">
              <el-pagination
                v-model:current-page="query.pageNo"
                v-model:page-size="query.pageSize"
                :total="total"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="fetchOrders"
              />
            </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { myServiceOrders, type ServiceOrderVO } from '@/api/care'

const orders = ref<ServiceOrderVO[]>([])
const total = ref(0)
const query = reactive({
  pageNo: 1,
  pageSize: 10,
})

const fetchOrders = async () => {
  const res = await myServiceOrders(query)
  if (res.success) {
    orders.value = res.data.list
    total.value = res.data.total
  }
}

const handleSizeChange = () => {
  query.pageNo = 1
  fetchOrders()
}

const sortedOrders = computed(() => {
  return [...orders.value].sort((a, b) => String(a.serviceTime).localeCompare(String(b.serviceTime)))
})

const pad = (value: number) => String(value).padStart(2, '0')

const toLocalDateKey = (date: Date) => {
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

const now = () => new Date()

const todayKey = computed(() => toLocalDateKey(now()))

const todayOrders = computed(() => {
  return sortedOrders.value.filter(item => String(item.serviceTime).slice(0, 10) === todayKey.value)
})

const upcomingOrders = computed(() => {
  const current = now().getTime()
  return sortedOrders.value
    .filter(item => new Date(item.serviceTime).getTime() >= current)
    .slice(0, 6)
})

const agendaOrders = computed(() => {
  return todayOrders.value.length ? todayOrders.value : upcomingOrders.value
})

const agendaTitle = computed(() => {
  return todayOrders.value.length ? '今日待办' : '最近待办'
})

const summaryCards = computed(() => {
  const pending = orders.value.filter(item => item.orderStatus === 'PENDING_SERVICE').length
  const serving = orders.value.filter(item => item.orderStatus === 'IN_SERVICE').length
  const done = orders.value.filter(item => item.orderStatus === 'COMPLETED').length
  return [
    { label: '今日安排', value: String(todayOrders.value.length), note: '按本地日期统计的当日预约服务数量' },
    { label: '待上门', value: String(pending), note: '已接单、待开始服务的任务' },
    { label: '服务中', value: String(serving), note: '当前正在执行中的上门服务' },
    { label: '已完成', value: String(done), note: '本账号已完结的服务记录' },
  ]
})

const statusLabel = (status: string) => {
  const map: Record<string, string> = {
    PENDING_ACCEPT: '待接单',
    PENDING_SERVICE: '待上门',
    IN_SERVICE: '服务中',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  }
  return map[status] || status
}

const statusTagType = (status: string) => {
  if (status === 'PENDING_ACCEPT') return 'info'
  if (status === 'PENDING_SERVICE') return 'warning'
  if (status === 'IN_SERVICE') return 'primary'
  if (status === 'COMPLETED') return 'success'
  if (status === 'CANCELLED' || status === 'REFUNDED') return 'danger'
  return 'info'
}

const formatTime = (value: string) => String(value).slice(11, 16)

onMounted(fetchOrders)
</script>

<style scoped lang="scss">
.staff-schedule-page {
  .page-head {
    margin-bottom: 20px;
  }

  .page-kicker {
    margin: 0 0 8px;
    color: var(--app-primary);
    font-size: 12px;
    font-weight: 700;
    letter-spacing: 0.12em;
  }

  h2 {
    margin: 0 0 8px;
    font-size: 28px;
    color: var(--app-text-primary);
  }

  span {
    color: var(--app-text-secondary);
    font-size: 14px;
  }
}

.summary-row {
  margin-bottom: 16px;
}

.summary-card {
  border: 1px solid rgba(38, 104, 76, 0.08);

  .summary-label {
    margin: 0 0 12px;
    color: var(--app-text-secondary);
    font-size: 13px;
  }

  .summary-value {
    display: block;
    margin-bottom: 6px;
    font-size: 28px;
    color: var(--app-text-primary);
    line-height: 1;
  }

  .summary-note {
    color: var(--app-text-secondary);
    font-size: 13px;
  }
}

.agenda-card,
.schedule-card {
  height: 100%;

  :deep(.el-card__header) {
    padding: 18px 20px;
  }
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;

  strong {
    display: block;
    margin-bottom: 4px;
    color: var(--app-text-primary);
    font-size: 16px;
  }
}

.agenda-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.agenda-item {
  display: grid;
  grid-template-columns: 56px 1fr auto;
  gap: 14px;
  align-items: center;
  padding: 14px;
  border: 1px solid rgba(38, 104, 76, 0.08);
  border-radius: 14px;
  background: linear-gradient(180deg, rgba(245, 250, 247, 0.92) 0%, rgba(255, 255, 255, 1) 100%);
}

.agenda-time {
  font-size: 18px;
  font-weight: 700;
  color: var(--app-primary);
}

.agenda-title {
  margin-bottom: 4px;
  font-size: 15px;
  font-weight: 600;
  color: var(--app-text-primary);
}

.agenda-sub {
  color: var(--app-text-secondary);
  font-size: 13px;
}
</style>
