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
            <el-table-column prop="status" label="状态" width="120" />
            <el-table-column prop="remark" label="备注" min-width="180" />
          </el-table>
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
const form = reactive<FamilyBindingReq>({
  elderUserId: undefined,
  relationLabel: '女儿',
  healthAccess: 1,
  locationAccess: 1,
  remark: '',
})

const fetchBindings = async () => {
  const res = await myFamilyBindings({ pageNo: 1, pageSize: 100 })
  if (res.success) bindings.value = res.data.list
}

const submitBinding = async () => {
  await createFamilyBinding(form)
  ElMessage.success('绑定申请已提交')
  fetchBindings()
}

onMounted(fetchBindings)
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
</style>
