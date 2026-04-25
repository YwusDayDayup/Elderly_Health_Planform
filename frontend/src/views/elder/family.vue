<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">家属授权</p><h2>绑定申请审核与授权管理</h2><span>查看家属申请，决定是否开放健康和位置协同能力。</span></div></div>
    <el-card shadow="never">
      <el-table :data="bindings">
        <el-table-column prop="familyName" label="家属" width="120" />
        <el-table-column prop="relationLabel" label="关系" width="120" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="200" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" link type="primary" @click="audit(row.id, true)">通过</el-button>
            <el-button v-if="row.status === 'PENDING'" link type="danger" @click="audit(row.id, false)">拒绝</el-button>
            <el-button link type="danger" @click="remove(row.id)">解绑</el-button>
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
          @current-change="fetchBindings"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { auditFamilyBinding, deleteFamilyBinding, myFamilyBindings, type FamilyBindingVO } from '@/api/care'
import { ElMessage } from 'element-plus'

const bindings = ref<FamilyBindingVO[]>([])
const total = ref(0)
const query = reactive({
  pageNo: 1,
  pageSize: 10,
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

const audit = async (id: number, approved: boolean) => {
  await auditFamilyBinding(id, approved)
  ElMessage.success('审核完成')
  fetchBindings()
}

const remove = async (id: number) => {
  await deleteFamilyBinding(id)
  ElMessage.success('已解绑')
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
  if (status === 'REJECTED') return 'danger'
  return 'warning'
}

onMounted(fetchBindings)
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
</style>
