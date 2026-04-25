<template>
  <div class="app-container">
    <div class="page-head">
      <div>
        <p class="page-kicker">健康预警</p>
        <h2>老人健康异常监控</h2>
        <span>集中查看系统生成的健康与位置预警事件。</span>
      </div>
    </div>
    <div class="admin-zone">
      <div class="admin-zone__header">
        <el-icon class="zone-icon"><Warning /></el-icon>
        <span class="zone-title">预警列表</span>
        <span class="zone-meta">{{ alerts.length }} 条记录</span>
      </div>
      <div class="admin-zone__toolbar">
        <el-form :inline="true">
          <el-form-item label="处理状态">
            <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 140px">
              <el-option label="待处理" value="OPEN" />
              <el-option label="已处理" value="CLOSED" />
            </el-select>
          </el-form-item>
          <el-form-item><el-button type="primary" @click="fetchAlerts">查询</el-button></el-form-item>
        </el-form>
      </div>
      <div class="admin-zone__body admin-zone__body--flush">
        <el-table :data="alerts">
          <el-table-column prop="username" label="用户" width="120" />
          <el-table-column label="预警类型" width="100">
            <template #default="{ row }">
              {{ alertTypeLabel(row.alertType) }}
            </template>
          </el-table-column>
          <el-table-column label="等级" width="100">
            <template #default="{ row }">
              <el-tag :type="levelType(row.alertLevel)">{{ alertLevelLabel(row.alertLevel) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="content" label="内容" min-width="200" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'OPEN' ? 'danger' : 'success'">{{ row.status === 'OPEN' ? '待处理' : '已处理' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="时间" width="180" />
          <el-table-column label="操作" width="90">
            <template #default="{ row }">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
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
            @current-change="fetchAlerts"
          />
        </div>
      </div>
    </div>

    <el-dialog v-model="detailVisible" title="预警详情" width="680px">
      <el-descriptions v-if="currentAlert" :column="2" border>
        <el-descriptions-item label="用户">{{ currentAlert.nickname }}</el-descriptions-item>
        <el-descriptions-item label="账号">{{ currentAlert.username }}</el-descriptions-item>
        <el-descriptions-item label="预警类型">{{ alertTypeLabel(currentAlert.alertType) }}</el-descriptions-item>
        <el-descriptions-item label="预警等级">{{ alertLevelLabel(currentAlert.alertLevel) }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">{{ currentAlert.status === 'OPEN' ? '待处理' : '已处理' }}</el-descriptions-item>
        <el-descriptions-item label="触发时间">{{ currentAlert.createTime }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ currentAlert.title }}</el-descriptions-item>
        <el-descriptions-item label="详情" :span="2">{{ currentAlert.content }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Warning } from '@element-plus/icons-vue'
import { listAdminHealthAlerts, type HealthAlertVO } from '@/api/care'

const alerts = ref<HealthAlertVO[]>([])
const total = ref(0)
const query = reactive({ status: '', pageNo: 1, pageSize: 10 })
const detailVisible = ref(false)
const currentAlert = ref<HealthAlertVO | null>(null)

const fetchAlerts = async () => {
  const res = await listAdminHealthAlerts(query)
  if (res.success) {
    alerts.value = res.data.list
    total.value = res.data.total
  }
}

const handleSizeChange = (size: number) => {
  query.pageSize = size
  fetchAlerts()
}

const showDetail = (row: HealthAlertVO) => {
  currentAlert.value = row
  detailVisible.value = true
}

const alertTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    HEALTH: '健康异常',
    LOCATION: '位置越界',
  }
  return map[type] || type
}

const alertLevelLabel = (level: string) => {
  const map: Record<string, string> = {
    LOW: '低',
    MEDIUM: '中',
    HIGH: '高',
  }
  return map[level] || level
}

const levelType = (level: string) => {
  if (level === 'HIGH') return 'danger'
  if (level === 'MEDIUM') return 'warning'
  return 'info'
}

onMounted(fetchAlerts)
</script>

<style scoped lang="scss">
// styles inherited from _common.scss via admin-zone
</style>
