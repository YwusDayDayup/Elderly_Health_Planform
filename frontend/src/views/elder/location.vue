<template>
  <div class="app-container">
    <div class="page-head"><div><p class="page-kicker">位置安全</p><h2>安全区域与位置上报</h2><span>设置电子围栏半径，并模拟上报当前位置。</span></div></div>
    <el-row :gutter="16">
      <el-col :xs="24" :lg="10">
        <el-card shadow="never">
          <template #header><span>安全区域</span></template>
          <el-form :model="zone" label-width="100px">
            <el-form-item label="中心纬度"><el-input-number v-model="zone.centerLatitude" :precision="6" /></el-form-item>
            <el-form-item label="中心经度"><el-input-number v-model="zone.centerLongitude" :precision="6" /></el-form-item>
            <el-form-item label="半径(米)"><el-input-number v-model="zone.radiusMeters" :min="100" /></el-form-item>
            <el-form-item label="地址"><el-input v-model="zone.address" /></el-form-item>
            <el-form-item><el-button type="primary" @click="saveZone">保存安全区域</el-button></el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="14">
        <el-card shadow="never">
          <template #header><span>位置上报</span></template>
          <el-form :model="locationForm" :inline="true" class="toolbar-form">
            <el-form-item label="纬度"><el-input-number v-model="locationForm.latitude" :precision="6" /></el-form-item>
            <el-form-item label="经度"><el-input-number v-model="locationForm.longitude" :precision="6" /></el-form-item>
            <el-form-item label="地址"><el-input v-model="locationForm.address" /></el-form-item>
            <el-form-item><el-button type="primary" @click="submitLocation">上报位置</el-button></el-form-item>
          </el-form>
          <el-descriptions v-if="latestLocation" :column="1" border>
            <el-descriptions-item label="地址">{{ latestLocation.address }}</el-descriptions-item>
            <el-descriptions-item label="坐标">{{ latestLocation.latitude }}, {{ latestLocation.longitude }}</el-descriptions-item>
            <el-descriptions-item label="围栏状态">{{ latestLocation.withinZone === 1 ? '区域内' : '已越界' }}</el-descriptions-item>
            <el-descriptions-item label="上报时间">{{ latestLocation.createTime }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getLatestLocation, getSafeZone, reportLocation, updateSafeZone, type LocationVO, type SafeZoneVO } from '@/api/care'
import { ElMessage } from 'element-plus'

const zone = reactive<SafeZoneVO>({
  userId: 0,
  centerLatitude: 31.2304,
  centerLongitude: 121.4737,
  radiusMeters: 500,
  address: '幸福社区',
})
const latestLocation = ref<LocationVO | null>(null)
const locationForm = reactive({ latitude: 31.2304, longitude: 121.4737, address: '幸福社区 2 栋 301' })

const fetchZone = async () => {
  const res = await getSafeZone()
  if (res.success && res.data) Object.assign(zone, res.data)
}

const fetchLatest = async () => {
  const res = await getLatestLocation()
  if (res.success) latestLocation.value = res.data
}

const saveZone = async () => {
  await updateSafeZone(zone)
  ElMessage.success('安全区域已保存')
}

const submitLocation = async () => {
  await reportLocation(locationForm)
  ElMessage.success('位置已上报')
  fetchLatest()
}

onMounted(() => {
  fetchZone()
  fetchLatest()
})
</script>

<style scoped lang="scss">
.page-head { margin-bottom: 20px; }
.page-kicker { margin: 0 0 8px; color: var(--app-primary); font-size: 12px; font-weight: 700; letter-spacing: .12em; }
h2 { margin: 0 0 8px; font-size: 28px; color: var(--app-text-primary); }
span { color: var(--app-text-secondary); font-size: 14px; }
.toolbar-form { margin-bottom: 16px; }
</style>
