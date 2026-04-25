<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">我的订单</p><h2>服务预约进度与支付</h2><span>跟踪预约、支付、服务中与完成后的评价流程。</span></div></div>
    <el-card shadow="never">
      <el-table :data="orders">
        <el-table-column prop="orderNo" label="订单号" width="170" />
        <el-table-column prop="serviceName" label="服务" width="140" />
        <el-table-column prop="serviceTime" label="预约时间" width="180" />
        <el-table-column prop="staffName" label="服务人员" width="120" />
        <el-table-column label="支付" width="100">
          <template #default="{ row }">
            <el-tag effect="light" :type="payTagType(row.payStatus)">{{ payLabel(row.payStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="160">
          <template #default="{ row }">
            <el-tag effect="light" :type="statusTagType(row.orderStatus)">{{ statusLabel(row.orderStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button v-if="row.payStatus === 'UNPAID'" link type="primary" @click="handlePay(row.id)">支付</el-button>
            <el-button v-if="row.orderStatus !== 'COMPLETED' && row.orderStatus !== 'REFUNDED'" link type="danger" @click="handleCancel(row.id)">取消</el-button>
            <el-button v-if="row.orderStatus === 'COMPLETED' && !row.rating" link type="success" @click="openRate(row)">评价</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="page-pagination-right">
        <el-pagination
          v-model:current-page="query.pageNo"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="fetchOrders"
        />
      </div>
    </el-card>

    <el-dialog v-model="rateVisible" title="服务评价" width="420px">
      <el-form :model="rateForm" label-width="80px">
        <el-form-item label="评分"><el-rate v-model="rateForm.rating" /></el-form-item>
        <el-form-item label="评价"><el-input v-model="rateForm.ratingContent" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rateVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRate">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { cancelServiceOrder, myServiceOrders, payServiceOrder, rateServiceOrder, type ServiceOrderVO } from '@/api/care'
import { ElMessage } from 'element-plus'

const orders = ref<ServiceOrderVO[]>([])
const total = ref(0)
const query = reactive({ pageNo: 1, pageSize: 10 })
const rateVisible = ref(false)
const currentId = ref<number>()
const rateForm = reactive({ rating: 5, ratingContent: '' })

const fetchOrders = async () => {
  const res = await myServiceOrders(query)
  if (res.success) {
    orders.value = res.data.list
    total.value = res.data.total
  }
}

const handleSizeChange = (size: number) => {
  query.pageSize = size
  fetchOrders()
}

const handlePay = async (id: number) => {
  await payServiceOrder(id)
  ElMessage.success('支付成功')
  fetchOrders()
}

const handleCancel = async (id: number) => {
  await cancelServiceOrder(id)
  ElMessage.success('订单已取消')
  fetchOrders()
}

const openRate = (row: ServiceOrderVO) => {
  currentId.value = row.id
  rateForm.rating = 5
  rateForm.ratingContent = ''
  rateVisible.value = true
}

const submitRate = async () => {
  if (!currentId.value) return
  await rateServiceOrder(currentId.value, rateForm)
  ElMessage.success('评价成功')
  rateVisible.value = false
  fetchOrders()
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

const payLabel = (status: string) => {
  const map: Record<string, string> = {
    UNPAID: '未支付',
    PAID: '已支付',
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

const payTagType = (status: string) => {
  if (status === 'PAID') return 'success'
  if (status === 'REFUNDED') return 'danger'
  return 'warning'
}

onMounted(fetchOrders)
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
</style>
