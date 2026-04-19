<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">服务档案</p><h2>服务人员身份与资质信息</h2><span>维护服务范围、证书编号和个人介绍。</span></div></div>
    <el-card shadow="never">
      <el-form :model="profile" label-width="110px" style="max-width: 720px">
        <el-form-item label="真实姓名"><el-input v-model="profile.realName" /></el-form-item>
        <el-form-item label="证书编号"><el-input v-model="profile.certificateNo" /></el-form-item>
        <el-form-item label="服务方向"><el-input v-model="profile.specialty" /></el-form-item>
        <el-form-item label="服务半径"><el-input-number v-model="profile.serviceRadiusKm" :min="1" /></el-form-item>
        <el-form-item label="个人介绍"><el-input v-model="profile.intro" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="审核状态"><el-tag>{{ profile.auditStatus }}</el-tag></el-form-item>
        <el-form-item><el-button type="primary" @click="saveProfile">保存资料</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive } from 'vue'
import { getMyStaffProfile, updateMyStaffProfile, type StaffProfileVO } from '@/api/care'
import { ElMessage } from 'element-plus'

const profile = reactive<StaffProfileVO>({
  userId: 0,
  username: '',
  nickname: '',
  phone: '',
  realName: '',
  certificateNo: '',
  specialty: '',
  serviceRadiusKm: 5,
  intro: '',
  auditStatus: 'PENDING',
})

const fetchProfile = async () => {
  const res = await getMyStaffProfile()
  if (res.success) Object.assign(profile, res.data)
}

const saveProfile = async () => {
  await updateMyStaffProfile(profile)
  ElMessage.success('资料已保存')
}

onMounted(fetchProfile)
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
</style>
