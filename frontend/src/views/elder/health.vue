<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">健康管理</p><h2>健康档案与日常打卡</h2><span>维护老人档案、授权设置并记录关键健康指标。</span></div></div>
    <el-row :gutter="16">
      <el-col :xs="24" :lg="10">
        <el-card shadow="never">
          <template #header><span>老人档案</span></template>
          <el-form :model="profile" label-width="110px">
            <el-form-item label="真实姓名"><el-input v-model="profile.realName" /></el-form-item>
            <el-form-item label="身份证号"><el-input v-model="profile.idCard" /></el-form-item>
            <el-form-item label="紧急联系人"><el-input v-model="profile.emergencyContactName" /></el-form-item>
            <el-form-item label="联系电话"><el-input v-model="profile.emergencyContactPhone" /></el-form-item>
            <el-form-item label="住址"><el-input v-model="profile.address" /></el-form-item>
            <el-form-item label="健康备注"><el-input v-model="profile.healthNotes" type="textarea" :rows="3" /></el-form-item>
            <el-form-item label="健康授权">
              <el-switch v-model="profile.familyHealthAuthorized" :active-value="1" :inactive-value="0" />
            </el-form-item>
            <el-form-item label="位置授权">
              <el-switch v-model="profile.familyLocationAuthorized" :active-value="1" :inactive-value="0" />
            </el-form-item>
            <el-form-item><el-button type="primary" @click="saveProfile">保存档案</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="14">
        <el-card shadow="never">
          <template #header><span>健康记录</span></template>
          <el-form :inline="true" class="toolbar-form">
            <el-form-item label="体重"><el-input-number v-model="recordForm.weight" :precision="1" /></el-form-item>
            <el-form-item label="收缩压"><el-input-number v-model="recordForm.systolicBp" /></el-form-item>
            <el-form-item label="舒张压"><el-input-number v-model="recordForm.diastolicBp" /></el-form-item>
            <el-form-item label="血糖"><el-input-number v-model="recordForm.bloodSugar" :precision="1" /></el-form-item>
            <el-form-item label="体温"><el-input-number v-model="recordForm.bodyTemp" :precision="1" /></el-form-item>
            <el-form-item><el-button type="primary" @click="submitRecord">新增记录</el-button></el-form-item>
          </el-form>
          <el-table :data="records" size="small">
            <el-table-column prop="recordDate" label="日期" width="120" />
            <el-table-column prop="weight" label="体重" width="80" />
            <el-table-column label="血压" width="120">
              <template #default="{ row }">{{ row.systolicBp }}/{{ row.diastolicBp }}</template>
            </el-table-column>
            <el-table-column prop="bloodSugar" label="血糖" width="80" />
            <el-table-column prop="bodyTemp" label="体温" width="80" />
            <el-table-column label="异常" width="80">
              <template #default="{ row }"><el-tag :type="row.abnormalFlag ? 'danger' : 'success'">{{ row.abnormalFlag ? '异常' : '正常' }}</el-tag></template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { createHealthRecord, getMyElderProfile, myHealthRecords, updateMyElderProfile, type ElderProfileVO, type HealthRecordVO } from '@/api/care'
import { ElMessage } from 'element-plus'

const profile = reactive<ElderProfileVO>({
  userId: 0,
  username: '',
  nickname: '',
  phone: '',
  realName: '',
  idCard: '',
  emergencyContactName: '',
  emergencyContactPhone: '',
  address: '',
  healthNotes: '',
  familyHealthAuthorized: 1,
  familyLocationAuthorized: 0,
})
const records = ref<HealthRecordVO[]>([])
const recordForm = reactive({ weight: undefined as number | undefined, systolicBp: undefined as number | undefined, diastolicBp: undefined as number | undefined, bloodSugar: undefined as number | undefined, bodyTemp: undefined as number | undefined })

const fetchProfile = async () => {
  const res = await getMyElderProfile()
  if (res.success) Object.assign(profile, res.data)
}

const fetchRecords = async () => {
  const res = await myHealthRecords({ pageNo: 1, pageSize: 100 })
  if (res.success) records.value = res.data.list
}

const saveProfile = async () => {
  await updateMyElderProfile(profile)
  ElMessage.success('档案已保存')
}

const submitRecord = async () => {
  await createHealthRecord(recordForm)
  ElMessage.success('健康记录已保存')
  fetchRecords()
}

onMounted(() => {
  fetchProfile()
  fetchRecords()
})
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
.toolbar-form { margin-bottom: 16px; }
</style>
