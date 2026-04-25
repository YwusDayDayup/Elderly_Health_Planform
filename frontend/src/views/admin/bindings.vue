<template>
  <div class="app-container">
    <div class="page-head">
      <div>
        <p class="page-kicker">家属绑定</p>
        <h2>家属关系与服务人员审核</h2>
        <span>同时查看老人-家属绑定关系和服务人员档案审核状态。</span>
      </div>
    </div>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="14">
        <div class="admin-zone" style="min-height: 400px">
          <div class="admin-zone__header">
            <el-icon class="zone-icon"><Connection /></el-icon>
            <span class="zone-title">家属绑定记录</span>
            <span class="zone-meta">{{ bindings.length }} 条</span>
          </div>
          <div class="admin-zone__body admin-zone__body--flush">
            <el-table :data="bindings">
              <el-table-column prop="elderName" label="老人" width="120" />
              <el-table-column prop="familyName" label="家属" width="120" />
              <el-table-column prop="relationLabel" label="关系" width="100" />
              <el-table-column label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="bindingStatusType(row.status)">{{ bindingStatusLabel(row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" min-width="160" />
              <el-table-column label="操作" width="90">
                <template #default="{ row }">
                  <el-button link type="primary" @click="showBindingDetail(row)">详情</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="page-pagination-right">
              <el-pagination
                v-model:current-page="bindingQuery.pageNo"
                v-model:page-size="bindingQuery.pageSize"
                :total="bindingTotal"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleBindingSizeChange"
                @current-change="fetchBindings"
              />
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :lg="10">
        <div class="admin-zone" style="min-height: 400px">
          <div class="admin-zone__header">
            <el-icon class="zone-icon"><User /></el-icon>
            <span class="zone-title">服务人员审核</span>
            <span class="zone-meta">{{ staffProfiles.length }} 条</span>
          </div>
          <div class="admin-zone__body admin-zone__body--flush">
            <el-table :data="staffProfiles">
              <el-table-column prop="nickname" label="账号" width="110" />
              <el-table-column prop="realName" label="姓名" width="100" />
              <el-table-column prop="specialty" label="服务方向" min-width="130" />
              <el-table-column label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="staffAuditType(row.auditStatus)">{{ staffAuditLabel(row.auditStatus) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="160">
                <template #default="{ row }">
                  <el-button link type="primary" @click="showStaffDetail(row)">详情</el-button>
                  <el-button link type="primary" @click="audit(row.id, 'APPROVED')">通过</el-button>
                  <el-button link type="danger" @click="audit(row.id, 'REJECTED')">驳回</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="page-pagination-right">
              <el-pagination
                v-model:current-page="staffQuery.pageNo"
                v-model:page-size="staffQuery.pageSize"
                :total="staffTotal"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleStaffSizeChange"
                @current-change="fetchStaffProfiles"
              />
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-dialog v-model="bindingDetailVisible" title="家属关系详情" width="680px">
      <el-descriptions v-if="currentBinding" :column="2" border>
        <el-descriptions-item label="老人">{{ currentBinding.elderName }}</el-descriptions-item>
        <el-descriptions-item label="家属">{{ currentBinding.familyName }}</el-descriptions-item>
        <el-descriptions-item label="关系">{{ currentBinding.relationLabel }}</el-descriptions-item>
        <el-descriptions-item label="绑定状态">{{ bindingStatusLabel(currentBinding.status) }}</el-descriptions-item>
        <el-descriptions-item label="健康权限">{{ currentBinding.healthAccess === 1 ? '已授权' : '未授权' }}</el-descriptions-item>
        <el-descriptions-item label="位置权限">{{ currentBinding.locationAccess === 1 ? '已授权' : '未授权' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentBinding.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="staffDetailVisible" title="服务人员详情" width="680px">
      <el-descriptions v-if="currentStaff" :column="2" border>
        <el-descriptions-item label="账号">{{ currentStaff.nickname }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentStaff.realName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentStaff.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">{{ staffAuditLabel(currentStaff.auditStatus) }}</el-descriptions-item>
        <el-descriptions-item label="服务方向">{{ currentStaff.specialty || '-' }}</el-descriptions-item>
        <el-descriptions-item label="服务范围">{{ currentStaff.serviceRadiusKm }} 公里</el-descriptions-item>
        <el-descriptions-item label="证书编号" :span="2">{{ currentStaff.certificateNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="介绍" :span="2">{{ currentStaff.intro || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Connection, User } from '@element-plus/icons-vue'
import { auditAdminStaffProfile, listAdminFamilyBindings, listAdminStaffProfiles, type FamilyBindingVO, type StaffProfileVO } from '@/api/care'
import { ElMessage } from 'element-plus'

const bindings = ref<FamilyBindingVO[]>([])
const bindingTotal = ref(0)
const bindingQuery = reactive({
  pageNo: 1,
  pageSize: 10,
})

const staffProfiles = ref<StaffProfileVO[]>([])
const staffTotal = ref(0)
const staffQuery = reactive({
  pageNo: 1,
  pageSize: 10,
})

const bindingDetailVisible = ref(false)
const staffDetailVisible = ref(false)
const currentBinding = ref<FamilyBindingVO | null>(null)
const currentStaff = ref<StaffProfileVO | null>(null)

const fetchBindings = async () => {
  const res = await listAdminFamilyBindings(bindingQuery)
  if (res.success) {
    bindings.value = res.data.list
    bindingTotal.value = res.data.total
  }
}

const fetchStaffProfiles = async () => {
  const res = await listAdminStaffProfiles(staffQuery)
  if (res.success) {
    staffProfiles.value = res.data.list
    staffTotal.value = res.data.total
  }
}

const fetchData = async () => {
  await Promise.all([fetchBindings(), fetchStaffProfiles()])
}

const handleBindingSizeChange = () => {
  bindingQuery.pageNo = 1
  fetchBindings()
}

const handleStaffSizeChange = () => {
  staffQuery.pageNo = 1
  fetchStaffProfiles()
}

const audit = async (id: number, status: string) => {
  await auditAdminStaffProfile(id, status)
  ElMessage.success('审核状态已更新')
  fetchStaffProfiles()
}

const showBindingDetail = (row: FamilyBindingVO) => {
  currentBinding.value = row
  bindingDetailVisible.value = true
}

const showStaffDetail = (row: StaffProfileVO) => {
  currentStaff.value = row
  staffDetailVisible.value = true
}

const bindingStatusLabel = (status: string) => {
  const map: Record<string, string> = {
    ACTIVE: '已绑定',
    PENDING: '待审核',
    REJECTED: '已驳回',
  }
  return map[status] || status
}

const bindingStatusType = (status: string) => {
  if (status === 'ACTIVE') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

const staffAuditLabel = (status: string) => {
  const map: Record<string, string> = {
    APPROVED: '已通过',
    PENDING: '待审核',
    REJECTED: '已驳回',
  }
  return map[status] || status
}

const staffAuditType = (status: string) => {
  if (status === 'APPROVED') return 'success'
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

onMounted(fetchData)
</script>

<style scoped lang="scss">
// styles inherited from _common.scss via admin-zone
</style>
