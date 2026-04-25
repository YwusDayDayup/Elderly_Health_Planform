<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">绑定管理</p><h2>申请关联老人账号</h2><span>家属可发起绑定申请，并等待老人审核授权。</span></div></div>
    <el-row :gutter="16">
      <el-col :xs="24" :lg="8">
        <el-card shadow="never">
          <template #header><span>新增绑定申请</span></template>
          <el-form :model="form" label-width="90px">
            <el-form-item label="老人账号ID"><el-input-number v-model="form.elderUserId" :min="1" /></el-form-item>
            <el-form-item label="关系"><el-input v-model="form.relationLabel" placeholder="如 女儿 / 儿子" /></el-form-item>
            <el-form-item label="健康权限"><el-switch v-model="form.healthAccess" :active-value="1" :inactive-value="0" /></el-form-item>
            <el-form-item label="位置权限"><el-switch v-model="form.locationAccess" :active-value="1" :inactive-value="0" /></el-form-item>
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
            <el-form-item><el-button type="primary" @click="submitBinding">发起申请</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="16">
        <el-card shadow="never">
          <template #header><span>我的绑定记录</span></template>
          <el-table :data="bindings">
            <el-table-column prop="elderName" label="老人" width="120" />
            <el-table-column prop="relationLabel" label="关系" width="120" />
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="180" />
          </el-table>
          <div class="page-pagination-right">
            <el-pagination
              v-model:current-page="query.pageNo"
              v-model:page-size="query.pageSize"
              :total="total"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="fetchBindings"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { createFamilyBinding, myFamilyBindings, type FamilyBindingReq, type FamilyBindingVO } from '@/api/care'
import { ElMessage } from 'element-plus'

const bindings = ref<FamilyBindingVO[]>([])
const total = ref(0)
const query = reactive({
  pageNo: 1,
  pageSize: 10,
})
const form = reactive<FamilyBindingReq>({
  elderUserId: undefined,
  relationLabel: '女儿',
  healthAccess: 1,
  locationAccess: 1,
  remark: '',
})

const fetchBindings = async () => {
  const res = await myFamilyBindings(query)
  if (res.success) {
    bindings.value = res.data.list
    total.value = res.data.total
  }
}

const handleSizeChange = () => {
  query.pageNo = 1
  fetchBindings()
}

const submitBinding = async () => {
  await createFamilyBinding(form)
  ElMessage.success('绑定申请已提交')
  fetchBindings()
}

const statusLabel = (status: string) => {
  const map: Record<string, string> = {
    ACTIVE: '已绑定',
    PENDING: '待审核',
    REJECTED: '已拒绝',
  }
  return map[status] || status
}

const statusType = (status: string) => {
  if (status === 'ACTIVE') return 'success'
  if (status === 'PENDING') return 'warning'
  if (status === 'REJECTED') return 'danger'
  return 'info'
}

onMounted(fetchBindings)
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
</style>
