<template>
  <div class="app-container">
    <div class="page-head">
      <p class="page-kicker">订单调度</p>
      <h2>服务订单分配与状态追踪</h2>
      <span>管理员可统一查看所有订单并分配服务人员。</span>
    </div>

    <!-- 搜索工具栏 -->
    <div class="admin-zone">
      <div class="admin-zone__toolbar">
        <el-form :inline="true">
          <el-form-item label="关键字"><el-input v-model="query.keyword" placeholder="服务名称" clearable /></el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 160px">
              <el-option label="待支付" value="PENDING_PAYMENT" />
              <el-option label="待派单" value="PENDING_ASSIGN" />
              <el-option label="待接单" value="PENDING_ACCEPT" />
              <el-option label="待上门" value="PENDING_SERVICE" />
              <el-option label="服务中" value="IN_SERVICE" />
              <el-option label="已完成" value="COMPLETED" />
              <el-option label="已取消" value="CANCELLED" />
              <el-option label="已退款" value="REFUNDED" />
            </el-select>
          </el-form-item>
          <el-form-item><el-button type="primary" @click="fetchOrders">查询</el-button></el-form-item>
        </el-form>
      </div>
      <div class="admin-zone__body admin-zone__body--flush">
        <el-table :data="orders">
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="serviceName" label="服务" width="140" />
          <el-table-column prop="elderName" label="老人" width="120" />
          <el-table-column prop="staffName" label="服务人员" width="120" />
          <el-table-column prop="serviceTime" label="预约时间" width="180" />
          <el-table-column label="订单状态" width="120">
            <template #default="{ row }">
              <el-tag :type="orderStatusType(row.orderStatus)">{{ orderStatusLabel(row.orderStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="支付状态" width="100">
            <template #default="{ row }">
              <el-tag :type="payStatusType(row.payStatus)">{{ payStatusLabel(row.payStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
              <el-button link type="primary" @click="openAssign(row)">派单</el-button>
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
      </div>
    </div>

    <el-dialog v-model="assignVisible" title="分配服务人员" width="420px">
      <el-form label-width="90px">
        <el-form-item label="订单号">{{ currentOrder?.orderNo }}</el-form-item>
        <el-form-item label="服务人员">
          <el-select v-model="assignStaffUserId" style="width: 100%">
            <el-option v-for="item in staffOptions" :key="item.id" :label="item.nickname || item.username" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确认派单</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="订单详情" width="760px">
      <el-descriptions v-if="currentOrder" :column="2" border>
        <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="服务名称">{{ currentOrder.serviceName }}</el-descriptions-item>
        <el-descriptions-item label="老人姓名">{{ currentOrder.elderName }}</el-descriptions-item>
        <el-descriptions-item label="服务人员">{{ currentOrder.staffName || '待派单' }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">{{ orderStatusLabel(currentOrder.orderStatus) }}</el-descriptions-item>
        <el-descriptions-item label="支付状态">{{ payStatusLabel(currentOrder.payStatus) }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ currentOrder.serviceTime }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentOrder.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="服务地址" :span="2">{{ currentOrder.serviceAddress }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价" :span="2">{{ currentOrder.ratingContent || '暂无评价' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { assignAdminServiceOrder, listAdminServiceOrders, type ServiceOrderVO } from '@/api/care'
import { adminUserPage, type AdminUserVO } from '@/api/user'
import { ElMessage } from 'element-plus'

const query = reactive({ keyword: '', status: '', pageNo: 1, pageSize: 10 })
const orders = ref<ServiceOrderVO[]>([])
const total = ref(0)
const staffOptions = ref<AdminUserVO[]>([])
const assignVisible = ref(false)
const detailVisible = ref(false)
const currentOrder = ref<ServiceOrderVO | null>(null)
const assignStaffUserId = ref<number>()

const fetchOrders = async () => {
  const res = await listAdminServiceOrders(query)
  if (res.success) {
    orders.value = res.data.list
    total.value = res.data.total
  }
}

const handleSizeChange = (size: number) => {
  query.pageSize = size
  fetchOrders()
}

const fetchStaffUsers = async () => {
  const res = await adminUserPage({ pageNo: 1, pageSize: 100, role: 'STAFF' })
  if (res.success) staffOptions.value = res.data.list
}

const openAssign = (row: ServiceOrderVO) => {
  currentOrder.value = row
  assignStaffUserId.value = row.staffUserId || staffOptions.value[0]?.id
  assignVisible.value = true
}

const showDetail = (row: ServiceOrderVO) => {
  currentOrder.value = row
  detailVisible.value = true
}

const submitAssign = async () => {
  if (!currentOrder.value || !assignStaffUserId.value) return
  await assignAdminServiceOrder(currentOrder.value.id, assignStaffUserId.value)
  ElMessage.success('派单成功')
  assignVisible.value = false
  fetchOrders()
}

const orderStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    PENDING_PAYMENT: '待支付',
    PENDING_ASSIGN: '待派单',
    PENDING_ACCEPT: '待接单',
    PENDING_SERVICE: '待上门',
    IN_SERVICE: '服务中',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REFUNDED: '已退款',
  }
  return map[status] || status
}

const payStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    UNPAID: '未支付',
    PAID: '已支付',
    REFUNDED: '已退款',
  }
  return map[status] || status
}

const orderStatusType = (status: string) => {
  if (status === 'COMPLETED') return 'success'
  if (status === 'IN_SERVICE' || status === 'PENDING_SERVICE') return 'warning'
  if (status === 'CANCELLED' || status === 'REFUNDED') return 'danger'
  return 'info'
}

const payStatusType = (status: string) => {
  if (status === 'PAID') return 'success'
  if (status === 'REFUNDED') return 'danger'
  return 'warning'
}

onMounted(() => {
  fetchOrders()
  fetchStaffUsers()
})
</script>

<style scoped lang="scss">
// styles inherited from _common.scss via admin-zone
</style>
