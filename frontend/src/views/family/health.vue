<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">健康监测</p><h2>查看老人健康记录</h2><span>在老人授权后，家属可查看体征记录和异常变化。</span></div></div>
    <el-card shadow="never">
      <el-form :inline="true" class="toolbar-form">
        <el-form-item label="选择绑定老人">
          <el-select v-model="targetUserId" placeholder="请选择老人" style="width: 220px" @change="fetchRecords">
            <el-option v-for="item in activeBindings" :key="item.elderUserId" :label="item.elderName" :value="item.elderUserId" />
          </el-select>
        </el-form-item>
      </el-form>
      <el-table :data="records">
        <el-table-column prop="recordDate" label="日期" width="120" />
        <el-table-column prop="weight" label="体重" width="80" />
        <el-table-column label="血压" width="120">
          <template #default="{ row }">{{ row.systolicBp }}/{{ row.diastolicBp }}</template>
        </el-table-column>
        <el-table-column prop="bloodSugar" label="血糖" width="80" />
        <el-table-column prop="bodyTemp" label="体温" width="80" />
        <el-table-column prop="note" label="备注" min-width="180" />
        <el-table-column label="异常" width="80">
          <template #default="{ row }"><el-tag :type="row.abnormalFlag ? 'danger' : 'success'">{{ row.abnormalFlag ? '异常' : '正常' }}</el-tag></template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { myFamilyBindings, myHealthRecords, type FamilyBindingVO, type HealthRecordVO } from '@/api/care'

const bindings = ref<FamilyBindingVO[]>([])
const records = ref<HealthRecordVO[]>([])
const targetUserId = ref<number>()

const activeBindings = computed(() => bindings.value.filter(item => item.status === 'ACTIVE'))

const fetchBindings = async () => {
  const res = await myFamilyBindings({ pageNo: 1, pageSize: 100 })
  if (res.success) {
    bindings.value = res.data.list
    if (!targetUserId.value && activeBindings.value.length) {
      targetUserId.value = activeBindings.value[0]!.elderUserId
      fetchRecords()
    }
  }
}

const fetchRecords = async () => {
  if (!targetUserId.value) return
  const res = await myHealthRecords({ pageNo: 1, pageSize: 100, targetUserId: targetUserId.value })
  if (res.success) records.value = res.data.list
}

onMounted(fetchBindings)
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
.toolbar-form { margin-bottom: 16px; }
</style>
