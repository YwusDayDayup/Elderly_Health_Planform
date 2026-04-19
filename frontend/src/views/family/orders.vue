<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">服务同步</p><h2>老人服务订单状态</h2><span>家属可查看老人服务预约、派单与完成进度。</span></div></div>
    <el-card shadow="never">
      <el-table :data="orders">
        <el-table-column prop="orderNo" label="订单号" width="170" />
        <el-table-column prop="elderName" label="老人" width="120" />
        <el-table-column prop="serviceName" label="服务" width="140" />
        <el-table-column prop="staffName" label="服务人员" width="120" />
        <el-table-column prop="serviceTime" label="预约时间" width="180" />
        <el-table-column label="状态" width="150">
          <template #default="{ row }">
            <el-tag effect="light" :type="statusTagType(row.orderStatus)">{{ statusLabel(row.orderStatus) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { myServiceOrders, type ServiceOrderVO } from '@/api/care'

const orders = ref<ServiceOrderVO[]>([])

const fetchOrders = async () => {
  const res = await myServiceOrders({ pageNo: 1, pageSize: 100 })
  if (res.success) orders.value = res.data.list
}

const statusLabel = (status: string) => {
  const map: Record<string, string> = {
    PENDING_PAYMENT: '待支付',
    PENDING_ASSIGN: '待派单',
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
  if (status === 'COMPLETED') return 'success'
  if (status === 'IN_SERVICE' || status === 'PENDING_SERVICE') return 'warning'
  if (status === 'CANCELLED' || status === 'REFUNDED') return 'danger'
  return 'info'
}

onMounted(fetchOrders)
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
</style>
