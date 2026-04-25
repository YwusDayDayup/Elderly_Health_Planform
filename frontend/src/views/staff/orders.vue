<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">接单中心</p><h2>服务执行与状态更新</h2><span>服务人员可接单、开始服务并完成订单。</span></div></div>
    <el-card shadow="never">
      <el-table :data="orders">
        <el-table-column prop="orderNo" label="订单号" width="170" />
        <el-table-column prop="elderName" label="老人" width="120" />
        <el-table-column prop="serviceName" label="服务" width="140" />
        <el-table-column prop="serviceTime" label="预约时间" width="180" />
        <el-table-column prop="serviceAddress" label="地址" min-width="220" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <el-tag effect="light" :type="statusTagType(row.orderStatus)">{{ statusLabel(row.orderStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button v-if="row.orderStatus === 'PENDING_ACCEPT'" link type="primary" @click="accept(row.id)">接单</el-button>
            <el-button v-if="row.orderStatus === 'PENDING_SERVICE'" link type="warning" @click="start(row.id)">开始服务</el-button>
            <el-button v-if="row.orderStatus === 'IN_SERVICE'" link type="success" @click="complete(row.id)">完成服务</el-button>
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { acceptServiceOrder, completeServiceOrder, myServiceOrders, startServiceOrder, type ServiceOrderVO } from '@/api/care'
import { ElMessage } from 'element-plus'

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

const accept = async (id: number) => {
  await acceptServiceOrder(id)
  ElMessage.success('已接单')
  fetchOrders()
}

const start = async (id: number) => {
  await startServiceOrder(id)
  ElMessage.success('服务已开始')
  fetchOrders()
}

const complete = async (id: number) => {
  await completeServiceOrder(id)
  ElMessage.success('服务已完成')
  fetchOrders()
}

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

onMounted(fetchOrders)
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
</style>
